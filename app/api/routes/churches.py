from typing import Optional
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func
import json

from app.database import get_db
from app.models.church import Church
from app.models.user import User
from app.schemas.church import (
    ChurchCreate, ChurchUpdate, ChurchResponse,
    ChurchListResponse, ChurchListItem
)
from app.schemas.common import ApiResponse, Coordinates
from app.core.security import get_current_user

router = APIRouter()


def parse_photos(photos):
    """Parse photos from JSON string or list"""
    if isinstance(photos, str):
        try:
            return json.loads(photos)
        except:
            return []
    return photos or []


@router.get("", response_model=ApiResponse[ChurchListResponse])
async def get_churches(
    religion: Optional[str] = None,
    city: Optional[str] = None,
    search: Optional[str] = None,
    page: int = Query(1, ge=1),
    per_page: int = Query(20, ge=1, le=100),
    db: AsyncSession = Depends(get_db)
):
    """Get list of churches with filters"""
    query = select(Church).where(Church.is_active == True)

    if religion:
        query = query.where(Church.religion == religion)
    if city:
        query = query.where(Church.city.ilike(f"%{city}%"))
    if search:
        query = query.where(Church.name.ilike(f"%{search}%"))

    # Count total
    count_query = select(func.count()).select_from(query.subquery())
    total_result = await db.execute(count_query)
    total = total_result.scalar()

    # Paginate
    query = query.offset((page - 1) * per_page).limit(per_page)
    result = await db.execute(query)
    churches = result.scalars().all()

    church_items = []
    for c in churches:
        photos = parse_photos(c.photos)
        church_items.append(ChurchListItem(
            id=c.id,
            name=c.name,
            religion=c.religion,
            address=c.address,
            rating=c.rating,
            photo=photos[0] if photos else None,
            coordinates=Coordinates(lat=c.latitude, lng=c.longitude)
        ))

    return ApiResponse.success_response(
        ChurchListResponse(churches=church_items, total=total)
    )


@router.get("/nearby", response_model=ApiResponse[ChurchListResponse])
async def get_nearby_churches(
    lat: float = Query(..., ge=-90, le=90),
    lng: float = Query(..., ge=-180, le=180),
    radius: int = Query(5000, ge=100, le=50000),
    religion: Optional[str] = None,
    db: AsyncSession = Depends(get_db)
):
    """Get churches near a location (radius in meters)"""
    # Approximate conversion: 1 degree latitude = 111km
    lat_delta = radius / 111000
    lng_delta = radius / 111000  # Simplified for SQLite

    query = select(Church).where(
        Church.is_active == True,
        Church.latitude.between(lat - lat_delta, lat + lat_delta),
        Church.longitude.between(lng - lng_delta, lng + lng_delta)
    )

    if religion:
        query = query.where(Church.religion == religion)

    result = await db.execute(query)
    churches = result.scalars().all()

    # Calculate actual distance and sort
    def calc_distance(church):
        from math import radians, cos, sin, asin, sqrt
        lat1, lon1 = radians(lat), radians(lng)
        lat2, lon2 = radians(church.latitude), radians(church.longitude)
        dlat = lat2 - lat1
        dlon = lon2 - lon1
        a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
        return 2 * asin(sqrt(a)) * 6371000  # meters

    churches_with_distance = [(c, calc_distance(c)) for c in churches]
    churches_with_distance.sort(key=lambda x: x[1])
    churches_with_distance = [(c, d) for c, d in churches_with_distance if d <= radius]

    church_items = []
    for c, d in churches_with_distance:
        photos = parse_photos(c.photos)
        church_items.append(ChurchListItem(
            id=c.id,
            name=c.name,
            religion=c.religion,
            distance=round(d),
            address=c.address,
            rating=c.rating,
            photo=photos[0] if photos else None,
            coordinates=Coordinates(lat=c.latitude, lng=c.longitude)
        ))

    return ApiResponse.success_response(
        ChurchListResponse(churches=church_items, total=len(church_items))
    )


@router.get("/{church_id}", response_model=ApiResponse[ChurchResponse])
async def get_church(
    church_id: str,
    db: AsyncSession = Depends(get_db)
):
    """Get church details by ID"""
    result = await db.execute(
        select(Church).where(Church.id == church_id, Church.is_active == True)
    )
    church = result.scalar_one_or_none()

    if not church:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Church not found"
        )

    return ApiResponse.success_response(ChurchResponse.model_validate(church))


@router.post("", response_model=ApiResponse[ChurchResponse])
async def create_church(
    church_data: ChurchCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Create a new church (admin only)"""
    data = church_data.model_dump()
    data['photos'] = json.dumps(data.get('photos', []))
    data['working_hours'] = json.dumps(data.get('working_hours', {}))
    data['admin_id'] = current_user.id

    church = Church(**data)
    db.add(church)
    await db.commit()
    await db.refresh(church)

    return ApiResponse.success_response(ChurchResponse.model_validate(church))


@router.patch("/{church_id}", response_model=ApiResponse[ChurchResponse])
async def update_church(
    church_id: str,
    church_data: ChurchUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Update church details"""
    result = await db.execute(
        select(Church).where(Church.id == church_id)
    )
    church = result.scalar_one_or_none()

    if not church:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Church not found"
        )

    if church.admin_id != current_user.id and current_user.role != "admin":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to update this church"
        )

    update_data = church_data.model_dump(exclude_unset=True)
    if 'photos' in update_data:
        update_data['photos'] = json.dumps(update_data['photos'])
    if 'working_hours' in update_data:
        update_data['working_hours'] = json.dumps(update_data['working_hours'])

    for field, value in update_data.items():
        setattr(church, field, value)

    await db.commit()
    await db.refresh(church)

    return ApiResponse.success_response(ChurchResponse.model_validate(church))


@router.post("/{church_id}/favorite", response_model=ApiResponse)
async def add_to_favorites(
    church_id: str,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Add church to user's favorites"""
    result = await db.execute(
        select(Church).where(Church.id == church_id, Church.is_active == True)
    )
    if not result.scalar_one_or_none():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Church not found"
        )

    favorites = current_user.favorite_churches
    if isinstance(favorites, str):
        favorites = json.loads(favorites) if favorites else []

    if church_id not in favorites:
        favorites.append(church_id)
        current_user.favorite_churches = json.dumps(favorites)
        await db.commit()

    return ApiResponse.success_response({"message": "Added to favorites"})


@router.delete("/{church_id}/favorite", response_model=ApiResponse)
async def remove_from_favorites(
    church_id: str,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Remove church from user's favorites"""
    favorites = current_user.favorite_churches
    if isinstance(favorites, str):
        favorites = json.loads(favorites) if favorites else []

    if church_id in favorites:
        favorites.remove(church_id)
        current_user.favorite_churches = json.dumps(favorites)
        await db.commit()

    return ApiResponse.success_response({"message": "Removed from favorites"})

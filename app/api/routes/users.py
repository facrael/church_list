import json
from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from app.database import get_db
from app.models.user import User
from app.models.church import Church
from app.models.booking import Booking
from app.models.payment import Payment
from app.schemas.user import UserResponse, UserUpdate
from app.schemas.church import ChurchListItem, ChurchListResponse
from app.schemas.booking import BookingResponse, BookingListResponse
from app.schemas.common import ApiResponse, Coordinates
from app.core.security import get_current_user

router = APIRouter()


def parse_favorites(favorites):
    """Parse favorites from JSON string or list"""
    if isinstance(favorites, str):
        try:
            return json.loads(favorites)
        except:
            return []
    return favorites or []


def parse_photos(photos):
    """Parse photos from JSON string or list"""
    if isinstance(photos, str):
        try:
            return json.loads(photos)
        except:
            return []
    return photos or []


@router.get("/me", response_model=ApiResponse[UserResponse])
async def get_current_user_profile(
    current_user: User = Depends(get_current_user)
):
    """Get current user's profile"""
    user_data = UserResponse.model_validate(current_user)
    user_data.favorite_churches = parse_favorites(current_user.favorite_churches)
    return ApiResponse.success_response(user_data)


@router.patch("/me", response_model=ApiResponse[UserResponse])
async def update_current_user(
    user_data: UserUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Update current user's profile"""
    update_data = user_data.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(current_user, field, value)

    await db.commit()
    await db.refresh(current_user)

    response = UserResponse.model_validate(current_user)
    response.favorite_churches = parse_favorites(current_user.favorite_churches)
    return ApiResponse.success_response(response)


@router.get("/me/favorites", response_model=ApiResponse[ChurchListResponse])
async def get_favorite_churches(
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Get user's favorite churches"""
    favorites = parse_favorites(current_user.favorite_churches)

    if not favorites:
        return ApiResponse.success_response(
            ChurchListResponse(churches=[], total=0)
        )

    result = await db.execute(
        select(Church).where(
            Church.id.in_(favorites),
            Church.is_active == True
        )
    )
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
        ChurchListResponse(churches=church_items, total=len(church_items))
    )


@router.get("/me/bookings", response_model=ApiResponse[BookingListResponse])
async def get_user_bookings(
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Get user's booking history"""
    result = await db.execute(
        select(Booking)
        .where(Booking.user_id == current_user.id)
        .order_by(Booking.created_at.desc())
    )
    bookings = result.scalars().all()

    return ApiResponse.success_response(
        BookingListResponse(
            bookings=[BookingResponse.model_validate(b) for b in bookings],
            total=len(bookings)
        )
    )


@router.get("/me/payments", response_model=ApiResponse)
async def get_user_payments(
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Get user's payment history"""
    result = await db.execute(
        select(Payment)
        .where(Payment.user_id == current_user.id)
        .order_by(Payment.created_at.desc())
    )
    payments = result.scalars().all()

    payment_list = [
        {
            "id": p.id,
            "amount": p.amount,
            "type": p.type,
            "status": p.status,
            "created_at": p.created_at.isoformat()
        }
        for p in payments
    ]

    return ApiResponse.success_response({
        "payments": payment_list,
        "total": len(payment_list)
    })

from typing import Optional
from uuid import UUID
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func

from app.database import get_db
from app.models.service import Service
from app.models.church import Church
from app.models.user import User
from app.schemas.service import (
    ServiceCreate, ServiceUpdate, ServiceResponse, ServiceListResponse
)
from app.schemas.common import ApiResponse
from app.core.security import get_current_user

router = APIRouter()


@router.get("", response_model=ApiResponse[ServiceListResponse])
async def get_services(
    church_id: Optional[UUID] = None,
    category: Optional[str] = None,
    available_only: bool = True,
    page: int = Query(1, ge=1),
    per_page: int = Query(20, ge=1, le=100),
    db: AsyncSession = Depends(get_db)
):
    """Get list of services with filters"""
    query = select(Service)

    if church_id:
        query = query.where(Service.church_id == church_id)
    if category:
        query = query.where(Service.category == category)
    if available_only:
        query = query.where(Service.is_available == True)

    # Count total
    count_query = select(func.count()).select_from(query.subquery())
    total_result = await db.execute(count_query)
    total = total_result.scalar()

    # Paginate
    query = query.offset((page - 1) * per_page).limit(per_page)
    result = await db.execute(query)
    services = result.scalars().all()

    return ApiResponse.success_response(
        ServiceListResponse(
            services=[ServiceResponse.model_validate(s) for s in services],
            total=total
        )
    )


@router.get("/categories", response_model=ApiResponse)
async def get_service_categories(
    db: AsyncSession = Depends(get_db)
):
    """Get all unique service categories"""
    result = await db.execute(
        select(Service.category).distinct()
    )
    categories = [row[0] for row in result.all()]

    return ApiResponse.success_response({"categories": categories})


@router.get("/{service_id}", response_model=ApiResponse[ServiceResponse])
async def get_service(
    service_id: UUID,
    db: AsyncSession = Depends(get_db)
):
    """Get service details by ID"""
    result = await db.execute(
        select(Service).where(Service.id == service_id)
    )
    service = result.scalar_one_or_none()

    if not service:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Service not found"
        )

    return ApiResponse.success_response(ServiceResponse.model_validate(service))


@router.post("", response_model=ApiResponse[ServiceResponse])
async def create_service(
    service_data: ServiceCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Create a new service for a church"""
    # Verify church exists and user has permission
    result = await db.execute(
        select(Church).where(Church.id == service_data.church_id)
    )
    church = result.scalar_one_or_none()

    if not church:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Church not found"
        )

    if church.admin_id != current_user.id and current_user.role.value != "admin":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to add services to this church"
        )

    service = Service(**service_data.model_dump())
    db.add(service)
    await db.commit()
    await db.refresh(service)

    return ApiResponse.success_response(ServiceResponse.model_validate(service))


@router.patch("/{service_id}", response_model=ApiResponse[ServiceResponse])
async def update_service(
    service_id: UUID,
    service_data: ServiceUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Update service details"""
    result = await db.execute(
        select(Service).where(Service.id == service_id)
    )
    service = result.scalar_one_or_none()

    if not service:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Service not found"
        )

    # Check permission
    result = await db.execute(
        select(Church).where(Church.id == service.church_id)
    )
    church = result.scalar_one_or_none()

    if church.admin_id != current_user.id and current_user.role.value != "admin":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to update this service"
        )

    update_data = service_data.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(service, field, value)

    await db.commit()
    await db.refresh(service)

    return ApiResponse.success_response(ServiceResponse.model_validate(service))


@router.delete("/{service_id}", response_model=ApiResponse)
async def delete_service(
    service_id: UUID,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Delete a service (soft delete by setting is_available=False)"""
    result = await db.execute(
        select(Service).where(Service.id == service_id)
    )
    service = result.scalar_one_or_none()

    if not service:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Service not found"
        )

    # Check permission
    result = await db.execute(
        select(Church).where(Church.id == service.church_id)
    )
    church = result.scalar_one_or_none()

    if church.admin_id != current_user.id and current_user.role.value != "admin":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to delete this service"
        )

    service.is_available = False
    await db.commit()

    return ApiResponse.success_response({"message": "Service deleted"})

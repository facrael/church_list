from typing import Optional
from datetime import datetime, timedelta
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func

from app.database import get_db
from app.models.booking import Booking, BookingStatus, PaymentStatus
from app.models.service import Service
from app.models.user import User
from app.schemas.booking import (
    BookingCreate, BookingUpdate, BookingResponse,
    BookingListResponse, BookingWithPaymentUrl
)
from app.schemas.common import ApiResponse
from app.core.security import get_current_user
from app.config import settings

router = APIRouter()


@router.get("", response_model=ApiResponse[BookingListResponse])
async def get_my_bookings(
    status_filter: Optional[str] = None,
    page: int = Query(1, ge=1),
    per_page: int = Query(20, ge=1, le=100),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Get current user's bookings"""
    query = select(Booking).where(Booking.user_id == current_user.id)

    if status_filter:
        query = query.where(Booking.status == status_filter)

    query = query.order_by(Booking.created_at.desc())

    count_query = select(func.count()).select_from(query.subquery())
    total_result = await db.execute(count_query)
    total = total_result.scalar()

    query = query.offset((page - 1) * per_page).limit(per_page)
    result = await db.execute(query)
    bookings = result.scalars().all()

    return ApiResponse.success_response(
        BookingListResponse(
            bookings=[BookingResponse.model_validate(b) for b in bookings],
            total=total
        )
    )


@router.get("/{booking_id}", response_model=ApiResponse[BookingResponse])
async def get_booking(
    booking_id: str,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Get booking details by ID"""
    result = await db.execute(
        select(Booking).where(
            Booking.id == booking_id,
            Booking.user_id == current_user.id
        )
    )
    booking = result.scalar_one_or_none()

    if not booking:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Booking not found"
        )

    return ApiResponse.success_response(BookingResponse.model_validate(booking))


@router.post("", response_model=ApiResponse[BookingWithPaymentUrl])
async def create_booking(
    booking_data: BookingCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Create a new booking"""
    result = await db.execute(
        select(Service).where(
            Service.id == booking_data.service_id,
            Service.is_available == True
        )
    )
    service = result.scalar_one_or_none()

    if not service:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Service not found or unavailable"
        )

    if service.church_id != booking_data.church_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Service does not belong to the specified church"
        )

    now = datetime.utcnow()
    if booking_data.booking_date < now:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Booking date cannot be in the past"
        )

    max_date = now + timedelta(days=service.max_advance_days)
    if booking_data.booking_date > max_date:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Booking date cannot be more than {service.max_advance_days} days in advance"
        )

    commission = service.price * settings.SERVICE_COMMISSION_RATE

    booking = Booking(
        user_id=current_user.id,
        service_id=booking_data.service_id,
        church_id=booking_data.church_id,
        booking_date=booking_data.booking_date,
        payment_amount=service.price,
        commission_amount=commission,
        contact_phone=booking_data.contact_phone or current_user.phone,
        notes=booking_data.notes,
        names=booking_data.names
    )
    db.add(booking)
    await db.commit()
    await db.refresh(booking)

    payment_url = f"https://yookassa.ru/checkout/mock-{booking.id}"
    expires_at = now + timedelta(hours=1)

    return ApiResponse.success_response(
        BookingWithPaymentUrl(
            booking_id=booking.id,
            status=booking.status,
            payment_url=payment_url,
            expires_at=expires_at
        )
    )


@router.patch("/{booking_id}", response_model=ApiResponse[BookingResponse])
async def update_booking(
    booking_id: str,
    booking_data: BookingUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Update booking (cancel or update notes)"""
    result = await db.execute(
        select(Booking).where(
            Booking.id == booking_id,
            Booking.user_id == current_user.id
        )
    )
    booking = result.scalar_one_or_none()

    if not booking:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Booking not found"
        )

    if booking_data.status == BookingStatus.CANCELLED.value:
        if booking.status == BookingStatus.COMPLETED.value:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Cannot cancel completed booking"
            )
        booking.status = BookingStatus.CANCELLED.value

    if booking_data.notes:
        booking.notes = booking_data.notes

    await db.commit()
    await db.refresh(booking)

    return ApiResponse.success_response(BookingResponse.model_validate(booking))


@router.delete("/{booking_id}", response_model=ApiResponse)
async def cancel_booking(
    booking_id: str,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Cancel a booking"""
    result = await db.execute(
        select(Booking).where(
            Booking.id == booking_id,
            Booking.user_id == current_user.id
        )
    )
    booking = result.scalar_one_or_none()

    if not booking:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Booking not found"
        )

    if booking.status == BookingStatus.COMPLETED.value:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Cannot cancel completed booking"
        )

    booking.status = BookingStatus.CANCELLED.value
    await db.commit()

    return ApiResponse.success_response({"message": "Booking cancelled"})

from typing import Optional, List
from pydantic import BaseModel, Field
from datetime import datetime


class BookingCreate(BaseModel):
    service_id: str
    church_id: str
    booking_date: datetime
    contact_phone: Optional[str] = Field(None, pattern=r'^\+?[0-9]{10,15}$')
    notes: Optional[str] = None
    names: Optional[str] = None  # For prayer notes: comma-separated names


class BookingUpdate(BaseModel):
    status: Optional[str] = None
    notes: Optional[str] = None


class BookingResponse(BaseModel):
    id: str
    user_id: str
    service_id: str
    church_id: str
    booking_date: datetime
    status: str
    payment_status: str
    payment_amount: float
    commission_amount: float
    notes: Optional[str] = None
    contact_phone: Optional[str] = None
    names: Optional[str] = None
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True


class BookingListResponse(BaseModel):
    bookings: List[BookingResponse]
    total: int


class BookingWithPaymentUrl(BaseModel):
    booking_id: str
    status: str
    payment_url: Optional[str] = None
    expires_at: Optional[datetime] = None

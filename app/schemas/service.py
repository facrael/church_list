from typing import Optional, List
from pydantic import BaseModel, Field
from datetime import datetime


class ServiceCreate(BaseModel):
    church_id: str
    name: str = Field(..., max_length=255)
    description: Optional[str] = None
    category: str = Field(..., max_length=100)
    price: float = Field(..., ge=0)
    duration: int = Field(0, ge=0)  # minutes
    requires_booking: bool = True
    max_advance_days: int = Field(30, ge=1)


class ServiceUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    category: Optional[str] = None
    price: Optional[float] = None
    duration: Optional[int] = None
    is_available: Optional[bool] = None
    requires_booking: Optional[bool] = None
    max_advance_days: Optional[int] = None


class ServiceResponse(BaseModel):
    id: str
    church_id: str
    name: str
    description: Optional[str] = None
    category: str
    price: float
    duration: int
    is_available: bool
    requires_booking: bool
    max_advance_days: int
    created_at: datetime

    class Config:
        from_attributes = True


class ServiceListResponse(BaseModel):
    services: List[ServiceResponse]
    total: int

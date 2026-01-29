from typing import Optional, List, Dict, Any
from pydantic import BaseModel, Field
from datetime import datetime

from app.schemas.common import Coordinates


class ChurchCreate(BaseModel):
    name: str = Field(..., max_length=255)
    religion: str
    denomination: Optional[str] = None
    description: Optional[str] = None
    latitude: float = Field(..., ge=-90, le=90)
    longitude: float = Field(..., ge=-180, le=180)
    address: str = Field(..., max_length=500)
    city: str = Field(..., max_length=100)
    phone: Optional[str] = None
    email: Optional[str] = None
    website: Optional[str] = None
    photos: List[str] = []
    working_hours: Dict[str, str] = {}


class ChurchUpdate(BaseModel):
    name: Optional[str] = None
    denomination: Optional[str] = None
    description: Optional[str] = None
    phone: Optional[str] = None
    email: Optional[str] = None
    website: Optional[str] = None
    photos: Optional[List[str]] = None
    working_hours: Optional[Dict[str, str]] = None


class ChurchResponse(BaseModel):
    id: str
    name: str
    religion: str
    denomination: Optional[str] = None
    description: Optional[str] = None
    latitude: float
    longitude: float
    address: str
    city: str
    phone: Optional[str] = None
    email: Optional[str] = None
    website: Optional[str] = None
    photos: Any = []
    working_hours: Any = {}
    rating: float
    reviews_count: int
    verified: bool
    created_at: datetime

    class Config:
        from_attributes = True


class ChurchListItem(BaseModel):
    id: str
    name: str
    religion: str
    distance: Optional[float] = None  # in meters
    address: str
    rating: float
    photo: Optional[str] = None
    coordinates: Coordinates

    class Config:
        from_attributes = True


class ChurchListResponse(BaseModel):
    churches: List[ChurchListItem]
    total: int


class ChurchNearbyRequest(BaseModel):
    lat: float = Field(..., ge=-90, le=90)
    lng: float = Field(..., ge=-180, le=180)
    radius: int = Field(5000, ge=100, le=50000)  # meters
    religion: Optional[str] = None

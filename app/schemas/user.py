from typing import Optional, List
from pydantic import BaseModel, EmailStr, Field
from datetime import datetime


class UserCreate(BaseModel):
    phone: Optional[str] = Field(None, pattern=r'^\+?[0-9]{10,15}$')
    email: Optional[EmailStr] = None
    password: str = Field(..., min_length=6)
    name: Optional[str] = None
    religion: Optional[str] = None


class UserLogin(BaseModel):
    email: Optional[EmailStr] = None
    phone: Optional[str] = None
    password: str


class UserUpdate(BaseModel):
    name: Optional[str] = None
    avatar: Optional[str] = None
    religion: Optional[str] = None


class UserResponse(BaseModel):
    id: str
    phone: Optional[str] = None
    email: Optional[str] = None
    name: Optional[str] = None
    avatar: Optional[str] = None
    religion: Optional[str] = None
    role: str
    is_verified: bool
    premium_until: Optional[datetime] = None
    favorite_churches: List[str] = []
    created_at: datetime

    class Config:
        from_attributes = True


class Token(BaseModel):
    access_token: str
    refresh_token: str
    token_type: str = "bearer"


class TokenPayload(BaseModel):
    sub: str
    exp: int
    type: str  # "access" or "refresh"

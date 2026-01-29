from typing import TypeVar, Generic, Optional, Any
from pydantic import BaseModel
from datetime import datetime

T = TypeVar('T')


class ApiResponse(BaseModel, Generic[T]):
    success: bool = True
    data: Optional[T] = None
    error: Optional[dict] = None
    meta: Optional[dict] = None

    @classmethod
    def success_response(cls, data: T, meta: dict = None):
        return cls(
            success=True,
            data=data,
            meta=meta or {"timestamp": datetime.utcnow().isoformat(), "version": "1.0"}
        )

    @classmethod
    def error_response(cls, code: str, message: str, details: dict = None):
        return cls(
            success=False,
            error={
                "code": code,
                "message": message,
                "details": details or {},
                "timestamp": datetime.utcnow().isoformat()
            }
        )


class PaginationParams(BaseModel):
    page: int = 1
    per_page: int = 20


class Coordinates(BaseModel):
    lat: float
    lng: float

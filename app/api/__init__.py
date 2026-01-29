from fastapi import APIRouter
from app.api.routes import auth, churches, services, bookings, users

api_router = APIRouter()

api_router.include_router(auth.router, prefix="/auth", tags=["Authentication"])
api_router.include_router(churches.router, prefix="/churches", tags=["Churches"])
api_router.include_router(services.router, prefix="/services", tags=["Services"])
api_router.include_router(bookings.router, prefix="/bookings", tags=["Bookings"])
api_router.include_router(users.router, prefix="/users", tags=["Users"])

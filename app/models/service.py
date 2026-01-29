import uuid
from datetime import datetime
from sqlalchemy import String, Text, DateTime, Boolean, Float, Integer, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


class Service(Base):
    __tablename__ = "services"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )
    church_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("churches.id"), nullable=False
    )
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=True)
    category: Mapped[str] = mapped_column(String(100), nullable=False)
    price: Mapped[float] = mapped_column(Float, nullable=False)
    duration: Mapped[int] = mapped_column(Integer, default=0)  # minutes
    is_available: Mapped[bool] = mapped_column(Boolean, default=True)
    requires_booking: Mapped[bool] = mapped_column(Boolean, default=True)
    max_advance_days: Mapped[int] = mapped_column(Integer, default=30)

    created_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    # Relationships
    church = relationship("Church", back_populates="services")
    bookings = relationship("Booking", back_populates="service")

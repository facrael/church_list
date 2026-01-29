import uuid
from datetime import datetime
from sqlalchemy import String, Text, DateTime, Enum, Boolean, Float, JSON
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base
from app.models.user import Religion


class Church(Base):
    __tablename__ = "churches"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    religion: Mapped[str] = mapped_column(String(20), nullable=False)
    denomination: Mapped[str] = mapped_column(String(100), nullable=True)
    description: Mapped[str] = mapped_column(Text, nullable=True)

    # Location
    latitude: Mapped[float] = mapped_column(Float, nullable=False)
    longitude: Mapped[float] = mapped_column(Float, nullable=False)
    address: Mapped[str] = mapped_column(String(500), nullable=False)
    city: Mapped[str] = mapped_column(String(100), nullable=False)

    # Contact
    phone: Mapped[str] = mapped_column(String(20), nullable=True)
    email: Mapped[str] = mapped_column(String(255), nullable=True)
    website: Mapped[str] = mapped_column(String(500), nullable=True)

    # Media (JSON array of URLs)
    photos: Mapped[str] = mapped_column(JSON, default="[]")

    # Schedule
    working_hours: Mapped[str] = mapped_column(JSON, default="{}")

    # Stats
    rating: Mapped[float] = mapped_column(Float, default=0.0)
    reviews_count: Mapped[int] = mapped_column(default=0)

    # Status
    verified: Mapped[bool] = mapped_column(Boolean, default=False)
    is_active: Mapped[bool] = mapped_column(Boolean, default=True)

    # Admin
    admin_id: Mapped[str] = mapped_column(String(36), nullable=True)

    created_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    # Relationships
    services = relationship("Service", back_populates="church")
    bookings = relationship("Booking", back_populates="church")
    events = relationship("Event", back_populates="church")

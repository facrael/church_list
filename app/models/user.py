import uuid
from datetime import datetime
from sqlalchemy import String, DateTime, Boolean, JSON
from sqlalchemy.orm import Mapped, mapped_column, relationship
import enum


class Religion(str, enum.Enum):
    ORTHODOX = "orthodox"
    ISLAM = "islam"
    JUDAISM = "judaism"


class UserRole(str, enum.Enum):
    USER = "user"
    CHURCH_ADMIN = "church_admin"
    ADMIN = "admin"


from app.database import Base


class User(Base):
    __tablename__ = "users"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )
    phone: Mapped[str] = mapped_column(String(20), unique=True, nullable=True)
    email: Mapped[str] = mapped_column(String(255), unique=True, nullable=True)
    hashed_password: Mapped[str] = mapped_column(String(255), nullable=False)
    name: Mapped[str] = mapped_column(String(100), nullable=True)
    avatar: Mapped[str] = mapped_column(String(500), nullable=True)
    religion: Mapped[str] = mapped_column(String(20), nullable=True)
    role: Mapped[str] = mapped_column(String(20), default=UserRole.USER.value)
    is_active: Mapped[bool] = mapped_column(Boolean, default=True)
    is_verified: Mapped[bool] = mapped_column(Boolean, default=False)
    premium_until: Mapped[datetime] = mapped_column(DateTime, nullable=True)
    favorite_churches: Mapped[str] = mapped_column(JSON, default="[]")
    created_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    # Relationships
    bookings = relationship("Booking", back_populates="user")
    payments = relationship("Payment", back_populates="user")

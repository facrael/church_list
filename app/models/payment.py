import uuid
from datetime import datetime
from sqlalchemy import String, DateTime, Float, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
import enum

from app.database import Base


class PaymentType(str, enum.Enum):
    SERVICE = "service"
    DONATION = "donation"


class PaymentProviderStatus(str, enum.Enum):
    PENDING = "pending"
    SUCCESS = "success"
    FAILED = "failed"
    REFUNDED = "refunded"


class Payment(Base):
    __tablename__ = "payments"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )
    user_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("users.id"), nullable=False
    )
    booking_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("bookings.id"), nullable=True
    )
    church_id: Mapped[str] = mapped_column(String(36), nullable=False)

    amount: Mapped[float] = mapped_column(Float, nullable=False)
    commission: Mapped[float] = mapped_column(Float, default=0.0)
    type: Mapped[str] = mapped_column(String(20), nullable=False)
    status: Mapped[str] = mapped_column(String(20), default=PaymentProviderStatus.PENDING.value)

    payment_method: Mapped[str] = mapped_column(String(50), nullable=True)
    payment_provider: Mapped[str] = mapped_column(String(50), default="yookassa")
    transaction_id: Mapped[str] = mapped_column(String(255), nullable=True)
    payment_url: Mapped[str] = mapped_column(String(500), nullable=True)

    created_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    # Relationships
    user = relationship("User", back_populates="payments")
    booking = relationship("Booking", back_populates="payment")

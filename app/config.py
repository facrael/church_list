from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    # App
    APP_NAME: str = "Religious Marketplace API"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = True

    # Database (SQLite for development, PostgreSQL for production)
    DATABASE_URL: str = "sqlite+aiosqlite:///./church_marketplace.db"

    # JWT
    SECRET_KEY: str = "your-secret-key-change-in-production"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7

    # Redis
    REDIS_URL: str = "redis://localhost:6379"

    # 2GIS API
    DGIS_API_KEY: str = ""

    # YooKassa
    YOOKASSA_SHOP_ID: str = ""
    YOOKASSA_SECRET_KEY: str = ""

    # Commission rates
    SERVICE_COMMISSION_RATE: float = 0.07  # 7%
    DONATION_COMMISSION_RATE: float = 0.04  # 4%

    class Config:
        env_file = ".env"


@lru_cache()
def get_settings() -> Settings:
    return Settings()


settings = get_settings()

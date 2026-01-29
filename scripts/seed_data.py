"""
Seed script to populate the database with test data.
Run: python -m scripts.seed_data
"""
import asyncio
import json
import sys
sys.path.insert(0, '.')

from app.database import async_session_maker, init_db
from app.models.user import User, Religion, UserRole
from app.models.church import Church
from app.models.service import Service
from app.models.content import Content, ContentType
from app.core.security import get_password_hash


async def seed_users():
    """Create test users"""
    users = [
        User(
            email="admin@example.com",
            phone="+79991234567",
            hashed_password=get_password_hash("admin123"),
            name="Admin User",
            role=UserRole.ADMIN.value,
            is_verified=True,
            favorite_churches=json.dumps([])
        ),
        User(
            email="user@example.com",
            phone="+79997654321",
            hashed_password=get_password_hash("user123"),
            name="Test User",
            religion=Religion.ORTHODOX.value,
            is_verified=True,
            favorite_churches=json.dumps([])
        ),
        User(
            email="church_admin@example.com",
            phone="+79998887766",
            hashed_password=get_password_hash("church123"),
            name="Church Admin",
            role=UserRole.CHURCH_ADMIN.value,
            religion=Religion.ORTHODOX.value,
            is_verified=True,
            favorite_churches=json.dumps([])
        ),
    ]
    return users


async def seed_churches():
    """Create test churches"""
    churches = [
        Church(
            name="Храм Христа Спасителя",
            religion=Religion.ORTHODOX.value,
            denomination="Русская православная церковь",
            description="Кафедральный собор Русской православной церкви. Крупнейший православный храм России.",
            latitude=55.7447,
            longitude=37.6056,
            address="ул. Волхонка, 15",
            city="Москва",
            phone="+74959524054",
            email="info@xxc.ru",
            website="https://xxc.ru",
            photos=json.dumps([
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Christ_the_Savior_Cathedral_Moscow.jpg/800px-Christ_the_Savior_Cathedral_Moscow.jpg"
            ]),
            working_hours=json.dumps({
                "monday": "08:00-20:00",
                "tuesday": "08:00-20:00",
                "wednesday": "08:00-20:00",
                "thursday": "08:00-20:00",
                "friday": "08:00-20:00",
                "saturday": "08:00-20:00",
                "sunday": "07:00-21:00"
            }),
            rating=4.8,
            reviews_count=1250,
            verified=True
        ),
        Church(
            name="Храм Василия Блаженного",
            religion=Religion.ORTHODOX.value,
            denomination="Русская православная церковь",
            description="Собор Покрова Пресвятой Богородицы, что на Рву. Памятник русской архитектуры.",
            latitude=55.7525,
            longitude=37.6231,
            address="Красная площадь, 2",
            city="Москва",
            phone="+74956988304",
            photos=json.dumps([
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Saint_Basil%27s_Cathedral_2017.jpg/800px-Saint_Basil%27s_Cathedral_2017.jpg"
            ]),
            working_hours=json.dumps({
                "monday": "10:00-18:00",
                "tuesday": "10:00-18:00",
                "wednesday": "10:00-18:00",
                "thursday": "10:00-18:00",
                "friday": "10:00-18:00",
                "saturday": "10:00-18:00",
                "sunday": "10:00-18:00"
            }),
            rating=4.9,
            reviews_count=3500,
            verified=True
        ),
        Church(
            name="Московская Соборная мечеть",
            religion=Religion.ISLAM.value,
            denomination="Суннитский ислам",
            description="Главная мечеть Москвы. Одна из крупнейших мечетей в России и Европе.",
            latitude=55.7708,
            longitude=37.6313,
            address="Выползов пер., 7",
            city="Москва",
            phone="+74956816633",
            email="info@moscowmosque.ru",
            website="https://moscowmosque.ru",
            photos=json.dumps([
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Moscow_Cathedral_Mosque.jpg/800px-Moscow_Cathedral_Mosque.jpg"
            ]),
            working_hours=json.dumps({
                "monday": "09:00-21:00",
                "tuesday": "09:00-21:00",
                "wednesday": "09:00-21:00",
                "thursday": "09:00-21:00",
                "friday": "09:00-21:00",
                "saturday": "09:00-21:00",
                "sunday": "09:00-21:00"
            }),
            rating=4.9,
            reviews_count=890,
            verified=True
        ),
        Church(
            name="Московская хоральная синагога",
            religion=Religion.JUDAISM.value,
            denomination="Ортодоксальный иудаизм",
            description="Главная синагога Москвы. Историческое здание в центре города.",
            latitude=55.7565,
            longitude=37.6485,
            address="Большой Спасоглинищевский пер., 10",
            city="Москва",
            phone="+74959234366",
            email="info@mjcc.ru",
            website="https://mjcc.ru",
            photos=json.dumps([
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Moscow_Choral_Synagogue.jpg/800px-Moscow_Choral_Synagogue.jpg"
            ]),
            working_hours=json.dumps({
                "monday": "09:00-18:00",
                "tuesday": "09:00-18:00",
                "wednesday": "09:00-18:00",
                "thursday": "09:00-18:00",
                "friday": "09:00-15:00",
                "saturday": "closed",
                "sunday": "09:00-18:00"
            }),
            rating=4.7,
            reviews_count=320,
            verified=True
        ),
    ]
    return churches


async def seed_services(churches: list[Church]):
    """Create test services for churches"""
    services = []

    orthodox_services = [
        ("Крещение", "Таинство крещения для детей и взрослых", "Таинства", 3000, 60),
        ("Венчание", "Таинство венчания для новобрачных", "Таинства", 5000, 90),
        ("Записка о здравии", "Поминовение о здравии на Литургии", "Записки", 50, 0),
        ("Записка о упокоении", "Поминовение о упокоении на Литургии", "Записки", 50, 0),
        ("Молебен", "Молебен о здравии", "Молитвы", 500, 30),
        ("Панихида", "Панихида об упокоении", "Молитвы", 500, 30),
        ("Освящение автомобиля", "Освящение транспортного средства", "Освящение", 1000, 30),
        ("Освящение жилья", "Освящение квартиры или дома", "Освящение", 2000, 60),
    ]

    for church in churches[:2]:
        for name, desc, category, price, duration in orthodox_services:
            services.append(Service(
                church_id=church.id,
                name=name,
                description=desc,
                category=category,
                price=price,
                duration=duration,
                requires_booking=category == "Таинства",
                max_advance_days=30
            ))

    islamic_services = [
        ("Никах", "Исламская церемония бракосочетания", "Обряды", 5000, 60),
        ("Ду'а", "Коллективная молитва-просьба", "Молитвы", 1000, 30),
        ("Джаназа", "Погребальная молитва", "Обряды", 3000, 45),
        ("Консультация имама", "Духовная беседа с имамом", "Консультации", 0, 30),
    ]

    mosque = churches[2]
    for name, desc, category, price, duration in islamic_services:
        services.append(Service(
            church_id=mosque.id,
            name=name,
            description=desc,
            category=category,
            price=price,
            duration=duration,
            requires_booking=True,
            max_advance_days=30
        ))

    jewish_services = [
        ("Бар-мицва", "Церемония совершеннолетия мальчика", "Обряды", 10000, 120),
        ("Бат-мицва", "Церемония совершеннолетия девочки", "Обряды", 10000, 120),
        ("Йорцайт", "Поминовение усопших", "Молитвы", 500, 30),
        ("Консультация раввина", "Духовная беседа с раввином", "Консультации", 0, 30),
    ]

    synagogue = churches[3]
    for name, desc, category, price, duration in jewish_services:
        services.append(Service(
            church_id=synagogue.id,
            name=name,
            description=desc,
            category=category,
            price=price,
            duration=duration,
            requires_booking=True,
            max_advance_days=60
        ))

    return services


async def seed_content():
    """Create test religious content"""
    content = [
        Content(
            type=ContentType.PRAYER.value,
            title="Отче наш",
            content="""Отче наш, Иже еси на небесех!
Да святится имя Твое,
да приидет Царствие Твое,
да будет воля Твоя,
яко на небеси и на земли.
Хлеб наш насущный даждь нам днесь;
и остави нам долги наша,
якоже и мы оставляем должником нашим;
и не введи нас во искушение,
но избави нас от лукаваго.""",
            religion=Religion.ORTHODOX.value,
            category="Основные молитвы"
        ),
        Content(
            type=ContentType.PRAYER.value,
            title="Аль-Фатиха",
            content="""Во имя Аллаха, Милостивого, Милосердного!
Хвала Аллаху, Господу миров,
Милостивому, Милосердному,
Властелину Дня воздаяния!
Тебе одному мы поклоняемся
и Тебя одного молим о помощи.
Веди нас прямым путем...""",
            religion=Religion.ISLAM.value,
            category="Суры Корана"
        ),
        Content(
            type=ContentType.PRAYER.value,
            title="Шма Исраэль",
            content="""Слушай, Израиль: Господь — Бог наш,
Господь один!
Люби Господа, Бога твоего,
всем сердцем твоим,
и всей душою твоей,
и всеми силами твоими.""",
            religion=Religion.JUDAISM.value,
            category="Основные молитвы"
        ),
    ]
    return content


async def main():
    print("Initializing database...")
    await init_db()

    async with async_session_maker() as session:
        try:
            from sqlalchemy import select
            result = await session.execute(select(User).limit(1))
            if result.scalar_one_or_none():
                print("Database already seeded. Skipping...")
                return

            print("Seeding users...")
            users = await seed_users()
            for user in users:
                session.add(user)
            await session.flush()

            print("Seeding churches...")
            churches = await seed_churches()
            churches[0].admin_id = users[2].id
            for church in churches:
                session.add(church)
            await session.flush()

            print("Seeding services...")
            services = await seed_services(churches)
            for service in services:
                session.add(service)

            print("Seeding content...")
            content = await seed_content()
            for item in content:
                session.add(item)

            await session.commit()
            print("Database seeded successfully!")

            print("\nTest accounts:")
            print("  Admin: admin@example.com / admin123")
            print("  User: user@example.com / user123")
            print("  Church Admin: church_admin@example.com / church123")

        except Exception as e:
            await session.rollback()
            print(f"Error seeding database: {e}")
            raise


if __name__ == "__main__":
    asyncio.run(main())

"""
Скрипт для сбора данных о религиозных объектах России из OpenStreetMap.

Использование:
    python fetch_places.py

Результат сохраняется в data/places-russia.json
"""

import requests
import json
import time
from pathlib import Path
from datetime import datetime

# Все регионы России с OSM ID
REGIONS_RUSSIA = [
    # Города федерального значения
    {"name": "Москва", "osm_id": 2555133, "slug": "moscow"},
    {"name": "Санкт-Петербург", "osm_id": 337422, "slug": "spb"},
    {"name": "Севастополь", "osm_id": 1574364, "slug": "sevastopol"},

    # Области
    {"name": "Московская область", "osm_id": 51490, "slug": "moscow-oblast"},
    {"name": "Ленинградская область", "osm_id": 176095, "slug": "leningrad-oblast"},
    {"name": "Свердловская область", "osm_id": 79379, "slug": "sverdlovsk"},
    {"name": "Нижегородская область", "osm_id": 72195, "slug": "nizhniy-novgorod"},
    {"name": "Ростовская область", "osm_id": 85606, "slug": "rostov"},
    {"name": "Челябинская область", "osm_id": 77687, "slug": "chelyabinsk"},
    {"name": "Самарская область", "osm_id": 72194, "slug": "samara"},
    {"name": "Воронежская область", "osm_id": 72181, "slug": "voronezh"},
    {"name": "Волгоградская область", "osm_id": 77665, "slug": "volgograd"},
    {"name": "Саратовская область", "osm_id": 72193, "slug": "saratov"},
    {"name": "Тюменская область", "osm_id": 140291, "slug": "tyumen"},
    {"name": "Оренбургская область", "osm_id": 77669, "slug": "orenburg"},
    {"name": "Омская область", "osm_id": 140292, "slug": "omsk"},
    {"name": "Новосибирская область", "osm_id": 140294, "slug": "novosibirsk"},
    {"name": "Кемеровская область", "osm_id": 144763, "slug": "kemerovo"},
    {"name": "Иркутская область", "osm_id": 145454, "slug": "irkutsk"},
    {"name": "Белгородская область", "osm_id": 85617, "slug": "belgorod"},
    {"name": "Брянская область", "osm_id": 81997, "slug": "bryansk"},
    {"name": "Владимирская область", "osm_id": 72197, "slug": "vladimir"},
    {"name": "Вологодская область", "osm_id": 462139, "slug": "vologda"},
    {"name": "Ивановская область", "osm_id": 85617, "slug": "ivanovo"},
    {"name": "Калининградская область", "osm_id": 103906, "slug": "kaliningrad"},
    {"name": "Калужская область", "osm_id": 81995, "slug": "kaluga"},
    {"name": "Костромская область", "osm_id": 85963, "slug": "kostroma"},
    {"name": "Курская область", "osm_id": 72223, "slug": "kursk"},
    {"name": "Липецкая область", "osm_id": 72169, "slug": "lipetsk"},
    {"name": "Мурманская область", "osm_id": 2099216, "slug": "murmansk"},
    {"name": "Новгородская область", "osm_id": 89331, "slug": "novgorod"},
    {"name": "Орловская область", "osm_id": 72224, "slug": "orel"},
    {"name": "Пензенская область", "osm_id": 72182, "slug": "penza"},
    {"name": "Псковская область", "osm_id": 155262, "slug": "pskov"},
    {"name": "Рязанская область", "osm_id": 71950, "slug": "ryazan"},
    {"name": "Смоленская область", "osm_id": 81996, "slug": "smolensk"},
    {"name": "Тамбовская область", "osm_id": 72180, "slug": "tambov"},
    {"name": "Тверская область", "osm_id": 2095259, "slug": "tver"},
    {"name": "Тульская область", "osm_id": 81993, "slug": "tula"},
    {"name": "Ульяновская область", "osm_id": 72192, "slug": "ulyanovsk"},
    {"name": "Ярославская область", "osm_id": 81994, "slug": "yaroslavl"},
    {"name": "Архангельская область", "osm_id": 140337, "slug": "arkhangelsk"},
    {"name": "Астраханская область", "osm_id": 112819, "slug": "astrakhan"},
    {"name": "Курганская область", "osm_id": 140290, "slug": "kurgan"},
    {"name": "Амурская область", "osm_id": 147166, "slug": "amur"},
    {"name": "Магаданская область", "osm_id": 151233, "slug": "magadan"},
    {"name": "Сахалинская область", "osm_id": 394235, "slug": "sakhalin"},
    {"name": "Томская область", "osm_id": 140295, "slug": "tomsk"},

    # Края
    {"name": "Краснодарский край", "osm_id": 108082, "slug": "krasnodar"},
    {"name": "Красноярский край", "osm_id": 190090, "slug": "krasnoyarsk"},
    {"name": "Пермский край", "osm_id": 115135, "slug": "perm"},
    {"name": "Ставропольский край", "osm_id": 108081, "slug": "stavropol"},
    {"name": "Алтайский край", "osm_id": 144764, "slug": "altai-krai"},
    {"name": "Приморский край", "osm_id": 151225, "slug": "primorsky"},
    {"name": "Хабаровский край", "osm_id": 151223, "slug": "khabarovsk"},
    {"name": "Забайкальский край", "osm_id": 145730, "slug": "zabaykalsky"},
    {"name": "Камчатский край", "osm_id": 151231, "slug": "kamchatka"},

    # Республики
    {"name": "Республика Татарстан", "osm_id": 79374, "slug": "tatarstan"},
    {"name": "Республика Башкортостан", "osm_id": 77677, "slug": "bashkortostan"},
    {"name": "Республика Дагестан", "osm_id": 109876, "slug": "dagestan"},
    {"name": "Чеченская Республика", "osm_id": 109877, "slug": "chechnya"},
    {"name": "Удмуртская Республика", "osm_id": 115134, "slug": "udmurtia"},
    {"name": "Чувашская Республика", "osm_id": 80513, "slug": "chuvashia"},
    {"name": "Республика Крым", "osm_id": 1574364, "slug": "crimea"},
    {"name": "Республика Мордовия", "osm_id": 72196, "slug": "mordovia"},
    {"name": "Республика Марий Эл", "osm_id": 115114, "slug": "mari-el"},
    {"name": "Кабардино-Балкарская Республика", "osm_id": 109879, "slug": "kabardino-balkaria"},
    {"name": "Республика Северная Осетия", "osm_id": 110032, "slug": "north-ossetia"},
    {"name": "Карачаево-Черкесская Республика", "osm_id": 109878, "slug": "karachay-cherkessia"},
    {"name": "Республика Адыгея", "osm_id": 108083, "slug": "adygea"},
    {"name": "Республика Калмыкия", "osm_id": 108083, "slug": "kalmykia"},
    {"name": "Республика Карелия", "osm_id": 393980, "slug": "karelia"},
    {"name": "Республика Коми", "osm_id": 115136, "slug": "komi"},
    {"name": "Республика Саха (Якутия)", "osm_id": 151234, "slug": "sakha"},
    {"name": "Республика Бурятия", "osm_id": 145729, "slug": "buryatia"},
    {"name": "Республика Тыва", "osm_id": 145195, "slug": "tuva"},
    {"name": "Республика Хакасия", "osm_id": 190911, "slug": "khakassia"},
    {"name": "Республика Алтай", "osm_id": 145194, "slug": "altai-republic"},
    {"name": "Республика Ингушетия", "osm_id": 253252, "slug": "ingushetia"},

    # Автономные округа
    {"name": "Ханты-Мансийский АО", "osm_id": 140296, "slug": "khanty-mansiysk"},
    {"name": "Ямало-Ненецкий АО", "osm_id": 191706, "slug": "yamalo-nenets"},
    {"name": "Ненецкий АО", "osm_id": 274048, "slug": "nenets"},
    {"name": "Чукотский АО", "osm_id": 151232, "slug": "chukotka"},
    {"name": "Еврейская АО", "osm_id": 147167, "slug": "jewish-ao"},
]


def fetch_region(osm_id: int, region_name: str) -> list:
    """Получить религиозные объекты региона из OSM"""

    query = f"""
    [out:json][timeout:180];
    area({3600000000 + osm_id})->.searchArea;
    (
        // Группа "Храмы": церкви, соборы, часовни, монастыри
        nwr["amenity"="place_of_worship"]["religion"="christian"](area.searchArea);
        nwr["building"="chapel"](area.searchArea);
        nwr["building"="cathedral"](area.searchArea);
        nwr["building"="church"](area.searchArea);
        nwr["building"="monastery"](area.searchArea);
        nwr["amenity"="monastery"](area.searchArea);
        // Группа "Святые источники": родники, купели
        nwr["natural"="spring"](area.searchArea);
        nwr["amenity"="drinking_water"]["religion"](area.searchArea);
        nwr["amenity"="baptistery"](area.searchArea);
    );
    out center meta;
    """

    response = requests.post(
        'https://overpass-api.de/api/interpreter',
        data=query.encode('utf-8'),
        timeout=200,
        headers={'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
    )
    response.raise_for_status()
    data = response.json()

    places = []
    for el in data.get('elements', []):
        tags = el.get('tags', {})

        # Определяем тип объекта
        place_type = detect_place_type(tags)

        # Фильтруем - оставляем только православные (или без указания)
        denomination = tags.get('denomination', '').lower()
        religion = tags.get('religion', '').lower()

        # Пропускаем явно не православные
        if denomination and 'orthodox' not in denomination:
            if 'catholic' in denomination or 'protestant' in denomination or 'baptist' in denomination:
                continue

        # Для родников и источников - берём все
        if place_type in ['spring', 'font']:
            pass  # Берём все
        elif religion and 'christian' not in religion:
            continue

        lat = el.get('lat') or el.get('center', {}).get('lat')
        lon = el.get('lon') or el.get('center', {}).get('lon')

        if lat and lon:
            places.append({
                'id': f"osm_{el['id']}",
                'name': tags.get('name', 'Без названия'),
                'type': place_type,
                'lat': round(lat, 6),
                'lng': round(lon, 6),
                'address': build_address(tags),
                'phone': tags.get('phone') or tags.get('contact:phone', ''),
                'website': tags.get('website') or tags.get('contact:website', ''),
                'opening_hours': tags.get('opening_hours', ''),
                'region': region_name,
                'denomination': tags.get('denomination', ''),
            })

    return places


def detect_place_type(tags: dict) -> str:
    """
    Определить тип объекта по тегам OSM.

    Две группы:
    - church: храмы, церкви, соборы, часовни, монастыри
    - spring: святые источники, родники, купели
    """
    # Группа "Святые источники"
    if tags.get('natural') == 'spring':
        return 'spring'
    if tags.get('amenity') == 'drinking_water':
        return 'spring'
    if tags.get('amenity') == 'baptistery':
        return 'spring'  # купели тоже в группу источников

    # Группа "Храмы" (всё остальное религиозное)
    return 'church'


def build_address(tags: dict) -> str:
    """Собрать адрес из тегов"""
    parts = []
    if tags.get('addr:city'):
        parts.append(tags['addr:city'])
    if tags.get('addr:street'):
        street = tags['addr:street']
        if tags.get('addr:housenumber'):
            street += f", {tags['addr:housenumber']}"
        parts.append(street)
    return ', '.join(parts)


def fetch_all_russia(output_file: str = None):
    """Собрать данные по всем регионам России"""

    if output_file is None:
        output_dir = Path(__file__).parent.parent / 'data'
        output_dir.mkdir(exist_ok=True)
        output_file = output_dir / 'places-russia.json'

    all_places = {}
    stats = {
        'church': 0,   # Храмы (церкви, соборы, часовни, монастыри)
        'spring': 0    # Святые источники (родники, купели)
    }
    errors = []

    total_regions = len(REGIONS_RUSSIA)

    print(f"{'='*60}")
    print(f"Сбор данных о религиозных объектах России")
    print(f"Регионов: {total_regions}")
    print(f"Начало: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"{'='*60}\n")

    for i, region in enumerate(REGIONS_RUSSIA, 1):
        print(f"[{i}/{total_regions}] {region['name']}...", end=' ', flush=True)

        try:
            places = fetch_region(region['osm_id'], region['name'])
            all_places[region['slug']] = places

            # Обновляем статистику
            for p in places:
                ptype = p['type']
                stats[ptype] = stats.get(ptype, 0) + 1

            print(f"✓ {len(places)} объектов")

        except requests.exceptions.Timeout:
            print(f"✗ Таймаут")
            errors.append(f"{region['name']}: таймаут")
            all_places[region['slug']] = []

        except requests.exceptions.RequestException as e:
            print(f"✗ Ошибка сети: {e}")
            errors.append(f"{region['name']}: {e}")
            all_places[region['slug']] = []

        except Exception as e:
            print(f"✗ Ошибка: {e}")
            errors.append(f"{region['name']}: {e}")
            all_places[region['slug']] = []

        # Пауза между запросами (требование Overpass API)
        if i < total_regions:
            time.sleep(10)

    # Сохраняем результат
    result = {
        'meta': {
            'generated': datetime.now().isoformat(),
            'total_places': sum(len(p) for p in all_places.values()),
            'regions_count': len(all_places),
            'stats': stats
        },
        'regions': all_places
    }

    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)

    # Итоговая статистика
    total = sum(len(p) for p in all_places.values())

    print(f"\n{'='*60}")
    print(f"ИТОГО")
    print(f"{'='*60}")
    print(f"Всего объектов: {total:,}")
    print(f"\nПо типам:")
    for t, count in sorted(stats.items(), key=lambda x: -x[1]):
        if count > 0:
            print(f"  {t}: {count:,}")

    if errors:
        print(f"\nОшибки ({len(errors)}):")
        for err in errors[:10]:
            print(f"  - {err}")
        if len(errors) > 10:
            print(f"  ... и ещё {len(errors) - 10}")

    print(f"\nФайл сохранён: {output_file}")
    print(f"Размер: {Path(output_file).stat().st_size / 1024 / 1024:.2f} MB")
    print(f"Завершено: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")

    return result


def fetch_single_region(slug: str):
    """Загрузить данные только для одного региона (для теста)"""

    region = next((r for r in REGIONS_RUSSIA if r['slug'] == slug), None)
    if not region:
        print(f"Регион '{slug}' не найден")
        print("Доступные регионы:")
        for r in REGIONS_RUSSIA[:10]:
            print(f"  - {r['slug']}: {r['name']}")
        return None

    print(f"Загрузка: {region['name']}...")
    places = fetch_region(region['osm_id'], region['name'])
    print(f"Найдено: {len(places)} объектов")

    # Показываем примеры
    print("\nПримеры:")
    for p in places[:5]:
        print(f"  - {p['name']} ({p['type']})")

    return places


def retry_failed_regions(output_file: str = None):
    """Дозагрузить только неудачные регионы (пустые или с ошибками)"""

    if output_file is None:
        output_dir = Path(__file__).parent.parent / 'data'
        output_file = output_dir / 'places-russia.json'

    # Загружаем существующие данные
    if not Path(output_file).exists():
        print("Файл не найден, запускаю полный сбор...")
        return fetch_all_russia(output_file)

    with open(output_file, 'r', encoding='utf-8') as f:
        existing_data = json.load(f)

    existing_regions = existing_data.get('regions', {})

    # Находим регионы с ошибками (пустые списки)
    failed_regions = []
    for region in REGIONS_RUSSIA:
        slug = region['slug']
        places = existing_regions.get(slug, [])
        if len(places) == 0:
            failed_regions.append(region)

    if not failed_regions:
        print("Все регионы успешно загружены!")
        return existing_data

    print(f"{'='*60}")
    print(f"Дозагрузка неудачных регионов")
    print(f"Регионов с ошибками: {len(failed_regions)}")
    print(f"{'='*60}\n")

    stats = {'church': 0, 'spring': 0}
    errors = []
    retry_count = 0

    for i, region in enumerate(failed_regions, 1):
        print(f"[{i}/{len(failed_regions)}] {region['name']}...", end=' ', flush=True)

        try:
            places = fetch_region(region['osm_id'], region['name'])
            existing_regions[region['slug']] = places

            for p in places:
                ptype = p['type']
                stats[ptype] = stats.get(ptype, 0) + 1

            print(f"✓ {len(places)} объектов")
            retry_count += 1

        except Exception as e:
            print(f"✗ {e}")
            errors.append(f"{region['name']}: {e}")

        # Увеличенная пауза для retry
        if i < len(failed_regions):
            time.sleep(15)

    # Обновляем метаданные
    total = sum(len(p) for p in existing_regions.values())
    existing_data['meta'] = {
        'generated': datetime.now().isoformat(),
        'total_places': total,
        'regions_count': len([r for r in existing_regions.values() if len(r) > 0]),
        'stats': {
            'church': sum(1 for places in existing_regions.values() for p in places if p['type'] == 'church'),
            'spring': sum(1 for places in existing_regions.values() for p in places if p['type'] == 'spring')
        }
    }
    existing_data['regions'] = existing_regions

    # Сохраняем
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(existing_data, f, ensure_ascii=False, indent=2)

    print(f"\n{'='*60}")
    print(f"Дозагружено: {retry_count} регионов")
    print(f"Всего объектов: {total:,}")

    if errors:
        print(f"\nОстались ошибки ({len(errors)}):")
        for err in errors:
            print(f"  - {err}")
        print("\nЗапустите ещё раз: python fetch_places.py --retry")

    print(f"\nФайл обновлён: {output_file}")

    return existing_data


if __name__ == '__main__':
    import sys

    if len(sys.argv) > 1:
        arg = sys.argv[1]
        if arg == '--retry' or arg == '-r':
            # Дозагрузка неудачных
            retry_failed_regions()
        elif arg == '--help' or arg == '-h':
            print("""
Использование:
  python fetch_places.py           # Полный сбор всех регионов
  python fetch_places.py --retry   # Дозагрузить только неудачные
  python fetch_places.py moscow    # Тест одного региона

Регионы: moscow, spb, moscow-oblast, krasnodar, и др.
            """)
        else:
            # Тест одного региона
            fetch_single_region(arg)
    else:
        # Полный сбор
        fetch_all_russia()

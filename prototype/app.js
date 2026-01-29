// ==========================================
// ПРАВОСЛАВНЫЙ МАРКЕТПЛЕЙС - JavaScript
// С интеграцией 2GIS API
// ==========================================

// 2GIS API Configuration
const DGIS_API_KEY = 'a6a0e70d-a34e-4f94-83ff-425d07d36042';

// Map instance
let map = null;
let markers = [];
let userMarker = null;
let currentPosition = { lat: 55.7558, lng: 37.6176 }; // Moscow by default
let useLeaflet = false; // Fallback to Leaflet if 2GIS fails

// OSM Places Data (loaded from JSON)
let osmPlacesData = null;
let osmPlacesLoaded = false;

// Place type configuration (2 groups)
// Храмы: church, cathedral, chapel, monastery
// Св. источники: spring, font
const PLACE_TYPES = {
    // Группа "Храмы"
    church: { icon: '☦', color: '#C41E3A', label: 'Храм', group: 'church' },
    cathedral: { icon: '☦', color: '#C41E3A', label: 'Собор', group: 'church' },
    chapel: { icon: '☦', color: '#C41E3A', label: 'Часовня', group: 'church' },
    monastery: { icon: '☦', color: '#C41E3A', label: 'Монастырь', group: 'church' },
    // Группа "Святые источники"
    font: { icon: '💧', color: '#1E90FF', label: 'Купель', group: 'spring' },
    spring: { icon: '💧', color: '#1E90FF', label: 'Источник', group: 'spring' }
};

// Data Store
const appData = {
    currentScreen: 'home',
    screenHistory: [],
    selectedChurch: null,
    selectedService: null,
    places2gis: [], // Real places from 2GIS

    // Православные храмы
    churches: [
        {
            id: 0,
            name: "Храм Христа Спасителя",
            type: "church",
            description: "Кафедральный собор Русской православной церкви. Крупнейший православный храм России.",
            address: "ул. Волхонка, 15, Москва",
            distance: "1.2 км",
            rating: 4.8,
            reviews: 1250,
            phone: "+7 495 952-40-54",
            email: "info@xxc.ru",
            website: "xxc.ru",
            lat: 55.7447,
            lng: 37.6056,
            image: "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Christ_the_Savior_Cathedral_Moscow.jpg/400px-Christ_the_Savior_Cathedral_Moscow.jpg",
            workingHours: {
                monday: "08:00-20:00",
                tuesday: "08:00-20:00",
                wednesday: "08:00-20:00",
                thursday: "08:00-20:00",
                friday: "08:00-20:00",
                saturday: "08:00-20:00",
                sunday: "07:00-21:00"
            },
            services: [
                { id: 1, name: "Крещение", description: "Таинство крещения для детей и взрослых", category: "Таинства", price: 3000, duration: 60 },
                { id: 2, name: "Венчание", description: "Таинство венчания для новобрачных", category: "Таинства", price: 5000, duration: 90 },
                { id: 3, name: "Записка о здравии", description: "Поминовение о здравии на Литургии", category: "Записки", price: 50, duration: 0 },
                { id: 4, name: "Записка о упокоении", description: "Поминовение о упокоении на Литургии", category: "Записки", price: 50, duration: 0 },
                { id: 5, name: "Молебен", description: "Молебен о здравии", category: "Молитвы", price: 500, duration: 30 },
                { id: 6, name: "Панихида", description: "Панихида об упокоении", category: "Молитвы", price: 500, duration: 30 },
                { id: 7, name: "Освящение автомобиля", description: "Освящение транспортного средства", category: "Освящение", price: 1000, duration: 30 },
                { id: 8, name: "Освящение жилья", description: "Освящение квартиры или дома", category: "Освящение", price: 2000, duration: 60 }
            ]
        },
        {
            id: 1,
            name: "Храм Василия Блаженного",
            type: "church",
            description: "Собор Покрова Пресвятой Богородицы, что на Рву. Памятник русской архитектуры.",
            address: "Красная площадь, 2, Москва",
            distance: "2.5 км",
            rating: 4.9,
            reviews: 3500,
            phone: "+7 495 698-83-04",
            lat: 55.7525,
            lng: 37.6231,
            image: "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Saint_Basil%27s_Cathedral_2017.jpg/400px-Saint_Basil%27s_Cathedral_2017.jpg",
            workingHours: {
                monday: "10:00-18:00",
                tuesday: "10:00-18:00",
                wednesday: "10:00-18:00",
                thursday: "10:00-18:00",
                friday: "10:00-18:00",
                saturday: "10:00-18:00",
                sunday: "10:00-18:00"
            },
            services: [
                { id: 1, name: "Крещение", description: "Таинство крещения", category: "Таинства", price: 3000, duration: 60 },
                { id: 3, name: "Записка о здравии", description: "Поминовение о здравии", category: "Записки", price: 50, duration: 0 },
                { id: 5, name: "Молебен", description: "Молебен о здравии", category: "Молитвы", price: 500, duration: 30 }
            ]
        },
        {
            id: 2,
            name: "Казанский собор",
            type: "church",
            description: "Собор Казанской иконы Божией Матери на Красной площади.",
            address: "Никольская ул., 3, Москва",
            distance: "2.3 км",
            rating: 4.8,
            reviews: 980,
            phone: "+7 495 698-27-26",
            lat: 55.7554,
            lng: 37.6188,
            image: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/Kazan_Cathedral_Moscow.jpg/400px-Kazan_Cathedral_Moscow.jpg",
            workingHours: {
                monday: "08:00-20:00",
                tuesday: "08:00-20:00",
                wednesday: "08:00-20:00",
                thursday: "08:00-20:00",
                friday: "08:00-20:00",
                saturday: "08:00-20:00",
                sunday: "07:00-20:00"
            },
            services: [
                { id: 1, name: "Крещение", description: "Таинство крещения", category: "Таинства", price: 2500, duration: 60 },
                { id: 2, name: "Венчание", description: "Таинство венчания", category: "Таинства", price: 4000, duration: 90 },
                { id: 3, name: "Записка о здравии", description: "Поминовение о здравии", category: "Записки", price: 50, duration: 0 },
                { id: 5, name: "Молебен", description: "Молебен о здравии", category: "Молитвы", price: 500, duration: 30 }
            ]
        },
        {
            id: 3,
            name: "Новодевичий монастырь",
            type: "church",
            description: "Православный женский монастырь, объект Всемирного наследия ЮНЕСКО.",
            address: "Новодевичий пр., 1, Москва",
            distance: "4.5 км",
            rating: 4.9,
            reviews: 2100,
            phone: "+7 499 246-85-26",
            lat: 55.7263,
            lng: 37.5564,
            image: "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/Novodevichy_Convent.jpg/400px-Novodevichy_Convent.jpg",
            workingHours: {
                monday: "09:00-17:00",
                tuesday: "09:00-17:00",
                wednesday: "09:00-17:00",
                thursday: "09:00-17:00",
                friday: "09:00-17:00",
                saturday: "09:00-17:00",
                sunday: "07:00-17:00"
            },
            services: [
                { id: 1, name: "Крещение", description: "Таинство крещения", category: "Таинства", price: 3000, duration: 60 },
                { id: 3, name: "Записка о здравии", description: "Поминовение о здравии", category: "Записки", price: 50, duration: 0 },
                { id: 5, name: "Молебен", description: "Молебен о здравии", category: "Молитвы", price: 500, duration: 30 },
                { id: 6, name: "Панихида", description: "Панихида об упокоении", category: "Молитвы", price: 500, duration: 30 }
            ]
        }
    ],

    // Купели для крещения
    fonts: [
        {
            id: 100,
            name: "Купель Храма Христа Спасителя",
            type: "font",
            description: "Крестильный храм при Кафедральном соборе. Современная купель с полным погружением.",
            address: "ул. Волхонка, 15, Москва",
            distance: "1.2 км",
            rating: 4.9,
            reviews: 450,
            phone: "+7 495 952-40-54",
            lat: 55.7445,
            lng: 37.6050,
            image: "",
            workingHours: {
                monday: "09:00-18:00",
                tuesday: "09:00-18:00",
                wednesday: "09:00-18:00",
                thursday: "09:00-18:00",
                friday: "09:00-18:00",
                saturday: "09:00-18:00",
                sunday: "10:00-16:00"
            },
            services: [
                { id: 1, name: "Крещение взрослых", description: "Таинство крещения с полным погружением", category: "Таинства", price: 3500, duration: 90 },
                { id: 2, name: "Крещение детей", description: "Таинство крещения для детей", category: "Таинства", price: 3000, duration: 60 }
            ]
        },
        {
            id: 101,
            name: "Купель храма Сергия Радонежского",
            type: "font",
            description: "Крестильня при храме. Купель для крещения с погружением.",
            address: "Крапивенский пер., 4, Москва",
            distance: "3.1 км",
            rating: 4.7,
            reviews: 230,
            phone: "+7 495 621-39-86",
            lat: 55.7650,
            lng: 37.6280,
            image: "",
            workingHours: {
                monday: "10:00-17:00",
                tuesday: "10:00-17:00",
                wednesday: "10:00-17:00",
                thursday: "10:00-17:00",
                friday: "10:00-17:00",
                saturday: "10:00-17:00",
                sunday: "12:00-16:00"
            },
            services: [
                { id: 1, name: "Крещение взрослых", description: "Таинство крещения с погружением", category: "Таинства", price: 2500, duration: 90 },
                { id: 2, name: "Крещение детей", description: "Таинство крещения для детей", category: "Таинства", price: 2000, duration: 60 }
            ]
        },
        {
            id: 102,
            name: "Крестильный храм при Даниловом монастыре",
            type: "font",
            description: "Крестильный храм во имя преподобного Серафима Саровского.",
            address: "Даниловский вал, 22, Москва",
            distance: "5.2 км",
            rating: 4.8,
            reviews: 340,
            phone: "+7 495 952-34-08",
            lat: 55.7108,
            lng: 37.6308,
            image: "",
            workingHours: {
                monday: "09:00-18:00",
                tuesday: "09:00-18:00",
                wednesday: "09:00-18:00",
                thursday: "09:00-18:00",
                friday: "09:00-18:00",
                saturday: "09:00-18:00",
                sunday: "08:00-18:00"
            },
            services: [
                { id: 1, name: "Крещение взрослых", description: "Таинство крещения с полным погружением", category: "Таинства", price: 3000, duration: 90 },
                { id: 2, name: "Крещение детей", description: "Таинство крещения для детей", category: "Таинства", price: 2500, duration: 60 }
            ]
        }
    ],

    prayers: {
        'otche-nash': {
            title: 'Отче наш',
            text: `Отче наш, Иже еси на небесех!
Да святится имя Твое,
да приидет Царствие Твое,
да будет воля Твоя,
яко на небеси и на земли.
Хлеб наш насущный даждь нам днесь;
и остави нам долги наша,
якоже и мы оставляем должником нашим;
и не введи нас во искушение,
но избави нас от лукаваго.`
        },
        'simvol-very': {
            title: 'Символ веры',
            text: `Верую во единаго Бога Отца, Вседержителя,
Творца небу и земли, видимым же всем и невидимым.
И во единаго Господа Иисуса Христа,
Сына Божия, Единороднаго,
Иже от Отца рожденнаго прежде всех век;
Света от Света, Бога истинна от Бога истинна,
рожденна, несотворенна, единосущна Отцу,
Имже вся быша.`
        },
        'bogoroditse': {
            title: 'Богородице Дево, радуйся',
            text: `Богородице Дево, радуйся,
Благодатная Марие, Господь с Тобою;
благословена Ты в женах
и благословен плод чрева Твоего,
яко Спаса родила еси душ наших.`
        },
        'trisvatoe': {
            title: 'Трисвятое',
            text: `Святый Боже, Святый Крепкий,
Святый Безсмертный, помилуй нас.
(Читается трижды)`
        },
        'angel': {
            title: 'Ангелу Хранителю',
            text: `Ангеле Божий, хранителю мой святый,
на соблюдение мне от Бога с небесе данный,
прилежно молю тя:
ты мя днесь просвети,
и от всякаго зла сохрани,
ко благому деянию настави
и на путь спасения направи.
Аминь.`
        },
        'iisusova': {
            title: 'Иисусова молитва',
            text: `Господи Иисусе Христе,
Сыне Божий,
помилуй мя грешнаго.`
        }
    }
};

// ==========================================
// ORTHODOX CALENDAR API (ortox.ru)
// ==========================================

const calendarState = {
    currentDate: new Date(),
    selectedDate: new Date(),
    cachedData: {},
    isLoading: false,
    initialized: false
};

// Fetch calendar data - using pravoslavie.ru via script injection
async function fetchCalendarData(date) {
    const dateStr = formatDateKey(date);

    // Check cache first
    if (calendarState.cachedData[dateStr]) {
        return calendarState.cachedData[dateStr];
    }

    // Use local fallback data (always works, no CORS issues)
    const data = getFallbackCalendarData(date);
    calendarState.cachedData[dateStr] = data;
    return data;
}

// Format date for API (MMDD)
function formatDateForAPI(date) {
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${month}${day}`;
}

function formatDateKey(date) {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${year}-${month}-${day}`;
}

// Fallback data when API is not available
function getFallbackCalendarData(date) {
    const holidays = getOrthodoxHolidays(date.getFullYear());
    const dateKey = `${date.getMonth() + 1}-${date.getDate()}`;
    const holiday = holidays[dateKey];

    return {
        DATE: formatDateKey(date),
        TODAY_RUS_NEW: formatRussianDate(date, 'new'),
        TODAY_RUS_OLD: formatRussianDate(date, 'old'),
        SEDMICA: getSedmicaName(date),
        HOLIDAYS: holiday ? [{ NAME: holiday.name, TYPE: holiday.type }] : [],
        PERSONS: [],
        READING: null,
        FEOFAN: null
    };
}

function formatRussianDate(date, style) {
    const months = ['января', 'февраля', 'марта', 'апреля', 'мая', 'июня',
                    'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря'];
    const day = date.getDate();
    const month = months[date.getMonth()];
    const year = date.getFullYear();

    if (style === 'old') {
        // Old style is 13 days behind
        const oldDate = new Date(date);
        oldDate.setDate(oldDate.getDate() - 13);
        return `${oldDate.getDate()} ${months[oldDate.getMonth()]}`;
    }
    return `${day} ${month} ${year}`;
}

function getSedmicaName(date) {
    // Simplified sedmica calculation
    const pascha = calculatePascha(date.getFullYear());
    const diff = Math.floor((date - pascha) / (1000 * 60 * 60 * 24 * 7));

    if (diff >= 0 && diff < 7) {
        return `${diff + 1}-я седмица по Пасхе`;
    }
    return 'Седмица';
}

// Calculate Orthodox Pascha (Easter) date
function calculatePascha(year) {
    const a = year % 19;
    const b = year % 4;
    const c = year % 7;
    const d = (19 * a + 15) % 30;
    const e = (2 * b + 4 * c + 6 * d + 6) % 7;

    let day = 22 + d + e;
    let month = 3; // March

    if (day > 31) {
        day -= 31;
        month = 4; // April
    }

    // Add 13 days for Julian to Gregorian conversion
    const julianDate = new Date(year, month - 1, day);
    julianDate.setDate(julianDate.getDate() + 13);

    return julianDate;
}

// Orthodox fixed and movable holidays
function getOrthodoxHolidays(year) {
    const pascha = calculatePascha(year);

    // Fixed holidays (month-day format)
    const fixed = {
        '1-7': { name: 'Рождество Христово', type: 'great' },
        '1-14': { name: 'Обрезание Господне', type: 'great' },
        '1-19': { name: 'Крещение Господне (Богоявление)', type: 'great' },
        '2-15': { name: 'Сретение Господне', type: 'great' },
        '4-7': { name: 'Благовещение Пресвятой Богородицы', type: 'great' },
        '5-22': { name: 'Перенесение мощей святителя Николая', type: 'holiday' },
        '6-7': { name: 'Третье обретение главы Иоанна Предтечи', type: 'holiday' },
        '7-7': { name: 'Рождество Иоанна Предтечи', type: 'great' },
        '7-12': { name: 'Память апп. Петра и Павла', type: 'great' },
        '8-14': { name: 'Происхождение честных древ Креста', type: 'holiday' },
        '8-19': { name: 'Преображение Господне', type: 'great' },
        '8-28': { name: 'Успение Пресвятой Богородицы', type: 'great' },
        '9-11': { name: 'Усекновение главы Иоанна Предтечи', type: 'great' },
        '9-21': { name: 'Рождество Пресвятой Богородицы', type: 'great' },
        '9-27': { name: 'Воздвижение Креста Господня', type: 'great' },
        '10-14': { name: 'Покров Пресвятой Богородицы', type: 'great' },
        '11-4': { name: 'Казанская икона Божией Матери', type: 'holiday' },
        '11-21': { name: 'Собор Архистратига Михаила', type: 'holiday' },
        '12-4': { name: 'Введение во храм Пресвятой Богородицы', type: 'great' },
        '12-19': { name: 'Святитель Николай Чудотворец', type: 'great' }
    };

    // Movable holidays (relative to Pascha)
    const movable = {};

    // Palm Sunday (Entry into Jerusalem) - 7 days before Pascha
    const palmSunday = new Date(pascha);
    palmSunday.setDate(palmSunday.getDate() - 7);
    movable[`${palmSunday.getMonth() + 1}-${palmSunday.getDate()}`] = {
        name: 'Вход Господень в Иерусалим (Вербное воскресенье)',
        type: 'great'
    };

    // Pascha
    movable[`${pascha.getMonth() + 1}-${pascha.getDate()}`] = {
        name: 'Светлое Христово Воскресение (Пасха)',
        type: 'pascha'
    };

    // Ascension - 39 days after Pascha
    const ascension = new Date(pascha);
    ascension.setDate(ascension.getDate() + 39);
    movable[`${ascension.getMonth() + 1}-${ascension.getDate()}`] = {
        name: 'Вознесение Господне',
        type: 'great'
    };

    // Pentecost (Trinity) - 49 days after Pascha
    const pentecost = new Date(pascha);
    pentecost.setDate(pentecost.getDate() + 49);
    movable[`${pentecost.getMonth() + 1}-${pentecost.getDate()}`] = {
        name: 'День Святой Троицы (Пятидесятница)',
        type: 'great'
    };

    return { ...fixed, ...movable };
}

// Render calendar UI
async function renderCalendar() {
    const date = calendarState.selectedDate;
    const year = date.getFullYear();
    const month = date.getMonth();

    // Update month/year header
    const monthNames = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
                        'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
    const monthYearEl = document.querySelector('.month-year');
    if (monthYearEl) {
        monthYearEl.textContent = `${monthNames[month]} ${year}`;
    }

    // Get holidays for this year
    const holidays = getOrthodoxHolidays(year);

    // Render calendar grid
    renderCalendarGrid(year, month, holidays);

    // Load and render day details
    await renderDayDetails(date);
}

function renderCalendarGrid(year, month, holidays) {
    const gridEl = document.querySelector('.calendar-grid');
    if (!gridEl) return;

    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startDayOfWeek = (firstDay.getDay() + 6) % 7; // Monday = 0
    const daysInMonth = lastDay.getDate();
    const prevMonthLastDay = new Date(year, month, 0).getDate();

    const today = new Date();
    const todayStr = `${today.getFullYear()}-${today.getMonth()}-${today.getDate()}`;
    const selectedStr = `${calendarState.selectedDate.getFullYear()}-${calendarState.selectedDate.getMonth()}-${calendarState.selectedDate.getDate()}`;

    let html = `
        <div class="day-name">Пн</div>
        <div class="day-name">Вт</div>
        <div class="day-name">Ср</div>
        <div class="day-name">Чт</div>
        <div class="day-name">Пт</div>
        <div class="day-name">Сб</div>
        <div class="day-name">Вс</div>
    `;

    // Previous month days
    for (let i = startDayOfWeek - 1; i >= 0; i--) {
        const day = prevMonthLastDay - i;
        html += `<div class="day-cell other">${day}</div>`;
    }

    // Current month days
    for (let day = 1; day <= daysInMonth; day++) {
        const dateStr = `${year}-${month}-${day}`;
        const holidayKey = `${month + 1}-${day}`;
        const holiday = holidays[holidayKey];

        let classes = 'day-cell';
        if (dateStr === todayStr) classes += ' today';
        if (dateStr === selectedStr) classes += ' selected';
        if (holiday) {
            classes += holiday.type === 'great' || holiday.type === 'pascha' ? ' holiday' : ' event';
        }

        html += `<div class="${classes}" data-date="${year}-${month + 1}-${day}">${day}</div>`;
    }

    // Next month days
    const cellsUsed = startDayOfWeek + daysInMonth;
    const remaining = 42 - cellsUsed;
    for (let day = 1; day <= remaining && day <= 14; day++) {
        html += `<div class="day-cell other">${day}</div>`;
    }

    gridEl.innerHTML = html;

    // Add click handlers
    gridEl.querySelectorAll('.day-cell:not(.other)').forEach(cell => {
        cell.addEventListener('click', async () => {
            const [y, m, d] = cell.dataset.date.split('-').map(Number);
            calendarState.selectedDate = new Date(y, m - 1, d);
            await renderCalendar();
        });
    });
}

async function renderDayDetails(date) {
    const holidaysContainer = document.querySelector('.upcoming-holidays');
    if (!holidaysContainer) return;

    // Show loading
    holidaysContainer.innerHTML = '<div class="loading-inline"><i class="fas fa-spinner fa-spin"></i> Загрузка...</div>';

    try {
        // Get calendar data (local calculation)
        const data = getFallbackCalendarData(date);

        let html = `<h3>События на ${formatRussianDate(date, 'new')}</h3>`;

        // Date info
        if (data.TODAY_RUS_OLD || data.SEDMICA) {
            html += `
                <div class="calendar-date-info">
                    ${data.TODAY_RUS_OLD ? `<p><strong>По ст. стилю:</strong> ${data.TODAY_RUS_OLD}</p>` : ''}
                    ${data.SEDMICA ? `<p><strong>Седмица:</strong> ${data.SEDMICA}</p>` : ''}
                </div>
            `;
        }

        // Holidays
        if (data.HOLIDAYS && data.HOLIDAYS.length > 0) {
            html += '<div class="calendar-section"><h4>Праздники</h4>';
            data.HOLIDAYS.forEach(holiday => {
                html += `
                    <div class="holiday-card orthodox">
                        <div class="holiday-icon"><i class="fas fa-cross"></i></div>
                        <div class="holiday-info">
                            <h4>${holiday.NAME}</h4>
                            ${holiday.URL ? `<a href="${holiday.URL}" target="_blank" class="holiday-link">Подробнее</a>` : ''}
                        </div>
                    </div>
                `;
            });
            html += '</div>';
        }

        // Saints (PERSONS)
        if (data.PERSONS && data.PERSONS.length > 0) {
            html += '<div class="calendar-section"><h4>Память святых</h4>';
            data.PERSONS.slice(0, 5).forEach(person => {
                html += `
                    <div class="saint-card">
                        ${person.PICTURE ? `<img src="${person.PICTURE}" alt="${person.NAME}" class="saint-image" onerror="this.style.display='none'">` :
                          '<div class="saint-icon"><i class="fas fa-user-circle"></i></div>'}
                        <div class="saint-info">
                            <h5>${person.NAME}</h5>
                            ${person.URL ? `<a href="${person.URL}" target="_blank" class="saint-link">Подробнее</a>` : ''}
                        </div>
                    </div>
                `;
            });
            if (data.PERSONS.length > 5) {
                html += `<p class="more-saints">И ещё ${data.PERSONS.length - 5} святых...</p>`;
            }
            html += '</div>';
        }

        // Scripture readings
        if (data.READING) {
            html += `
                <div class="calendar-section">
                    <h4>Чтения Священного Писания</h4>
                    <div class="reading-card">
                        <i class="fas fa-book-open"></i>
                        <span>${data.READING.NAME || 'Доступно чтение'}</span>
                    </div>
                </div>
            `;
        }

        // Feofan Zatvornik thoughts
        if (data.FEOFAN && data.FEOFAN.DETAIL_TEXT) {
            html += `
                <div class="calendar-section">
                    <h4>Мысли свт. Феофана Затворника</h4>
                    <div class="feofan-card">
                        <div class="feofan-text">${data.FEOFAN.DETAIL_TEXT.substring(0, 300)}...</div>
                        ${data.FEOFAN.DETAIL_PAGE_URL ? `<a href="https://ortox.ru${data.FEOFAN.DETAIL_PAGE_URL}" target="_blank">Читать полностью</a>` : ''}
                    </div>
                </div>
            `;
        }

        // If no content
        if (!html.includes('calendar-section')) {
            html += '<p class="no-events">На этот день нет особых событий</p>';
        }

        holidaysContainer.innerHTML = html;

    } catch (error) {
        console.error('Error rendering day details:', error);
        holidaysContainer.innerHTML = `
            <h3>События на ${formatRussianDate(date, 'new')}</h3>
            <p class="error-message">Не удалось загрузить данные календаря</p>
        `;
    }
}

// Initialize calendar
function initCalendar() {
    // Month navigation
    const prevBtn = document.getElementById('prev-month');
    const nextBtn = document.getElementById('next-month');

    if (prevBtn) {
        prevBtn.addEventListener('click', async () => {
            calendarState.selectedDate.setMonth(calendarState.selectedDate.getMonth() - 1);
            await renderCalendar();
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', async () => {
            calendarState.selectedDate.setMonth(calendarState.selectedDate.getMonth() + 1);
            await renderCalendar();
        });
    }

    // Initial render when calendar screen is first shown
    // We'll render on first navigation to calendar screen
}

// Fasting periods calculation
function getFastingPeriods(year) {
    const pascha = calculatePascha(year);
    const fasts = [];

    // Великий пост (Great Lent) - starts 48 days before Pascha, ends day before Pascha
    const greatLentStart = new Date(pascha);
    greatLentStart.setDate(greatLentStart.getDate() - 48);
    const greatLentEnd = new Date(pascha);
    greatLentEnd.setDate(greatLentEnd.getDate() - 1);
    fasts.push({
        name: 'Великий пост',
        start: greatLentStart,
        end: greatLentEnd,
        type: 'strict'
    });

    // Петров пост (Apostles' Fast) - starts Monday after All Saints, ends July 11
    const pentecost = new Date(pascha);
    pentecost.setDate(pentecost.getDate() + 49);
    const allSaints = new Date(pentecost);
    allSaints.setDate(allSaints.getDate() + 7); // Next Sunday
    const petrovStart = new Date(allSaints);
    petrovStart.setDate(petrovStart.getDate() + 1); // Monday after
    const petrovEnd = new Date(year, 6, 11); // July 11
    if (petrovStart < petrovEnd) {
        fasts.push({
            name: 'Петров пост',
            start: petrovStart,
            end: petrovEnd,
            type: 'moderate'
        });
    }

    // Успенский пост (Dormition Fast) - August 14-27
    fasts.push({
        name: 'Успенский пост',
        start: new Date(year, 7, 14),
        end: new Date(year, 7, 27),
        type: 'strict'
    });

    // Рождественский пост (Nativity Fast) - November 28 - January 6
    fasts.push({
        name: 'Рождественский пост',
        start: new Date(year, 10, 28),
        end: new Date(year + 1, 0, 6),
        type: 'moderate'
    });

    // Also check if we're in a fast from previous year
    const prevFast = {
        name: 'Рождественский пост',
        start: new Date(year - 1, 10, 28),
        end: new Date(year, 0, 6),
        type: 'moderate'
    };
    fasts.unshift(prevFast);

    return fasts;
}

function getCurrentFast(date) {
    const fasts = getFastingPeriods(date.getFullYear());

    for (const fast of fasts) {
        if (date >= fast.start && date <= fast.end) {
            return fast;
        }
    }
    return null;
}

// Update home screen with calendar data
function updateHomeScreen() {
    try {
        const today = new Date();

        // Russian weekday names
        const weekdays = ['воскресенье', 'понедельник', 'вторник', 'среда', 'четверг', 'пятница', 'суббота'];
        const months = ['января', 'февраля', 'марта', 'апреля', 'мая', 'июня',
                        'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря'];

        // Format date: "29 января, среда"
        const dateStr = `${today.getDate()} ${months[today.getMonth()]}, ${weekdays[today.getDay()]}`;
        const dateTitleEl = document.getElementById('home-date-title');
        if (dateTitleEl) {
            dateTitleEl.textContent = dateStr;
        }

        // Get calendar data (uses local calculations, no API needed)
        const calendarData = getFallbackCalendarData(today);

    // Display holiday if exists
    const holidayEl = document.getElementById('home-holiday');
    if (holidayEl) {
        if (calendarData.HOLIDAYS && calendarData.HOLIDAYS.length > 0) {
            const holiday = calendarData.HOLIDAYS[0];
            holidayEl.textContent = holiday.NAME || holiday.name || '';
            holidayEl.style.display = 'block';
        } else {
            // Check fallback holidays
            const holidays = getOrthodoxHolidays(today.getFullYear());
            const dateKey = `${today.getMonth() + 1}-${today.getDate()}`;
            if (holidays[dateKey]) {
                holidayEl.textContent = holidays[dateKey].name;
                holidayEl.style.display = 'block';
            } else {
                holidayEl.style.display = 'none';
            }
        }
    }

    // Featured event - show post, праздник, or sedmitsa
    const featuredTitleEl = document.getElementById('featured-title');
    const featuredMetaEl = document.getElementById('featured-meta');
    const featuredIconEl = document.querySelector('.featured-icon i');

    if (featuredTitleEl && featuredMetaEl) {
        // Priority: 1. Current fast, 2. Upcoming holiday, 3. Current sedmitsa
        const currentFast = getCurrentFast(today);

        if (currentFast) {
            featuredTitleEl.textContent = currentFast.name;
            const daysLeft = Math.ceil((currentFast.end - today) / (1000 * 60 * 60 * 24));
            featuredMetaEl.innerHTML = `<span class="featured-tag">До ${daysLeft} дн.</span>`;
            if (featuredIconEl) {
                featuredIconEl.className = 'fas fa-cross';
            }
        } else {
            // Check for upcoming holidays
            const upcomingHoliday = getNextHoliday(today);
            if (upcomingHoliday) {
                featuredTitleEl.textContent = upcomingHoliday.name;
                const daysUntil = Math.ceil((upcomingHoliday.date - today) / (1000 * 60 * 60 * 24));
                const dateStr = `${upcomingHoliday.date.getDate()} ${months[upcomingHoliday.date.getMonth()]}`;
                featuredMetaEl.innerHTML = `<span class="featured-tag">${dateStr}</span><span class="featured-days">через ${daysUntil} дн.</span>`;
                if (featuredIconEl) {
                    featuredIconEl.className = 'fas fa-church';
                }
            } else {
                // Show sedmitsa
                const sedmica = calendarData.SEDMICA || getSedmicaName(today);
                featuredTitleEl.textContent = sedmica;
                featuredMetaEl.innerHTML = '';
                if (featuredIconEl) {
                    featuredIconEl.className = 'fas fa-calendar-alt';
                }
            }
        }
    }
    } catch (error) {
        console.error('Error updating home screen:', error);
        // Set fallback values
        const featuredTitleEl = document.getElementById('featured-title');
        if (featuredTitleEl) {
            featuredTitleEl.textContent = 'Православный календарь';
        }
    }
}

// Get next upcoming holiday
function getNextHoliday(fromDate) {
    const holidays = getOrthodoxHolidays(fromDate.getFullYear());
    const nextYearHolidays = getOrthodoxHolidays(fromDate.getFullYear() + 1);

    let nearestHoliday = null;
    let nearestDate = null;

    const checkHolidays = (holidayMap, year) => {
        for (const [key, holiday] of Object.entries(holidayMap)) {
            const [month, day] = key.split('-').map(Number);
            const holidayDate = new Date(year, month - 1, day);

            if (holidayDate > fromDate) {
                if (!nearestDate || holidayDate < nearestDate) {
                    nearestDate = holidayDate;
                    nearestHoliday = { ...holiday, date: holidayDate };
                }
            }
        }
    };

    checkHolidays(holidays, fromDate.getFullYear());

    // If no holiday found this year, check next year
    if (!nearestHoliday) {
        checkHolidays(nextYearHolidays, fromDate.getFullYear() + 1);
    }

    return nearestHoliday;
}

// Render calendar when screen is shown
function onCalendarScreenShow() {
    if (!calendarState.initialized) {
        calendarState.initialized = true;
        renderCalendar();
    }
}

// DOM Elements
const elements = {
    header: document.getElementById('app-header'),
    headerTitle: document.getElementById('header-title'),
    backBtn: document.getElementById('back-btn'),
    content: document.getElementById('app-content'),
    bottomNav: document.getElementById('bottom-nav'),
    toast: document.getElementById('toast'),
    toastMessage: document.getElementById('toast-message'),
    loading: document.getElementById('loading')
};

// Screen titles
const screenTitles = {
    'home': 'Главная',
    'map': 'Карта храмов',
    'church': 'Храм',
    'booking': 'Бронирование',
    'payment': 'Оплата',
    'success': 'Успех',
    'orders': 'Мои заказы',
    'calendar': 'Календарь',
    'prayers': 'Молитвы',
    'prayer-detail': 'Молитва',
    'donate': 'Пожертвование',
    'profile': 'Профиль'
};

// ==========================================
// 2GIS MAP INTEGRATION
// ==========================================

let mapInitialized = false;
let mapIsLoading = false;
let mapLoadTimeout = null;

function initMap() {
    const mapContainer = document.getElementById('map-2gis');
    const mapLoading = document.getElementById('map-loading');

    if (!mapContainer) {
        console.error('Map container not found');
        return;
    }

    // Prevent multiple initializations
    if (mapInitialized && map) {
        console.log('Map already initialized, skipping...');
        return;
    }

    if (mapIsLoading) {
        console.log('Map is loading, skipping...');
        return;
    }

    // Check if mapgl is available, fallback to Leaflet
    if (typeof mapgl === 'undefined') {
        console.warn('2GIS MapGL not available, trying Leaflet fallback...');
        initLeafletMap();
        return;
    }

    console.log('Initializing 2GIS map...');
    console.log('API Key:', DGIS_API_KEY);
    console.log('Position:', currentPosition);

    // Destroy existing map if any
    if (map) {
        try {
            map.destroy();
            map = null;
        } catch (e) {
            console.log('Error destroying map:', e);
        }
    }

    // Clear the container
    mapContainer.innerHTML = '';
    mapInitialized = false;
    mapIsLoading = true;

    if (mapLoading) mapLoading.style.display = 'flex';

    // Set timeout for loading - fallback to Leaflet after 10 seconds
    if (mapLoadTimeout) clearTimeout(mapLoadTimeout);
    mapLoadTimeout = setTimeout(() => {
        if (mapIsLoading && !mapInitialized) {
            console.warn('2GIS map load timeout, switching to Leaflet...');
            mapIsLoading = false;
            // Destroy 2GIS map attempt
            if (map) {
                try { map.destroy(); } catch(e) {}
                map = null;
            }
            // Try Leaflet instead
            initLeafletMap();
        }
    }, 10000);

    try {
        // Initialize 2GIS MapGL
        map = new mapgl.Map('map-2gis', {
            center: [currentPosition.lng, currentPosition.lat],
            zoom: 14,
            key: DGIS_API_KEY
        });

        console.log('Map object created, waiting for load event...');

        map.on('load', () => {
            console.log('2GIS Map loaded successfully!');
            if (mapLoadTimeout) clearTimeout(mapLoadTimeout);
            if (mapLoading) mapLoading.style.display = 'none';
            mapInitialized = true;
            mapIsLoading = false;

            // Prevent map from centering on click
            map.on('click', (e) => {
                // Do nothing - prevent default centering behavior
            });

            // Add default church markers
            addDefaultMarkers();

            // Load and add OSM places
            loadOSMPlaces().then(() => {
                if (osmPlacesData) {
                    addOSMMarkersToMap();
                }
            });

            // Search for real places from 2GIS
            searchReligiousPlaces();

            showToast('Карта загружена');
        });

        map.on('error', (e) => {
            console.error('2GIS Map error:', e);
            if (mapLoadTimeout) clearTimeout(mapLoadTimeout);
            mapIsLoading = false;
            // Try Leaflet fallback
            if (map) {
                try { map.destroy(); } catch(ex) {}
                map = null;
            }
            console.log('Switching to Leaflet fallback...');
            initLeafletMap();
        });

    } catch (error) {
        console.error('Failed to initialize 2GIS map:', error);
        if (mapLoadTimeout) clearTimeout(mapLoadTimeout);
        mapIsLoading = false;
        // Try Leaflet fallback
        console.log('Switching to Leaflet fallback...');
        initLeafletMap();
    }
}

function addDefaultMarkers() {
    if (!map) return;

    // Clear existing markers
    markers.forEach(m => {
        try { m.destroy(); } catch(e) {}
    });
    markers = [];

    // Add markers for default churches
    appData.churches.forEach((church, index) => {
        try {
            const marker = new mapgl.Marker(map, {
                coordinates: [church.lng, church.lat],
                icon: createMarkerIcon(church.type || 'church', true),
                size: [56, 70],
                anchor: [28, 70]
            });

            marker.on('click', (e) => {
                if (e && e.originalEvent) {
                    e.originalEvent.stopPropagation();
                }
                openChurch(index);
            });

            markers.push(marker);

            // Add pulsing circle under marker
            addPulsingCircle(church.lng, church.lat, church.type || 'church');
        } catch(e) {
            console.error('Error adding marker:', e);
        }
    });

    // Add markers for fonts
    appData.fonts.forEach((font, index) => {
        try {
            const marker = new mapgl.Marker(map, {
                coordinates: [font.lng, font.lat],
                icon: createMarkerIcon('font', true),
                size: [56, 70],
                anchor: [28, 70]
            });

            marker.on('click', (e) => {
                if (e && e.originalEvent) {
                    e.originalEvent.stopPropagation();
                }
                openFont(index);
            });

            markers.push(marker);

            // Add pulsing circle under marker
            addPulsingCircle(font.lng, font.lat, 'font');
        } catch(e) {
            console.error('Error adding font marker:', e);
        }
    });
}

function addSingleMarker(place, index, type) {
    if (!map) return;

    try {
        if (useLeaflet) {
            // Leaflet marker
            const colors = {
                church: '#C41E3A',
                font: '#1E90FF'
            };
            const icons = {
                church: '☦',
                font: '💧'
            };
            const color = colors[type] || colors.church;
            const icon = icons[type] || icons.church;

            const customIcon = L.divIcon({
                html: `
                    <div style="
                        background: ${color};
                        width: 40px;
                        height: 40px;
                        border-radius: 50% 50% 50% 0;
                        transform: rotate(-45deg);
                        border: 3px solid white;
                        box-shadow: 0 3px 10px rgba(0,0,0,0.3);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    ">
                        <span style="
                            transform: rotate(45deg);
                            font-size: 18px;
                            color: white;
                        ">${icon}</span>
                    </div>
                `,
                className: 'custom-marker',
                iconSize: [40, 40],
                iconAnchor: [20, 40],
                popupAnchor: [0, -40]
            });

            const marker = L.marker([place.lat, place.lng], { icon: customIcon })
                .addTo(map)
                .bindPopup(`
                    <div style="min-width: 200px;">
                        <h3 style="margin: 0 0 8px 0; font-size: 14px;">${place.name}</h3>
                        <p style="margin: 0 0 8px 0; color: #666; font-size: 12px;">${place.address}</p>
                        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;">
                            <span style="color: #FFE473;">★</span>
                            <span style="font-size: 13px;">${place.rating}</span>
                        </div>
                        <button onclick="${type === 'font' ? 'openFont' : 'openChurch'}(${index})" style="
                            width: 100%;
                            padding: 8px;
                            background: ${color};
                            color: white;
                            border: none;
                            border-radius: 6px;
                            cursor: pointer;
                            font-size: 13px;
                        ">Подробнее</button>
                    </div>
                `);

            markers.push(marker);
        } else {
            // 2GIS marker
            const marker = new mapgl.Marker(map, {
                coordinates: [place.lng, place.lat],
                icon: createMarkerIcon(type, true),
                size: [56, 70],
                anchor: [28, 70]
            });

            marker.on('click', (e) => {
                if (e && e.originalEvent) {
                    e.originalEvent.stopPropagation();
                }
                if (type === 'font') {
                    openFont(index);
                } else {
                    openChurch(index);
                }
            });

            markers.push(marker);
            addPulsingCircle(place.lng, place.lat, type);
        }
    } catch(e) {
        console.error('Error adding marker:', e);
    }
}

function createMarkerIcon(type, isLarge = false) {
    const colors = {
        church: { main: '#C41E3A', gradient: '#8B0000' },  // Deep red for churches
        font: { main: '#1E90FF', gradient: '#0066CC' }      // Blue for fonts
    };
    const c = colors[type] || colors.church;

    const size = isLarge ? 56 : 44;
    const iconSize = isLarge ? 24 : 18;

    // Create pin-shaped SVG marker with gradient and shadow
    const svg = `
        <svg width="${size}" height="${size + 14}" viewBox="0 0 ${size} ${size + 14}" xmlns="http://www.w3.org/2000/svg">
            <defs>
                <linearGradient id="grad_${type}" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" style="stop-color:${c.main};stop-opacity:1" />
                    <stop offset="100%" style="stop-color:${c.gradient};stop-opacity:1" />
                </linearGradient>
                <filter id="shadow_${type}" x="-20%" y="-20%" width="140%" height="140%">
                    <feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.4"/>
                </filter>
            </defs>
            <!-- Pin shape -->
            <path d="M${size/2} ${size + 10}
                     C${size/2} ${size + 10} ${size - 4} ${size/2 + 8} ${size - 4} ${size/2}
                     C${size - 4} ${size/4} ${size * 0.75} 4 ${size/2} 4
                     C${size * 0.25} 4 4 ${size/4} 4 ${size/2}
                     C4 ${size/2 + 8} ${size/2} ${size + 10} ${size/2} ${size + 10}Z"
                  fill="url(#grad_${type})"
                  stroke="white"
                  stroke-width="3"
                  filter="url(#shadow_${type})"/>
            <!-- Inner circle -->
            <circle cx="${size/2}" cy="${size/2}" r="${size/3}" fill="white" opacity="0.95"/>
            <!-- Icon -->
            <text x="${size/2}" y="${size/2 + iconSize/3}" text-anchor="middle" fill="${c.main}" font-size="${iconSize}" font-weight="bold" font-family="Arial">
                ${type === 'font' ? '💧' : '☦'}
            </text>
        </svg>
    `;

    return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)));
}

// Add pulsing circle effect around markers
function addPulsingCircle(lng, lat, type) {
    if (!map) return;

    const colors = {
        church: 'rgba(196, 30, 58, 0.3)',
        font: 'rgba(30, 144, 255, 0.3)'
    };
    const color = colors[type] || colors.church;

    try {
        // Add a circle layer for visual emphasis
        const circleId = `pulse_${lng}_${lat}`.replace(/\./g, '_');

        map.addLayer({
            id: circleId,
            type: 'circle',
            filter: ['==', ['get', 'id'], circleId],
            paint: {
                'circle-radius': 30,
                'circle-color': color,
                'circle-opacity': 0.6
            }
        });
    } catch(e) {
        // Layers may not be supported, ignore
    }
}

function getTypeColor(type) {
    const colors = {
        church: '#C41E3A',
        font: '#1E90FF'
    };
    return colors[type] || colors.church;
}

// ==========================================
// OSM DATA LOADING (from pre-fetched JSON)
// ==========================================

async function loadOSMPlaces() {
    if (osmPlacesLoaded && osmPlacesData) {
        console.log('OSM data already loaded');
        return osmPlacesData;
    }

    // Try multiple paths
    const paths = [
        '../data/places-russia.json',
        'data/places-russia.json',
        '/data/places-russia.json'
    ];

    for (const path of paths) {
        try {
            console.log(`Trying to load OSM data from: ${path}`);
            const response = await fetch(path);

            if (!response.ok) {
                console.warn(`Path ${path} returned ${response.status}`);
                continue;
            }

            const data = await response.json();
            osmPlacesData = data;
            osmPlacesLoaded = true;

            const total = data.meta?.total_places || Object.values(data.regions || {}).flat().length;
            console.log(`OSM data loaded successfully from ${path}: ${total} places`);
            showToast(`Загружено ${total.toLocaleString()} мест`);
            return data;
        } catch (error) {
            console.warn(`Failed to load from ${path}:`, error.message);
        }
    }

    console.error('Could not load OSM places from any path');
    return null;
}

function getOSMPlacesForRegion(regionSlug) {
    if (!osmPlacesData || !osmPlacesData.regions) {
        return [];
    }
    return osmPlacesData.regions[regionSlug] || [];
}

function getOSMPlacesNearby(lat, lng, radiusKm = 30, types = null) {
    if (!osmPlacesData || !osmPlacesData.regions) {
        return [];
    }

    const allPlaces = [];

    // Collect places from all regions
    for (const [regionSlug, places] of Object.entries(osmPlacesData.regions)) {
        allPlaces.push(...places);
    }

    // Filter by distance and type
    return allPlaces.filter(place => {
        // Type filter
        if (types && types.length > 0 && !types.includes(place.type)) {
            return false;
        }

        // Distance filter
        const distance = calculateDistanceKm(lat, lng, place.lat, place.lng);
        return distance <= radiusKm;
    }).map(place => ({
        ...place,
        distance: calculateDistance(lat, lng, place.lat, place.lng)
    })).sort((a, b) => {
        // Sort by distance
        const distA = parseFloat(a.distance) || 999;
        const distB = parseFloat(b.distance) || 999;
        return distA - distB;
    });
}

function calculateDistanceKm(lat1, lng1, lat2, lng2) {
    const R = 6371; // Earth radius in km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
}

function detectRegionByCoords(lat, lng) {
    // Simple region detection based on coordinates
    // Moscow region
    if (lat >= 55.0 && lat <= 56.5 && lng >= 36.5 && lng <= 38.5) {
        return 'moscow';
    }
    // St. Petersburg region
    if (lat >= 59.5 && lat <= 60.5 && lng >= 29.5 && lng <= 31.0) {
        return 'spb';
    }
    // Default to Moscow
    return 'moscow';
}

async function addOSMMarkersToMap(types = null) {
    if (!map) {
        console.log('Map not ready for OSM markers');
        return;
    }

    // Load OSM data if not loaded
    await loadOSMPlaces();

    if (!osmPlacesData) {
        console.log('No OSM data available for markers');
        return;
    }

    console.log('Current position:', currentPosition);
    console.log('Filter types:', types);

    // Get places near current position (increased radius to 50km)
    const places = getOSMPlacesNearby(
        currentPosition.lat,
        currentPosition.lng,
        50, // 50km radius
        types
    );

    console.log(`Found ${places.length} OSM places within 50km`);
    if (places.length > 0) {
        console.log('First place:', places[0]);
    }

    // Add markers for each place
    places.forEach((place, index) => {
        try {
            const typeConfig = PLACE_TYPES[place.type] || PLACE_TYPES.church;

            if (useLeaflet) {
                // Leaflet marker
                const customIcon = L.divIcon({
                    html: `
                        <div style="
                            background: ${typeConfig.color};
                            width: 36px;
                            height: 36px;
                            border-radius: 50% 50% 50% 0;
                            transform: rotate(-45deg);
                            border: 2px solid white;
                            box-shadow: 0 2px 6px rgba(0,0,0,0.3);
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        ">
                            <span style="
                                transform: rotate(45deg);
                                font-size: 16px;
                                color: white;
                            ">${typeConfig.icon}</span>
                        </div>
                    `,
                    className: 'custom-marker osm-marker',
                    iconSize: [36, 36],
                    iconAnchor: [18, 36],
                    popupAnchor: [0, -36]
                });

                const marker = L.marker([place.lat, place.lng], { icon: customIcon })
                    .addTo(map)
                    .bindPopup(`
                        <div style="min-width: 180px;">
                            <h3 style="margin: 0 0 8px 0; font-size: 13px;">${place.name}</h3>
                            <p style="margin: 0 0 4px 0; color: #666; font-size: 11px;">${typeConfig.label}</p>
                            ${place.address ? `<p style="margin: 0 0 8px 0; color: #888; font-size: 11px;">${place.address}</p>` : ''}
                            <button onclick="openOSMPlace('${place.id}')" style="
                                width: 100%;
                                padding: 6px;
                                background: ${typeConfig.color};
                                color: white;
                                border: none;
                                border-radius: 4px;
                                cursor: pointer;
                                font-size: 12px;
                            ">Подробнее</button>
                        </div>
                    `);

                markers.push(marker);
            } else {
                // 2GIS marker
                const marker = new mapgl.Marker(map, {
                    coordinates: [place.lng, place.lat],
                    icon: createOSMMarkerIcon(place.type),
                    size: [40, 52],
                    anchor: [20, 52]
                });

                marker.on('click', (e) => {
                    if (e && e.originalEvent) {
                        e.originalEvent.stopPropagation();
                    }
                    openOSMPlace(place.id);
                });

                markers.push(marker);
            }
        } catch (e) {
            console.error('Error adding OSM marker:', e);
        }
    });

    // Update places count
    updatePlacesCount(places.length);
}

function createOSMMarkerIcon(type) {
    const typeConfig = PLACE_TYPES[type] || PLACE_TYPES.church;
    const size = 40;

    const svg = `
        <svg width="${size}" height="${size + 12}" viewBox="0 0 ${size} ${size + 12}" xmlns="http://www.w3.org/2000/svg">
            <defs>
                <filter id="osm_shadow" x="-20%" y="-20%" width="140%" height="140%">
                    <feDropShadow dx="0" dy="2" stdDeviation="2" flood-opacity="0.3"/>
                </filter>
            </defs>
            <path d="M${size/2} ${size + 8}
                     C${size/2} ${size + 8} ${size - 4} ${size/2 + 6} ${size - 4} ${size/2}
                     C${size - 4} ${size/4} ${size * 0.75} 4 ${size/2} 4
                     C${size * 0.25} 4 4 ${size/4} 4 ${size/2}
                     C4 ${size/2 + 6} ${size/2} ${size + 8} ${size/2} ${size + 8}Z"
                  fill="${typeConfig.color}"
                  stroke="white"
                  stroke-width="2"
                  filter="url(#osm_shadow)"/>
            <circle cx="${size/2}" cy="${size/2}" r="${size/3}" fill="white" opacity="0.9"/>
            <text x="${size/2}" y="${size/2 + 5}" text-anchor="middle" font-size="14">
                ${typeConfig.icon}
            </text>
        </svg>
    `;

    return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)));
}

function openOSMPlace(placeId) {
    if (!osmPlacesData || !osmPlacesData.regions) return;

    // Find place by ID
    let place = null;
    for (const [regionSlug, places] of Object.entries(osmPlacesData.regions)) {
        place = places.find(p => p.id === placeId);
        if (place) break;
    }

    if (!place) {
        console.error('Place not found:', placeId);
        return;
    }

    const typeConfig = PLACE_TYPES[place.type] || PLACE_TYPES.church;

    // Create church-like object for display
    const churchData = {
        id: place.id,
        name: place.name,
        type: place.type,
        typeName: typeConfig.label,
        description: `${typeConfig.label}${place.region ? `, ${place.region}` : ''}`,
        address: place.address || place.region || '',
        distance: place.distance || '',
        rating: 4.5,
        reviews: 0,
        phone: place.phone || '',
        email: '',
        website: place.website || '',
        lat: place.lat,
        lng: place.lng,
        image: '',
        workingHours: place.opening_hours ? parseOSMOpeningHours(place.opening_hours) : null,
        services: getDefaultServices(place.type),
        source: 'osm'
    };

    appData.selectedChurch = churchData;
    displayChurchDetails(churchData);
    navigateTo('church');
}

function parseOSMOpeningHours(hoursString) {
    // Simple parser for OSM opening_hours format
    // Full parsing would require a library like opening_hours.js
    return {
        monday: hoursString,
        tuesday: hoursString,
        wednesday: hoursString,
        thursday: hoursString,
        friday: hoursString,
        saturday: hoursString,
        sunday: hoursString
    };
}

// Search religious places using 2GIS Catalog API (only Orthodox)
async function searchReligiousPlaces(type = 'all') {
    const queries = [];

    // Search only Orthodox places
    if (type === 'all' || type === 'church') {
        queries.push({ query: 'храм церковь собор православный', type: 'church' });
    }
    if (type === 'all' || type === 'font') {
        queries.push({ query: 'крестильный храм купель крещение', type: 'font' });
    }

    const allPlaces = [];

    for (const q of queries) {
        try {
            const url = `https://catalog.api.2gis.com/3.0/items?q=${encodeURIComponent(q.query)}&point=${currentPosition.lng},${currentPosition.lat}&radius=5000&key=${DGIS_API_KEY}&fields=items.point,items.address,items.schedule,items.contact_groups,items.reviews,items.name_ex&page_size=20`;

            const response = await fetch(url);
            const data = await response.json();

            if (data.result && data.result.items) {
                const places = data.result.items.map(item => ({
                    id: item.id,
                    name: item.name,
                    type: q.type,
                    typeName: q.type === 'church' ? 'Храм' : 'Купель',
                    address: item.address_name || item.full_name || '',
                    lat: item.point?.lat,
                    lng: item.point?.lon,
                    phone: extractPhone(item.contact_groups),
                    schedule: item.schedule,
                    reviews: item.reviews?.general_review_count || 0,
                    rating: item.reviews?.general_rating || 0,
                    source: '2gis'
                })).filter(p => p.lat && p.lng);

                allPlaces.push(...places);
            }
        } catch (error) {
            console.error('Error fetching places:', error);
        }
    }

    if (allPlaces.length > 0) {
        appData.places2gis = allPlaces;
        addPlaceMarkers(allPlaces);
        updatePlacesList(allPlaces);
        updatePlacesCount(allPlaces.length);
    }
}

function extractPhone(contactGroups) {
    if (!contactGroups || !contactGroups.length) return '';
    for (const group of contactGroups) {
        if (group.contacts) {
            for (const contact of group.contacts) {
                if (contact.type === 'phone') {
                    return contact.value;
                }
            }
        }
    }
    return '';
}

function addPlaceMarkers(places) {
    if (!map) return;

    places.forEach((place, index) => {
        try {
            const marker = new mapgl.Marker(map, {
                coordinates: [place.lng, place.lat],
                icon: createMarkerIcon(place.type || 'church', false),
                size: [44, 58],
                anchor: [22, 58]
            });

            marker.on('click', (e) => {
                if (e && e.originalEvent) {
                    e.originalEvent.stopPropagation();
                }
                open2gisPlace(place);
            });

            markers.push(marker);
        } catch (e) {
            console.error('Error adding marker:', e);
        }
    });

    // Update count
    updatePlacesCount(places.length);

    // Show toast with count
    const total = places.length + appData.churches.length + appData.fonts.length;
    showToast(`Найдено ${total} православных мест`);
}

function open2gisPlace(place) {
    // Create a church-like object from 2GIS place
    const church = {
        id: place.id,
        name: place.name,
        type: place.type || 'church',
        typeName: place.typeName || 'Храм',
        description: `Найдено через 2GIS. ${place.address}`,
        address: place.address,
        distance: calculateDistance(currentPosition.lat, currentPosition.lng, place.lat, place.lng),
        rating: place.rating || 4.5,
        reviews: place.reviews || 0,
        phone: place.phone || 'Не указан',
        email: '',
        website: '',
        lat: place.lat,
        lng: place.lng,
        image: '',
        workingHours: parseSchedule(place.schedule),
        services: getDefaultServices(place.type || 'church'),
        source: '2gis'
    };

    appData.selectedChurch = church;
    displayChurchDetails(church);
    navigateTo('church');
}

function parseSchedule(schedule) {
    if (!schedule) return {
        monday: 'Уточняйте',
        tuesday: 'Уточняйте',
        wednesday: 'Уточняйте',
        thursday: 'Уточняйте',
        friday: 'Уточняйте',
        saturday: 'Уточняйте',
        sunday: 'Уточняйте'
    };

    // Parse 2GIS schedule format
    const days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
    const result = {};

    days.forEach((day, index) => {
        if (schedule[day]) {
            const s = schedule[day];
            if (s.working_hours && s.working_hours.length) {
                result[day] = s.working_hours.map(h => `${h.from}-${h.to}`).join(', ');
            } else {
                result[day] = 'Закрыто';
            }
        } else {
            result[day] = 'Уточняйте';
        }
    });

    return result;
}

function getDefaultServices(type) {
    const services = {
        church: [
            { id: 1, name: "Крещение", description: "Таинство крещения", category: "Таинства", price: 3000, duration: 60 },
            { id: 2, name: "Венчание", description: "Таинство венчания", category: "Таинства", price: 5000, duration: 90 },
            { id: 3, name: "Записка о здравии", description: "Поминовение о здравии", category: "Записки", price: 50, duration: 0 },
            { id: 4, name: "Молебен", description: "Молебен о здравии", category: "Молитвы", price: 500, duration: 30 }
        ],
        font: [
            { id: 1, name: "Крещение взрослых", description: "Таинство крещения с полным погружением", category: "Таинства", price: 3500, duration: 90 },
            { id: 2, name: "Крещение детей", description: "Таинство крещения для детей", category: "Таинства", price: 3000, duration: 60 }
        ]
    };
    return services[type] || services.church;
}

function calculateDistance(lat1, lng1, lat2, lng2) {
    const R = 6371; // km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLng/2) * Math.sin(dLng/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    const d = R * c;
    return d < 1 ? `${Math.round(d * 1000)} м` : `${d.toFixed(1)} км`;
}

function updatePlacesList(places) {
    const container = document.getElementById('churches-list');
    if (!container) return;

    // Combine default churches, fonts and 2GIS places
    const allPlaces = [...appData.churches, ...appData.fonts, ...places];

    container.innerHTML = allPlaces.map((place, index) => {
        const isChurch = index < appData.churches.length;
        const isFont = index >= appData.churches.length && index < appData.churches.length + appData.fonts.length;
        const is2gis = !isChurch && !isFont;
        const distance = place.distance || calculateDistance(
            currentPosition.lat, currentPosition.lng,
            place.lat, place.lng
        );
        const placeType = place.type || (isFont ? 'font' : 'church');
        const icon = placeType === 'font' ? 'tint' : 'church';

        return `
        <div class="church-card" data-place-id="${place.id}" data-type="${isChurch ? 'church' : isFont ? 'font' : '2gis'}" data-index="${index}">
            <div class="church-card-image">
                ${place.image ? `<img src="${place.image}" alt="${place.name}" onerror="this.style.display='none'">` : `
                <div class="church-card-placeholder ${placeType}">
                    <i class="fas fa-${icon}"></i>
                </div>
                `}
            </div>
            <div class="church-card-info">
                <h3>${place.name}</h3>
                <span class="distance">${distance}</span>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${place.rating || 4.5}</span>
                    ${place.source === '2gis' ? '<span class="source-badge">2GIS</span>' : ''}
                </div>
            </div>
        </div>
    `}).join('');

    // Add click handlers
    container.querySelectorAll('.church-card').forEach(card => {
        card.addEventListener('click', () => {
            const cardType = card.dataset.type;
            const index = parseInt(card.dataset.index);

            if (cardType === 'church') {
                openChurch(index);
            } else if (cardType === 'font') {
                openFont(index - appData.churches.length);
            } else {
                const place = appData.places2gis[index - appData.churches.length - appData.fonts.length];
                if (place) open2gisPlace(place);
            }
        });
    });
}

function updatePlacesCount(count) {
    const countEl = document.getElementById('places-count');
    if (countEl) {
        countEl.textContent = `Найдено: ${count + appData.churches.length + appData.fonts.length}`;
        countEl.style.display = 'block';
    }
}

// Get user location
function getUserLocation() {
    showToast('Определение местоположения...');

    if (!navigator.geolocation) {
        showToast('Геолокация не поддерживается браузером');
        console.error('Geolocation not supported');
        return;
    }

    // Check if we're on file:// protocol
    if (window.location.protocol === 'file:') {
        console.warn('Geolocation may not work on file:// protocol');
        showToast('Геолокация может не работать при открытии файла напрямую');
    }

    navigator.geolocation.getCurrentPosition(
        (position) => {
            console.log('Geolocation success:', position.coords);
            currentPosition = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };

            if (map) {
                if (useLeaflet) {
                    // Leaflet
                    map.setView([currentPosition.lat, currentPosition.lng], 14);

                    // Add user marker
                    if (userMarker) {
                        try { map.removeLayer(userMarker); } catch(e) {}
                    }
                    const userIcon = L.divIcon({
                        html: `<div style="
                            width: 20px;
                            height: 20px;
                            background: #4285F4;
                            border: 3px solid white;
                            border-radius: 50%;
                            box-shadow: 0 2px 6px rgba(0,0,0,0.3);
                        "></div>`,
                        className: 'user-marker',
                        iconSize: [20, 20],
                        iconAnchor: [10, 10]
                    });
                    userMarker = L.marker([currentPosition.lat, currentPosition.lng], { icon: userIcon })
                        .addTo(map)
                        .bindPopup('Вы здесь');
                } else {
                    // 2GIS
                    map.setCenter([currentPosition.lng, currentPosition.lat]);

                    // Add user marker
                    if (userMarker) {
                        try { userMarker.destroy(); } catch(e) {}
                    }
                    userMarker = new mapgl.Marker(map, {
                        coordinates: [currentPosition.lng, currentPosition.lat],
                        icon: createUserMarkerIcon(),
                        size: [24, 24],
                        anchor: [12, 12]
                    });

                    // Re-search places at new location
                    searchReligiousPlaces();
                }
            }

            showToast(`Местоположение: ${currentPosition.lat.toFixed(4)}, ${currentPosition.lng.toFixed(4)}`);
        },
        (error) => {
            console.error('Geolocation error:', error);
            let errorMsg = 'Не удалось определить местоположение';
            switch(error.code) {
                case error.PERMISSION_DENIED:
                    errorMsg = 'Доступ к геолокации запрещён';
                    break;
                case error.POSITION_UNAVAILABLE:
                    errorMsg = 'Местоположение недоступно';
                    break;
                case error.TIMEOUT:
                    errorMsg = 'Превышено время ожидания геолокации';
                    break;
            }
            showToast(errorMsg);
        },
        {
            enableHighAccuracy: true,
            timeout: 15000,
            maximumAge: 60000
        }
    );
}

function createUserMarkerIcon() {
    const svg = `
        <svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" fill="#4285F4" stroke="white" stroke-width="3"/>
            <circle cx="12" cy="12" r="4" fill="white"/>
        </svg>
    `;
    return 'data:image/svg+xml;base64,' + btoa(svg);
}

// ==========================================
// LEAFLET FALLBACK MAP
// ==========================================

function initLeafletMap() {
    const mapContainer = document.getElementById('map-2gis');
    const mapLoading = document.getElementById('map-loading');

    if (!mapContainer) return;

    if (typeof L === 'undefined') {
        console.error('Leaflet not available either!');
        if (mapLoading) {
            mapLoading.innerHTML = `
                <i class="fas fa-exclamation-triangle" style="font-size: 48px; color: #f0ad4e; margin-bottom: 10px;"></i>
                <p>Не удалось загрузить карту</p>
                <small>Проверьте интернет-соединение</small>
            `;
        }
        return;
    }

    console.log('Initializing Leaflet map (fallback)...');
    useLeaflet = true;

    // Clear container
    mapContainer.innerHTML = '';
    if (mapLoading) mapLoading.style.display = 'flex';

    try {
        // Create Leaflet map
        map = L.map('map-2gis').setView([currentPosition.lat, currentPosition.lng], 14);

        // Add OpenStreetMap tiles
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors',
            maxZoom: 19
        }).addTo(map);

        // Map is ready
        if (mapLoading) mapLoading.style.display = 'none';
        mapInitialized = true;
        mapIsLoading = false;

        // Add markers
        addLeafletMarkers();

        showToast('Карта загружена (OpenStreetMap)');
        console.log('Leaflet map initialized successfully');

    } catch (error) {
        console.error('Leaflet init error:', error);
        mapIsLoading = false;
        if (mapLoading) {
            mapLoading.innerHTML = `
                <i class="fas fa-map" style="font-size: 48px; color: #ccc; margin-bottom: 10px;"></i>
                <p>Ошибка загрузки карты</p>
                <small>${error.message}</small>
            `;
        }
    }
}

function addLeafletMarkers() {
    if (!map || !useLeaflet) return;

    // Clear existing markers
    markers.forEach(m => {
        try { map.removeLayer(m); } catch(e) {}
    });
    markers = [];

    // Type colors
    const colors = {
        church: '#C41E3A',
        font: '#1E90FF'
    };

    const icons = {
        church: '☦',
        font: '💧'
    };

    // Add markers for default churches
    appData.churches.forEach((church, index) => {
        const color = colors.church;
        const icon = icons.church;

        const customIcon = L.divIcon({
            html: `
                <div style="
                    background: ${color};
                    width: 40px;
                    height: 40px;
                    border-radius: 50% 50% 50% 0;
                    transform: rotate(-45deg);
                    border: 3px solid white;
                    box-shadow: 0 3px 10px rgba(0,0,0,0.3);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                ">
                    <span style="
                        transform: rotate(45deg);
                        font-size: 18px;
                        color: white;
                    ">${icon}</span>
                </div>
            `,
            className: 'custom-marker',
            iconSize: [40, 40],
            iconAnchor: [20, 40],
            popupAnchor: [0, -40]
        });

        const marker = L.marker([church.lat, church.lng], { icon: customIcon })
            .addTo(map)
            .bindPopup(`
                <div style="min-width: 200px;">
                    <h3 style="margin: 0 0 8px 0; font-size: 14px;">${church.name}</h3>
                    <p style="margin: 0 0 8px 0; color: #666; font-size: 12px;">${church.address}</p>
                    <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;">
                        <span style="color: #FFE473;">★</span>
                        <span style="font-size: 13px;">${church.rating}</span>
                        <span style="color: #999; font-size: 12px;">(${church.reviews} отзывов)</span>
                    </div>
                    <button onclick="openChurch(${index})" style="
                        width: 100%;
                        padding: 8px;
                        background: ${color};
                        color: white;
                        border: none;
                        border-radius: 6px;
                        cursor: pointer;
                        font-size: 13px;
                    ">Подробнее</button>
                </div>
            `);

        markers.push(marker);
    });

    // Add markers for fonts
    appData.fonts.forEach((font, index) => {
        const color = colors.font;
        const icon = icons.font;

        const customIcon = L.divIcon({
            html: `
                <div style="
                    background: ${color};
                    width: 40px;
                    height: 40px;
                    border-radius: 50% 50% 50% 0;
                    transform: rotate(-45deg);
                    border: 3px solid white;
                    box-shadow: 0 3px 10px rgba(0,0,0,0.3);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                ">
                    <span style="
                        transform: rotate(45deg);
                        font-size: 18px;
                        color: white;
                    ">${icon}</span>
                </div>
            `,
            className: 'custom-marker',
            iconSize: [40, 40],
            iconAnchor: [20, 40],
            popupAnchor: [0, -40]
        });

        const marker = L.marker([font.lat, font.lng], { icon: customIcon })
            .addTo(map)
            .bindPopup(`
                <div style="min-width: 200px;">
                    <h3 style="margin: 0 0 8px 0; font-size: 14px;">${font.name}</h3>
                    <p style="margin: 0 0 8px 0; color: #666; font-size: 12px;">${font.address}</p>
                    <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;">
                        <span style="color: #FFE473;">★</span>
                        <span style="font-size: 13px;">${font.rating}</span>
                        <span style="color: #999; font-size: 12px;">(${font.reviews} отзывов)</span>
                    </div>
                    <button onclick="openFont(${index})" style="
                        width: 100%;
                        padding: 8px;
                        background: ${color};
                        color: white;
                        border: none;
                        border-radius: 6px;
                        cursor: pointer;
                        font-size: 13px;
                    ">Подробнее</button>
                </div>
            `);

        markers.push(marker);
    });

    // Update places count
    updatePlacesCount(0);
}

// ==========================================
// Initialize App
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initQuickActions();
    initMapScreen();
    initChurchScreen();
    initBookingScreen();
    initPaymentScreen();
    initOrdersScreen();
    initPrayersScreen();
    initDonateScreen();
    initProfileScreen();
    initCalendar();

    renderNearbyChurches();
    renderNearbyFonts();
    renderChurchesList();

    // Update home screen with calendar data
    updateHomeScreen();

    // Show home screen on load
    navigateTo('home', false);

    // Try to get user location on load
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                currentPosition = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };
            },
            () => {}, // Ignore error, use default
            { timeout: 5000 }
        );
    }
});

// Navigation Functions
function initNavigation() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', () => {
            const screen = item.dataset.screen;
            navigateTo(screen);
        });
    });

    elements.backBtn.addEventListener('click', () => {
        goBack();
    });
}

function navigateTo(screen, addToHistory = true) {
    // Hide all screens
    document.querySelectorAll('.screen').forEach(s => {
        s.classList.remove('active');
        s.style.display = 'none';
    });

    // Show target screen
    const targetScreen = document.getElementById(`screen-${screen}`);
    if (targetScreen) {
        targetScreen.classList.add('active');
        targetScreen.style.display = 'block';
    }

    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.toggle('active', item.dataset.screen === screen);
    });

    elements.headerTitle.textContent = screenTitles[screen] || 'Приложение';

    const mainScreens = ['home', 'map', 'orders', 'prayers', 'profile'];
    elements.backBtn.classList.toggle('hidden', mainScreens.includes(screen));

    const hideNavScreens = ['payment', 'success'];
    const navWrapper = document.querySelector('.bottom-nav-wrapper');
    if (navWrapper) {
        navWrapper.style.display = hideNavScreens.includes(screen) ? 'none' : 'block';
    }
    elements.bottomNav.style.display = hideNavScreens.includes(screen) ? 'none' : 'flex';

    if (addToHistory && appData.currentScreen !== screen) {
        appData.screenHistory.push(appData.currentScreen);
    }

    appData.currentScreen = screen;
    elements.content.scrollTop = 0;

    // Initialize map when map screen is shown (only once)
    if (screen === 'map' && !mapInitialized && !mapIsLoading) {
        setTimeout(() => initMap(), 100);
    }

    // Initialize calendar when calendar screen is shown
    if (screen === 'calendar') {
        onCalendarScreenShow();
    }
}

function goBack() {
    if (appData.screenHistory.length > 0) {
        const prevScreen = appData.screenHistory.pop();
        navigateTo(prevScreen, false);
    } else {
        navigateTo('home', false);
    }
}

// Quick Actions
function initQuickActions() {
    document.querySelectorAll('.quick-action').forEach(action => {
        action.addEventListener('click', () => {
            const actionType = action.dataset.action;
            switch(actionType) {
                case 'map':
                case 'services':
                    navigateTo('map');
                    break;
                case 'fonts':
                    navigateTo('map');
                    // Set filter to fonts after navigating to map
                    setTimeout(() => {
                        const fontChip = document.querySelector('#screen-map .filter-chip[data-type="font"]');
                        if (fontChip) fontChip.click();
                    }, 200);
                    break;
                case 'donate':
                    navigateTo('donate');
                    break;
                case 'calendar':
                    navigateTo('calendar');
                    break;
            }
        });
    });

    document.querySelectorAll('.see-all').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const action = link.dataset.action;
            if (action === 'fonts') {
                navigateTo('map');
                setTimeout(() => {
                    const fontChip = document.querySelector('#screen-map .filter-chip[data-type="font"]');
                    if (fontChip) fontChip.click();
                }, 200);
            } else {
                navigateTo(action);
            }
        });
    });

    document.querySelectorAll('.service-card-mini').forEach(card => {
        card.addEventListener('click', () => {
            navigateTo('map');
        });
    });
}

// Render Churches
function renderNearbyChurches() {
    const container = document.getElementById('nearby-churches');
    if (!container) return; // Element may not exist in new design

    container.innerHTML = appData.churches.map((church, index) => `
        <div class="church-card" data-church-id="${index}">
            <div class="church-card-image">
                ${church.image ? `<img src="${church.image}" alt="${church.name}" onerror="this.style.display='none'">` : `
                <div class="church-card-placeholder church">
                    <i class="fas fa-church"></i>
                </div>
                `}
            </div>
            <div class="church-card-info">
                <h3>${church.name}</h3>
                <span class="distance">${church.distance}</span>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${church.rating}</span>
                </div>
            </div>
        </div>
    `).join('');

    container.querySelectorAll('.church-card').forEach(card => {
        card.addEventListener('click', () => {
            openChurch(parseInt(card.dataset.churchId));
        });
    });
}

function renderNearbyFonts() {
    const container = document.getElementById('nearby-fonts');
    if (!container) return;

    container.innerHTML = appData.fonts.map((font, index) => `
        <div class="church-card" data-font-id="${index}">
            <div class="church-card-image">
                ${font.image ? `<img src="${font.image}" alt="${font.name}" onerror="this.style.display='none'">` : `
                <div class="church-card-placeholder font">
                    <i class="fas fa-tint"></i>
                </div>
                `}
            </div>
            <div class="church-card-info">
                <h3>${font.name}</h3>
                <span class="distance">${font.distance}</span>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${font.rating}</span>
                </div>
            </div>
        </div>
    `).join('');

    container.querySelectorAll('.church-card').forEach(card => {
        card.addEventListener('click', () => {
            openFont(parseInt(card.dataset.fontId));
        });
    });
}

function renderChurchesList() {
    const container = document.getElementById('churches-list');
    if (!container) return;

    container.innerHTML = appData.churches.map((church, index) => `
        <div class="church-card" data-church-id="${index}">
            <div class="church-card-image">
                <img src="${church.image}" alt="${church.name}" onerror="this.style.display='none'">
            </div>
            <div class="church-card-info">
                <h3>${church.name}</h3>
                <span class="distance">${church.distance}</span>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${church.rating}</span>
                </div>
            </div>
        </div>
    `).join('');

    container.querySelectorAll('.church-card').forEach(card => {
        card.addEventListener('click', () => {
            openChurch(parseInt(card.dataset.churchId));
        });
    });
}

// Map Screen
function initMapScreen() {
    // Filter chips (by type: all, church, monastery, font, spring, shop)
    document.querySelectorAll('#screen-map .filter-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            document.querySelectorAll('#screen-map .filter-chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');

            const type = chip.dataset.type;

            // Clear markers
            if (useLeaflet) {
                markers.forEach(m => {
                    try { map.removeLayer(m); } catch(e) {}
                });
            } else {
                markers.forEach(m => {
                    try { m.destroy(); } catch(e) {}
                });
            }
            markers = [];

            // Filter and add markers based on type (2 groups)
            if (type === 'all') {
                addDefaultMarkers();
                if (osmPlacesData) {
                    addOSMMarkersToMap();
                }
                searchReligiousPlaces('all');
            } else if (type === 'church') {
                // Группа "Храмы": church, cathedral, chapel, monastery
                appData.churches.forEach((church, index) => {
                    addSingleMarker(church, index, 'church');
                });
                if (osmPlacesData) {
                    addOSMMarkersToMap(['church', 'cathedral', 'chapel', 'monastery']);
                }
                searchReligiousPlaces('church');
            } else if (type === 'spring') {
                // Группа "Св. источники": spring, font
                appData.fonts.forEach((font, index) => {
                    addSingleMarker(font, index, 'font');
                });
                if (osmPlacesData) {
                    addOSMMarkersToMap(['spring', 'font']);
                }
                searchReligiousPlaces('font');
            }
        });
    });

    // Location button
    document.getElementById('btn-location').addEventListener('click', () => {
        getUserLocation();
    });

    // Search input
    const searchInput = document.querySelector('#screen-map .map-search input');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                const query = e.target.value.trim();
                if (query.length > 2) {
                    searchPlacesByQuery(query);
                }
            }, 500);
        });
    }
}

async function searchPlacesByQuery(query) {
    try {
        const url = `https://catalog.api.2gis.com/3.0/items?q=${encodeURIComponent(query)}&point=${currentPosition.lng},${currentPosition.lat}&radius=10000&key=${DGIS_API_KEY}&page_size=20`;

        const response = await fetch(url);
        const data = await response.json();

        if (data.result && data.result.items) {
            const places = data.result.items.map(item => ({
                id: item.id,
                name: item.name,
                type: detectType(item.name),
                typeName: detectTypeName(item.name),
                address: item.address_name || '',
                lat: item.point?.lat,
                lng: item.point?.lon,
                phone: extractPhone(item.contact_groups),
                reviews: item.reviews?.general_review_count || 0,
                rating: item.reviews?.general_rating || 0,
                source: '2gis'
            })).filter(p => p.lat && p.lng);

            if (places.length > 0) {
                markers.forEach(m => m.destroy());
                markers = [];
                addPlaceMarkers(places);
                updatePlacesList(places);

                // Center on first result
                if (map && places[0]) {
                    map.setCenter([places[0].lng, places[0].lat]);
                }
            }
        }
    } catch (error) {
        console.error('Search error:', error);
    }
}

function detectType(name) {
    const lower = name.toLowerCase();
    if (lower.includes('купель') || lower.includes('крестил') || lower.includes('крещен')) return 'font';
    return 'church';
}

function detectTypeName(name) {
    const type = detectType(name);
    return type === 'font' ? 'Купель' : 'Храм';
}

// Church Screen
function initChurchScreen() {
    document.querySelectorAll('#screen-church .tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('#screen-church .tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('#screen-church .tab-content').forEach(c => c.classList.remove('active'));

            btn.classList.add('active');
            document.getElementById(`tab-${btn.dataset.tab}`).classList.add('active');
        });
    });

    document.getElementById('btn-favorite').addEventListener('click', function() {
        this.classList.toggle('active');
        const icon = this.querySelector('i');
        icon.classList.toggle('far');
        icon.classList.toggle('fas');
        showToast(this.classList.contains('active') ? 'Добавлено в избранное' : 'Удалено из избранного');
    });

    document.querySelectorAll('#screen-church .category-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            document.querySelectorAll('#screen-church .category-chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            filterServices(chip.dataset.category);
        });
    });

    document.getElementById('btn-church-donate').addEventListener('click', () => {
        navigateTo('donate');
    });
}

function openChurch(churchId) {
    const church = appData.churches[churchId];
    if (!church) return;

    appData.selectedChurch = church;
    displayChurchDetails(church);
    navigateTo('church');
}

function openFont(fontId) {
    const font = appData.fonts[fontId];
    if (!font) return;

    appData.selectedChurch = font;
    displayChurchDetails(font);
    navigateTo('church');
}

function displayChurchDetails(church) {
    document.getElementById('church-image').src = church.image || '';
    document.getElementById('church-name').textContent = church.name;
    // Hide religion badge since we only have Orthodox
    const religionEl = document.getElementById('church-religion');
    if (religionEl) religionEl.style.display = 'none';
    document.getElementById('church-rating').textContent = church.rating;
    document.getElementById('church-reviews').textContent = church.reviews;
    document.getElementById('church-address').textContent = church.address;
    document.getElementById('church-description').textContent = church.description;
    document.getElementById('church-phone').textContent = church.phone || 'Не указан';
    document.getElementById('church-email').textContent = church.email || 'Не указан';
    document.getElementById('church-website').textContent = church.website || 'Не указан';

    const hoursContainer = document.getElementById('church-hours');
    const dayNames = {
        monday: 'Понедельник',
        tuesday: 'Вторник',
        wednesday: 'Среда',
        thursday: 'Четверг',
        friday: 'Пятница',
        saturday: 'Суббота',
        sunday: 'Воскресенье'
    };

    if (church.workingHours) {
        hoursContainer.innerHTML = Object.entries(church.workingHours).map(([day, hours]) => `
            <div class="hours-row">
                <span class="day-name">${dayNames[day] || day}</span>
                <span class="hours">${hours}</span>
            </div>
        `).join('');
    }

    renderChurchServices(church.services || []);
    elements.headerTitle.textContent = church.name;
}

function renderChurchServices(services) {
    const container = document.getElementById('church-services');
    container.innerHTML = services.map(service => `
        <div class="service-card" data-service-id="${service.id}">
            <div class="service-card-info">
                <h3>${service.name}</h3>
                <p>${service.description}</p>
            </div>
            <div class="service-card-price">
                <span class="price">${service.price > 0 ? service.price + ' ₽' : 'Бесплатно'}</span>
                ${service.duration > 0 ? `<span class="duration">${service.duration} мин</span>` : ''}
            </div>
        </div>
    `).join('');

    container.querySelectorAll('.service-card').forEach(card => {
        card.addEventListener('click', () => {
            const service = appData.selectedChurch.services.find(s => s.id === parseInt(card.dataset.serviceId));
            openBooking(service);
        });
    });
}

function filterServices(category) {
    if (!appData.selectedChurch) return;

    const services = category === 'all'
        ? appData.selectedChurch.services
        : appData.selectedChurch.services.filter(s => s.category === category);

    renderChurchServices(services);
}

// Booking Screen
function initBookingScreen() {
    document.querySelectorAll('.date-option').forEach(option => {
        option.addEventListener('click', () => {
            document.querySelectorAll('.date-option').forEach(o => o.classList.remove('selected'));
            option.classList.add('selected');
            updateBookingSummary();
        });
    });

    document.querySelectorAll('.time-slot:not(.disabled)').forEach(slot => {
        slot.addEventListener('click', () => {
            document.querySelectorAll('.time-slot').forEach(s => s.classList.remove('selected'));
            slot.classList.add('selected');
            updateBookingSummary();
        });
    });

    document.getElementById('btn-to-payment').addEventListener('click', () => {
        navigateTo('payment');
    });
}

function openBooking(service) {
    appData.selectedService = service;

    document.getElementById('booking-service-name').textContent = service.name;
    document.getElementById('booking-church-name').textContent = appData.selectedChurch.name;
    document.getElementById('booking-price').textContent = service.price > 0 ? service.price + ' ₽' : 'Бесплатно';
    document.getElementById('booking-duration').innerHTML = service.duration > 0
        ? `<i class="far fa-clock"></i> ${service.duration} мин`
        : '';

    document.getElementById('summary-service').textContent = service.name;
    document.getElementById('summary-total').textContent = service.price > 0 ? service.price + ' ₽' : 'Бесплатно';

    updateBookingSummary();
    navigateTo('booking');
}

function updateBookingSummary() {
    const selectedDate = document.querySelector('.date-option.selected');
    const selectedTime = document.querySelector('.time-slot.selected');

    if (selectedDate && selectedTime) {
        const dateText = selectedDate.querySelector('.date').textContent;
        const timeText = selectedTime.textContent;
        document.getElementById('summary-datetime').textContent = `${dateText} янв, ${timeText}`;
    }
}

// Payment Screen
function initPaymentScreen() {
    document.querySelectorAll('.payment-method').forEach(method => {
        method.addEventListener('click', () => {
            document.querySelectorAll('.payment-method').forEach(m => m.classList.remove('selected'));
            method.classList.add('selected');

            const cardForm = document.getElementById('card-form');
            cardForm.style.display = method.dataset.method === 'card' ? 'block' : 'none';
        });
    });

    document.getElementById('btn-pay').addEventListener('click', () => {
        processPayment();
    });
}

function processPayment() {
    showLoading();

    setTimeout(() => {
        hideLoading();
        navigateTo('success');
    }, 2000);
}

// Success Screen
document.getElementById('btn-to-orders')?.addEventListener('click', () => {
    navigateTo('orders');
});

document.getElementById('btn-to-home')?.addEventListener('click', () => {
    appData.screenHistory = [];
    navigateTo('home');
});

// Orders Screen
function initOrdersScreen() {
    document.querySelectorAll('.orders-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.orders-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            const status = tab.dataset.status;
            document.getElementById('orders-active').classList.toggle('hidden', status !== 'active');
            document.getElementById('orders-completed').classList.toggle('hidden', status !== 'completed');
        });
    });
}

// Prayers Screen
function initPrayersScreen() {
    document.querySelectorAll('.prayer-card').forEach(card => {
        card.addEventListener('click', () => {
            const prayerId = card.dataset.prayer;
            openPrayer(prayerId);
        });
    });

    document.querySelectorAll('#screen-prayers .filter-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            document.querySelectorAll('#screen-prayers .filter-chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
        });
    });

    let fontSize = 18;

    document.getElementById('btn-decrease-font')?.addEventListener('click', () => {
        fontSize = Math.max(14, fontSize - 2);
        document.getElementById('prayer-text').style.fontSize = fontSize + 'px';
    });

    document.getElementById('btn-increase-font')?.addEventListener('click', () => {
        fontSize = Math.min(28, fontSize + 2);
        document.getElementById('prayer-text').style.fontSize = fontSize + 'px';
    });

    document.getElementById('btn-play-prayer')?.addEventListener('click', () => {
        const player = document.getElementById('audio-player');
        player.classList.toggle('hidden');
        showToast('Аудио будет доступно в приложении');
    });

    document.getElementById('btn-favorite-prayer')?.addEventListener('click', function() {
        const icon = this.querySelector('i');
        icon.classList.toggle('far');
        icon.classList.toggle('fas');
        showToast(icon.classList.contains('fas') ? 'Добавлено в избранное' : 'Удалено из избранного');
    });
}

function openPrayer(prayerId) {
    const prayer = appData.prayers[prayerId];
    if (!prayer) return;

    document.getElementById('prayer-title').textContent = prayer.title;
    // Hide religion badge since we only have Orthodox
    const religionEl = document.getElementById('prayer-religion');
    if (religionEl) religionEl.style.display = 'none';
    document.getElementById('prayer-text').innerHTML = prayer.text.split('\n').map(line => `<p>${line}</p>`).join('');

    navigateTo('prayer-detail');
}

// Donate Screen
function initDonateScreen() {
    document.querySelectorAll('.amount-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.amount-btn').forEach(b => b.classList.remove('selected'));
            btn.classList.add('selected');
            document.getElementById('custom-amount').value = '';
            document.getElementById('donate-amount').textContent = btn.dataset.amount + ' ₽';
        });
    });

    document.getElementById('custom-amount')?.addEventListener('input', function() {
        if (this.value) {
            document.querySelectorAll('.amount-btn').forEach(b => b.classList.remove('selected'));
            document.getElementById('donate-amount').textContent = this.value + ' ₽';
        }
    });

    document.querySelectorAll('.radio-option').forEach(option => {
        option.addEventListener('click', () => {
            document.querySelectorAll('.radio-option').forEach(o => o.classList.remove('selected'));
            option.classList.add('selected');
            option.querySelector('input').checked = true;
        });
    });

    document.getElementById('btn-donate')?.addEventListener('click', () => {
        showLoading();
        setTimeout(() => {
            hideLoading();
            showToast('Спасибо за пожертвование!');
            navigateTo('home');
        }, 1500);
    });
}

// Profile Screen
function initProfileScreen() {
    document.querySelectorAll('.toggle-switch').forEach(toggle => {
        toggle.addEventListener('click', () => {
            toggle.classList.toggle('active');
        });
    });

    document.querySelectorAll('.menu-item').forEach(item => {
        item.addEventListener('click', () => {
            const action = item.dataset.action;
            switch(action) {
                case 'favorites':
                    showToast('Раздел в разработке');
                    break;
                case 'payments':
                    showToast('История платежей');
                    break;
                case 'help':
                    showToast('Справка и поддержка');
                    break;
                case 'about':
                    showToast('Версия 1.0.0');
                    break;
            }
        });
    });

    document.querySelector('.btn-logout')?.addEventListener('click', () => {
        showToast('Выход из аккаунта');
    });
}

// Calendar filters
document.querySelectorAll('#screen-calendar .filter-chip').forEach(chip => {
    chip.addEventListener('click', () => {
        document.querySelectorAll('#screen-calendar .filter-chip').forEach(c => c.classList.remove('active'));
        chip.classList.add('active');
    });
});

// Utility Functions
function showToast(message) {
    elements.toastMessage.textContent = message;
    elements.toast.classList.remove('hidden');

    setTimeout(() => {
        elements.toast.classList.add('hidden');
    }, 3000);
}

function showLoading() {
    elements.loading.classList.remove('hidden');
}

function hideLoading() {
    elements.loading.classList.add('hidden');
}

// Console welcome
console.log('%c Православный Маркетплейс - Прототип с 2GIS ', 'background: #C41E3A; color: #D4AF37; font-size: 16px; padding: 10px;');
console.log('2GIS API Key:', DGIS_API_KEY);
console.log('Православный маркетплейс с картой храмов и купелей');

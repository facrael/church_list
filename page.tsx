"use client"

import React from "react"

import { useState } from "react"
import {
  Church,
  Droplets,
  Heart,
  Calendar,
  MapPin,
  Star,
  Clock,
  Home,
  Map,
  ShoppingBag,
  BookOpen,
  User,
  Bell,
  ChevronRight,
  Search,
  Settings,
  LogOut,
  CreditCard,
  Gift,
  Flame,
  Sun,
  Moon,
} from "lucide-react"

type TabType = "home" | "map" | "orders" | "prayers" | "profile"

export default function ChurchApp() {
  const [activeTab, setActiveTab] = useState<TabType>("home")

  return (
    <div className="min-h-screen bg-[#080c14] flex items-center justify-center p-4">
      <div className="w-full max-w-[390px] h-[844px] bg-[#0d1321] rounded-[44px] overflow-hidden shadow-2xl flex flex-col border border-[#1a2540]/50">
        {/* Status Bar Simulation */}
        <div className="h-12 px-8 flex items-center justify-between text-white text-xs">
          <span>9:41</span>
          <div className="flex items-center gap-1">
            <div className="w-4 h-2 border border-white rounded-sm">
              <div className="w-3 h-1 bg-white rounded-sm m-[1px]" />
            </div>
          </div>
        </div>

        {/* Content Area */}
        <div className="flex-1 overflow-hidden flex flex-col">
          {activeTab === "home" && <HomeTab />}
          {activeTab === "map" && <MapTab />}
          {activeTab === "orders" && <OrdersTab />}
          {activeTab === "prayers" && <PrayersTab />}
          {activeTab === "profile" && <ProfileTab />}
        </div>

        {/* Bottom Navigation */}
        <div className="px-6 pb-8 pt-2">
          <nav className="bg-[#151d2e] rounded-2xl px-4 py-3 flex items-center justify-between">
            <NavItem
              icon={<Home className="w-5 h-5" />}
              active={activeTab === "home"}
              onClick={() => setActiveTab("home")}
            />
            <NavItem
              icon={<Map className="w-5 h-5" />}
              active={activeTab === "map"}
              onClick={() => setActiveTab("map")}
            />
            <NavItem
              icon={<ShoppingBag className="w-5 h-5" />}
              active={activeTab === "orders"}
              onClick={() => setActiveTab("orders")}
            />
            <NavItem
              icon={<BookOpen className="w-5 h-5" />}
              active={activeTab === "prayers"}
              onClick={() => setActiveTab("prayers")}
            />
            <NavItem
              icon={<User className="w-5 h-5" />}
              active={activeTab === "profile"}
              onClick={() => setActiveTab("profile")}
            />
          </nav>
        </div>
      </div>
    </div>
  )
}

function NavItem({
  icon,
  active,
  onClick,
}: {
  icon: React.ReactNode
  active: boolean
  onClick: () => void
}) {
  return (
    <button
      onClick={onClick}
      className={`p-3 rounded-xl transition-all ${
        active ? "bg-amber-500 text-black" : "text-gray-500 hover:text-gray-300"
      }`}
    >
      {icon}
    </button>
  )
}

function HomeTab() {
  const today = new Date()
  const days = ["воскресенье", "понедельник", "вторник", "среда", "четверг", "пятница", "суббота"]
  const months = [
    "января",
    "февраля",
    "марта",
    "апреля",
    "мая",
    "июня",
    "июля",
    "августа",
    "сентября",
    "октября",
    "ноября",
    "декабря",
  ]
  const dayOfWeek = days[today.getDay()]
  const day = today.getDate()
  const month = months[today.getMonth()]

  // Church holidays by date (day-month format)
  const churchHolidays: Record<string, string> = {
    "7-1": "Рождество Христово",
    "19-1": "Крещение Господне",
    "15-2": "Сретение Господне",
    "7-4": "Благовещение Пресвятой Богородицы",
    "28-1": "Память прп. Павла Фивейского",
    "29-1": "Поклонение честным веригам ап. Петра",
    "25-7": "День Крещения Руси",
    "19-8": "Преображение Господне",
    "28-8": "Успение Пресвятой Богородицы",
    "21-9": "Рождество Пресвятой Богородицы",
    "27-9": "Воздвижение Креста Господня",
    "14-10": "Покров Пресвятой Богородицы",
    "4-12": "Введение во храм Пресвятой Богородицы",
  }

  const todayKey = `${day}-${today.getMonth() + 1}`
  const todayHoliday = churchHolidays[todayKey]

  return (
    <div className="flex-1 overflow-y-auto">
      {/* Header */}
      <div className="px-6 pb-4">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-white text-xl font-bold">
              {day} {month}, {dayOfWeek}
            </h1>
            {todayHoliday && (
              <p className="text-gray-500 text-sm mt-1">
                Сегодняшний праздник: <span className="text-amber-500">{todayHoliday}</span>
              </p>
            )}
          </div>
          <div className="flex items-center gap-3">
            <button className="w-10 h-10 bg-[#151d2e] rounded-xl flex items-center justify-center">
              <Search className="w-5 h-5 text-gray-400" />
            </button>
            <button className="w-10 h-10 bg-[#151d2e] rounded-xl flex items-center justify-center relative">
              <Bell className="w-5 h-5 text-gray-400" />
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-amber-500 rounded-full text-[10px] text-black font-bold flex items-center justify-center">
                3
              </span>
            </button>
          </div>
        </div>

        {/* Featured Card */}
        <div className="bg-gradient-to-br from-amber-500 to-amber-600 rounded-3xl p-5 mb-6">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <p className="text-black/60 text-sm mb-1">Ближайшее событие</p>
              <h2 className="text-black text-lg font-bold mb-2">Воскресная литургия</h2>
              <div className="flex items-center gap-4 text-black/70 text-sm">
                <span className="flex items-center gap-1">
                  <Clock className="w-4 h-4" />
                  09:00
                </span>
                <span className="flex items-center gap-1">
                  <Calendar className="w-4 h-4" />
                  28 янв
                </span>
              </div>
            </div>
            <div className="w-16 h-16 bg-black/10 rounded-2xl flex items-center justify-center">
              <Church className="w-8 h-8 text-black/70" />
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="px-6 mb-6">
        <div className="flex gap-3 overflow-x-auto pb-2 scrollbar-hide">
          <QuickAction icon={<Church className="w-5 h-5" />} label="Храмы" />
          <QuickAction icon={<Droplets className="w-5 h-5" />} label="Купели" />
          <QuickAction icon={<Heart className="w-5 h-5" />} label="Пожертвования" />
          <QuickAction icon={<Calendar className="w-5 h-5" />} label="Календарь" />
          <QuickAction icon={<Flame className="w-5 h-5" />} label="Свечи" />
        </div>
      </div>

      {/* Nearby Section */}
      <div className="px-6 mb-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-white text-lg font-semibold">Рядом с вами</h2>
          <button className="text-amber-500 text-sm font-medium">Все</button>
        </div>
        <div className="space-y-3">
          <LocationCard
            type="temple"
            name="Храм Христа Спасителя"
            address="ул. Волхонка, 15"
            distance="1.2 км"
            rating={4.8}
          />
          <LocationCard
            type="font"
            name="Купель Сергия Радонежского"
            address="ул. Ильинка, 8"
            distance="2.4 км"
            rating={4.9}
          />
        </div>
      </div>

      {/* Services Grid */}
      <div className="px-6 mb-6">
        <h2 className="text-white text-lg font-semibold mb-4">Услуги</h2>
        <div className="grid grid-cols-3 gap-3">
          <ServiceTile icon={<Gift className="w-6 h-6" />} label="Крещение" price="3000 ₽" />
          <ServiceTile icon={<Flame className="w-6 h-6" />} label="Молебен" price="500 ₽" />
          <ServiceTile icon={<BookOpen className="w-6 h-6" />} label="Записки" price="50 ₽" />
        </div>
      </div>

      {/* Daily Quote */}
      <div className="px-6 pb-4">
        <div className="bg-[#151d2e] rounded-2xl p-5 border-l-4 border-amber-500">
          <p className="text-white text-base italic leading-relaxed mb-2">
            {"«Любите друг друга, как Я возлюбил вас»"}
          </p>
          <p className="text-amber-500 text-sm font-medium">Ин. 15:12</p>
        </div>
      </div>
    </div>
  )
}

function MapTab() {
  return (
    <div className="flex-1 flex flex-col items-center justify-center px-6 text-center">
      <div className="w-24 h-24 bg-[#151d2e] rounded-3xl flex items-center justify-center mb-6">
        <Map className="w-12 h-12 text-amber-500" />
      </div>
      <h2 className="text-white text-xl font-bold mb-2">Карта храмов</h2>
      <p className="text-gray-500 text-sm leading-relaxed">
        Найдите ближайшие храмы и купели на интерактивной карте
      </p>
    </div>
  )
}

function OrdersTab() {
  return (
    <div className="flex-1 overflow-y-auto px-6">
      <h1 className="text-white text-2xl font-bold mb-6 pt-2">Мои заказы</h1>
      <div className="space-y-4">
        <OrderCard
          title="Записка о здравии"
          temple="Храм Христа Спасителя"
          status="completed"
          date="25 янв"
          price="150 ₽"
        />
        <OrderCard
          title="Молебен"
          temple="Храм Покрова"
          status="processing"
          date="27 янв"
          price="500 ₽"
        />
        <OrderCard
          title="Крещение"
          temple="Храм Николая Чудотворца"
          status="pending"
          date="15 фев"
          price="3000 ₽"
        />
      </div>
    </div>
  )
}

function PrayersTab() {
  return (
    <div className="flex-1 overflow-y-auto px-6">
      <h1 className="text-white text-2xl font-bold mb-2 pt-2">Молитвослов</h1>
      <p className="text-gray-500 text-sm mb-6">Собрание молитв на каждый день</p>

      {/* Time-based Prayers */}
      <div className="grid grid-cols-2 gap-3 mb-4">
        <button className="bg-amber-500 rounded-2xl p-4 flex items-center gap-3">
          <Sun className="w-6 h-6 text-black" />
          <div className="text-left">
            <p className="text-black font-semibold">Утренние</p>
            <p className="text-black/60 text-xs">12 молитв</p>
          </div>
        </button>
        <button className="bg-[#151d2e] rounded-2xl p-4 flex items-center gap-3">
          <Moon className="w-6 h-6 text-amber-500" />
          <div className="text-left">
            <p className="text-white font-semibold">Вечерние</p>
            <p className="text-gray-500 text-xs">10 молитв</p>
          </div>
        </button>
      </div>

      {/* Health and Memorial Prayers */}
      <div className="grid grid-cols-2 gap-3 mb-6">
        <button className="bg-[#151d2e] rounded-2xl p-4 flex items-center gap-3">
          <Heart className="w-6 h-6 text-green-500" />
          <div className="text-left">
            <p className="text-white font-semibold">За здравие</p>
            <p className="text-gray-500 text-xs">8 молитв</p>
          </div>
        </button>
        <button className="bg-[#151d2e] rounded-2xl p-4 flex items-center gap-3">
          <Flame className="w-6 h-6 text-purple-400" />
          <div className="text-left">
            <p className="text-white font-semibold">За упокой</p>
            <p className="text-gray-500 text-xs">6 молитв</p>
          </div>
        </button>
      </div>

      {/* Prayer List */}
      <h2 className="text-white font-semibold mb-4">Основные молитвы</h2>
      <div className="space-y-3">
        <PrayerItem title="Отче наш" duration="2 мин" />
        <PrayerItem title="Символ веры" duration="3 мин" />
        <PrayerItem title="Богородице Дево" duration="1 мин" />
        <PrayerItem title="Молитва Ангелу Хранителю" duration="2 мин" />
      </div>
    </div>
  )
}

function ProfileTab() {
  return (
    <div className="flex-1 overflow-y-auto px-6">
      {/* Profile Header */}
      <div className="flex items-center gap-4 mb-8 pt-2">
        <div className="w-16 h-16 bg-gradient-to-br from-amber-500 to-amber-600 rounded-2xl flex items-center justify-center">
          <span className="text-black text-2xl font-bold">А</span>
        </div>
        <div>
          <h1 className="text-white text-xl font-bold">Алексей Иванов</h1>
          <p className="text-gray-500 text-sm">alexey@email.com</p>
        </div>
      </div>

      {/* Profile Menu */}
      <div className="space-y-2">
        <ProfileItem icon={<User className="w-5 h-5" />} label="Личные данные" />
        <ProfileItem icon={<CreditCard className="w-5 h-5" />} label="Способы оплаты" />
        <ProfileItem icon={<Heart className="w-5 h-5" />} label="История пожертвований" />
        <ProfileItem icon={<Bell className="w-5 h-5" />} label="Уведомления" />
        <ProfileItem icon={<Settings className="w-5 h-5" />} label="Настройки" />
      </div>

      {/* Logout */}
      <button className="w-full mt-8 py-4 flex items-center justify-center gap-2 text-red-400 rounded-2xl bg-red-400/10">
        <LogOut className="w-5 h-5" />
        <span className="font-medium">Выйти</span>
      </button>
    </div>
  )
}

function QuickAction({ icon, label }: { icon: React.ReactNode; label: string }) {
  return (
    <button className="flex flex-col items-center gap-2 min-w-[72px]">
      <div className="w-14 h-14 bg-[#151d2e] rounded-2xl flex items-center justify-center text-amber-500">
        {icon}
      </div>
      <span className="text-gray-400 text-xs">{label}</span>
    </button>
  )
}

function LocationCard({
  type,
  name,
  address,
  distance,
  rating,
}: {
  type: "temple" | "font"
  name: string
  address: string
  distance: string
  rating: number
}) {
  return (
    <div className="bg-[#151d2e] rounded-2xl p-4 flex items-center gap-4">
      <div
        className={`w-14 h-14 rounded-xl flex items-center justify-center ${
          type === "temple" ? "bg-amber-500/20" : "bg-blue-500/20"
        }`}
      >
        {type === "temple" ? (
          <Church className="w-7 h-7 text-amber-500" />
        ) : (
          <Droplets className="w-7 h-7 text-blue-500" />
        )}
      </div>
      <div className="flex-1 min-w-0">
        <h3 className="text-white font-medium truncate">{name}</h3>
        <p className="text-gray-500 text-sm truncate">{address}</p>
        <div className="flex items-center gap-3 mt-1">
          <span className="text-gray-400 text-xs flex items-center gap-1">
            <MapPin className="w-3 h-3" />
            {distance}
          </span>
          <span className="text-amber-500 text-xs flex items-center gap-1">
            <Star className="w-3 h-3 fill-amber-500" />
            {rating}
          </span>
        </div>
      </div>
      <ChevronRight className="w-5 h-5 text-gray-600" />
    </div>
  )
}

function ServiceTile({ icon, label, price }: { icon: React.ReactNode; label: string; price: string }) {
  return (
    <button className="bg-[#151d2e] rounded-2xl p-4 flex flex-col items-center gap-2">
      <div className="text-amber-500">{icon}</div>
      <span className="text-white text-sm font-medium">{label}</span>
      <span className="text-gray-500 text-xs">от {price}</span>
    </button>
  )
}

function OrderCard({
  title,
  temple,
  status,
  date,
  price,
}: {
  title: string
  temple: string
  status: "completed" | "processing" | "pending"
  date: string
  price: string
}) {
  const statusConfig = {
    completed: { label: "Выполнено", color: "bg-green-500/20 text-green-400" },
    processing: { label: "В работе", color: "bg-amber-500/20 text-amber-400" },
    pending: { label: "Ожидает", color: "bg-blue-500/20 text-blue-400" },
  }

  return (
    <div className="bg-[#151d2e] rounded-2xl p-4">
      <div className="flex items-start justify-between mb-3">
        <div>
          <h3 className="text-white font-medium">{title}</h3>
          <p className="text-gray-500 text-sm">{temple}</p>
        </div>
        <span className={`px-3 py-1 rounded-full text-xs font-medium ${statusConfig[status].color}`}>
          {statusConfig[status].label}
        </span>
      </div>
      <div className="flex items-center justify-between">
        <span className="text-gray-500 text-sm">{date}</span>
        <span className="text-white font-semibold">{price}</span>
      </div>
    </div>
  )
}

function PrayerItem({ title, duration }: { title: string; duration: string }) {
  return (
    <button className="w-full bg-[#151d2e] rounded-2xl p-4 flex items-center gap-4">
      <div className="w-10 h-10 bg-amber-500/20 rounded-xl flex items-center justify-center">
        <BookOpen className="w-5 h-5 text-amber-500" />
      </div>
      <div className="flex-1 text-left">
        <h3 className="text-white font-medium">{title}</h3>
        <p className="text-gray-500 text-xs">{duration}</p>
      </div>
      <ChevronRight className="w-5 h-5 text-gray-600" />
    </button>
  )
}

function ProfileItem({ icon, label }: { icon: React.ReactNode; label: string }) {
  return (
    <button className="w-full bg-[#151d2e] rounded-2xl p-4 flex items-center gap-4">
      <div className="w-10 h-10 bg-amber-500/20 rounded-xl flex items-center justify-center text-amber-500">
        {icon}
      </div>
      <span className="text-white flex-1 text-left">{label}</span>
      <ChevronRight className="w-5 h-5 text-gray-600" />
    </button>
  )
}

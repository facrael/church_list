package com.religiousmarket.app.data.model

import kotlinx.serialization.Serializable

/**
 * Religion enum representing supported religions
 */
enum class Religion(val displayName: String, val icon: String) {
    ORTHODOX("Православие", "☦"),
    ISLAM("Ислам", "☪"),
    JUDAISM("Иудаизм", "✡");

    companion object {
        fun fromString(value: String): Religion {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: ORTHODOX
        }
    }
}

/**
 * Church/Temple model
 */
@Serializable
data class Church(
    val id: String,
    val name: String,
    val religion: Religion,
    val denomination: String = "",
    val description: String,
    val address: String,
    val city: String = "Москва",
    val latitude: Double,
    val longitude: Double,
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val imageUrl: String = "",
    val photos: List<String> = emptyList(),
    val rating: Float = 0f,
    val reviewsCount: Int = 0,
    val workingHours: Map<String, String> = emptyMap(),
    val isVerified: Boolean = false,
    val isFavorite: Boolean = false,
    val distance: String = ""
)

/**
 * Service offered by a church
 */
@Serializable
data class Service(
    val id: String,
    val churchId: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Int,
    val duration: Int = 0, // minutes
    val requiresBooking: Boolean = true,
    val maxAdvanceDays: Int = 30,
    val isAvailable: Boolean = true
)

/**
 * Booking model
 */
@Serializable
data class Booking(
    val id: String,
    val userId: String,
    val churchId: String,
    val serviceId: String,
    val churchName: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val status: BookingStatus,
    val totalPrice: Int,
    val createdAt: String
)

enum class BookingStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED
}

/**
 * Prayer/Religious content
 */
@Serializable
data class Prayer(
    val id: String,
    val title: String,
    val text: String,
    val religion: Religion,
    val category: String,
    val isFavorite: Boolean = false
)

/**
 * Calendar event
 */
@Serializable
data class CalendarEvent(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val churchName: String = "",
    val religion: Religion? = null
)

/**
 * User model
 */
@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val religion: Religion? = null,
    val avatarUrl: String = "",
    val favoriteChurches: List<String> = emptyList()
)

/**
 * Time slot for booking
 */
data class TimeSlot(
    val time: String,
    val isAvailable: Boolean = true
)

/**
 * Date option for booking
 */
data class DateOption(
    val dayOfWeek: String,
    val dayOfMonth: Int,
    val month: String,
    val fullDate: String
)

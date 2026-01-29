package com.religiousmarket.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation destinations
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Map : Screen("map")
    object Orders : Screen("orders")
    object Prayers : Screen("prayers")
    object Profile : Screen("profile")
    object ChurchDetails : Screen("church/{churchId}") {
        fun createRoute(churchId: String) = "church/$churchId"
    }
    object Booking : Screen("booking/{churchId}/{serviceId}") {
        fun createRoute(churchId: String, serviceId: String) = "booking/$churchId/$serviceId"
    }
    object Payment : Screen("payment")
    object Success : Screen("success")
    object PrayerDetail : Screen("prayer/{prayerId}") {
        fun createRoute(prayerId: String) = "prayer/$prayerId"
    }
    object Donate : Screen("donate")
    object Donate2 : Screen("donate/{churchId}") {
        fun createRoute(churchId: String) = "donate/$churchId"
    }
}

/**
 * Bottom navigation items
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        label = "Главная",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Screen.Map.route,
        label = "Карта",
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map
    ),
    BottomNavItem(
        route = Screen.Orders.route,
        label = "Заказы",
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt
    ),
    BottomNavItem(
        route = Screen.Prayers.route,
        label = "Молитвы",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook
    ),
    BottomNavItem(
        route = Screen.Profile.route,
        label = "Профиль",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

package com.religiousmarket.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.religiousmarket.app.ui.screens.booking.BookingScreen
import com.religiousmarket.app.ui.screens.church.ChurchScreen
import com.religiousmarket.app.ui.screens.home.HomeScreen
import com.religiousmarket.app.ui.screens.map.MapScreen
import com.religiousmarket.app.ui.screens.orders.OrdersScreen
import com.religiousmarket.app.ui.screens.prayers.PrayerDetailScreen
import com.religiousmarket.app.ui.screens.prayers.PrayersScreen
import com.religiousmarket.app.ui.screens.profile.ProfileScreen
import com.religiousmarket.app.ui.theme.Success
import com.religiousmarket.app.ui.theme.LocalAppColors

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Main screens
        composable(Screen.Home.route) {
            HomeScreen(
                onChurchClick = { churchId ->
                    navController.navigate(Screen.ChurchDetails.createRoute(churchId))
                },
                onMapClick = {
                    navController.navigate(Screen.Map.route)
                },
                onDonateClick = {
                    navController.navigate(Screen.Donate.route)
                },
                onCalendarClick = {
                    // Calendar screen - not implemented yet
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                onChurchClick = { churchId ->
                    navController.navigate(Screen.ChurchDetails.createRoute(churchId))
                }
            )
        }

        composable(Screen.Orders.route) {
            OrdersScreen()
        }

        composable(Screen.Prayers.route) {
            PrayersScreen(
                onPrayerClick = { prayerId ->
                    navController.navigate(Screen.PrayerDetail.createRoute(prayerId))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onDonateClick = {
                    navController.navigate(Screen.Donate.route)
                }
            )
        }

        // Detail screens
        composable(
            route = Screen.ChurchDetails.route,
            arguments = listOf(
                navArgument("churchId") { type = NavType.StringType }
            )
        ) {
            ChurchScreen(
                onBackClick = { navController.popBackStack() },
                onServiceClick = { churchId, serviceId ->
                    navController.navigate(Screen.Booking.createRoute(churchId, serviceId))
                },
                onDonateClick = { churchId ->
                    navController.navigate(Screen.Donate2.createRoute(churchId))
                }
            )
        }

        composable(
            route = Screen.Booking.route,
            arguments = listOf(
                navArgument("churchId") { type = NavType.StringType },
                navArgument("serviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val churchId = backStackEntry.arguments?.getString("churchId") ?: ""
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""

            BookingScreen(
                churchId = churchId,
                serviceId = serviceId,
                onBackClick = { navController.popBackStack() },
                onPaymentClick = {
                    navController.navigate(Screen.Payment.route)
                }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Success.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.Success.route) {
            SuccessScreen(
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onOrdersClick = {
                    navController.navigate(Screen.Orders.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.PrayerDetail.route,
            arguments = listOf(
                navArgument("prayerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val prayerId = backStackEntry.arguments?.getString("prayerId") ?: ""

            PrayerDetailScreen(
                prayerId = prayerId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Donate.route) {
            DonateScreen(
                churchId = null,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Donate2.route,
            arguments = listOf(
                navArgument("churchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val churchId = backStackEntry.arguments?.getString("churchId")

            DonateScreen(
                churchId = churchId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// Simple Payment Screen
@Composable
fun PaymentScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Оплата",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onSuccess) {
            Text("Оплатить")
        }
    }
}

// Success Screen
@Composable
fun SuccessScreen(
    onHomeClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Success,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Успешно!",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Ваша запись подтверждена",
            style = MaterialTheme.typography.bodyLarge,
            color = LocalAppColors.current.textSecondary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onOrdersClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Мои заказы")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onHomeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("На главную")
        }
    }
}

// Donate Screen
@Composable
fun DonateScreen(
    churchId: String?,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Пожертвование",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Выберите сумму пожертвования",
            style = MaterialTheme.typography.bodyLarge
        )
        // TODO: Add donate UI here
    }
}

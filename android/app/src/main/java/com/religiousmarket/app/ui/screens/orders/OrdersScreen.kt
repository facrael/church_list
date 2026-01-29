package com.religiousmarket.app.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.religiousmarket.app.data.model.Booking
import com.religiousmarket.app.data.model.BookingStatus
import com.religiousmarket.app.ui.theme.*

@Composable
fun OrdersScreen() {
    val colors = LocalAppColors.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Активные", "Завершённые")

    // Sample bookings
    val activeBookings = listOf(
        Booking(
            id = "b1",
            userId = "u1",
            churchId = "1",
            serviceId = "s1",
            churchName = "Храм Христа Спасителя",
            serviceName = "Крещение",
            date = "28 января 2024",
            time = "10:00",
            status = BookingStatus.CONFIRMED,
            totalPrice = 3000,
            createdAt = "25 января 2024"
        ),
        Booking(
            id = "b2",
            userId = "u1",
            churchId = "1",
            serviceId = "s5",
            churchName = "Храм Христа Спасителя",
            serviceName = "Молебен",
            date = "30 января 2024",
            time = "12:00",
            status = BookingStatus.PENDING,
            totalPrice = 500,
            createdAt = "26 января 2024"
        )
    )

    val completedBookings = listOf(
        Booking(
            id = "b3",
            userId = "u1",
            churchId = "2",
            serviceId = "s10",
            churchName = "Храм Василия Блаженного",
            serviceName = "Записка о здравии",
            date = "15 января 2024",
            time = "09:00",
            status = BookingStatus.COMPLETED,
            totalPrice = 50,
            createdAt = "14 января 2024"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = colors.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, color = if (selectedTab == index) Primary else colors.textSecondary) }
                )
            }
        }

        // Content
        val bookings = if (selectedTab == 0) activeBookings else completedBookings

        if (bookings.isEmpty()) {
            EmptyOrdersState(isActive = selectedTab == 0)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    OrderCard(booking = booking)
                }
            }
        }
    }
}

@Composable
private fun OrderCard(booking: Booking) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = booking.serviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = booking.churchName,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(
                    icon = Icons.Filled.CalendarMonth,
                    label = "Дата",
                    value = booking.date
                )
                DetailItem(
                    icon = Icons.Filled.Schedule,
                    label = "Время",
                    value = booking.time
                )
                DetailItem(
                    icon = Icons.Filled.Payments,
                    label = "Цена",
                    value = "${booking.totalPrice} ₽"
                )
            }

            if (booking.status == BookingStatus.CONFIRMED || booking.status == BookingStatus.PENDING) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Cancel */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Отменить")
                    }
                    Button(
                        onClick = { /* Reschedule */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Перенести")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: BookingStatus) {
    val (color, text) = when (status) {
        BookingStatus.PENDING -> Pair(Warning, "Ожидает")
        BookingStatus.CONFIRMED -> Pair(Success, "Подтверждён")
        BookingStatus.COMPLETED -> Pair(Primary, "Завершён")
        BookingStatus.CANCELLED -> Pair(Error, "Отменён")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    val colors = LocalAppColors.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun EmptyOrdersState(isActive: Boolean) {
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (isActive) Icons.Filled.Receipt else Icons.Filled.HistoryEdu,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isActive) "Нет активных заказов" else "История пуста",
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isActive)
                "Запишитесь на услугу в ближайшем храме"
            else
                "Здесь будут отображаться завершённые заказы",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textHint
        )
    }
}

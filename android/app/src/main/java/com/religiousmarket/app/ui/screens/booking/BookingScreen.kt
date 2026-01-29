package com.religiousmarket.app.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.religiousmarket.app.data.model.DateOption
import com.religiousmarket.app.data.model.TimeSlot
import com.religiousmarket.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    churchId: String,
    serviceId: String,
    onBackClick: () -> Unit,
    onPaymentClick: () -> Unit
) {
    val colors = LocalAppColors.current

    // Sample data
    val serviceName = "Крещение"
    val churchName = "Храм Христа Спасителя"
    val price = 3000
    val duration = 60

    val dateOptions = listOf(
        DateOption("Сб", 27, "янв", "2024-01-27"),
        DateOption("Вс", 28, "янв", "2024-01-28"),
        DateOption("Пн", 29, "янв", "2024-01-29"),
        DateOption("Вт", 30, "янв", "2024-01-30"),
        DateOption("Ср", 31, "янв", "2024-01-31"),
        DateOption("Чт", 1, "фев", "2024-02-01"),
        DateOption("Пт", 2, "фев", "2024-02-02")
    )

    val timeSlots = listOf(
        TimeSlot("09:00", true),
        TimeSlot("10:00", true),
        TimeSlot("11:00", false),
        TimeSlot("12:00", true),
        TimeSlot("14:00", true),
        TimeSlot("15:00", true),
        TimeSlot("16:00", false),
        TimeSlot("17:00", true)
    )

    var selectedDate by remember { mutableStateOf(dateOptions[1]) }
    var selectedTime by remember { mutableStateOf<TimeSlot?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Бронирование", color = colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface)
            )
        },
        bottomBar = {
            // Summary and button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Итого",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "$price ₽",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    if (selectedTime != null) {
                        Text(
                            text = "${selectedDate.dayOfMonth} ${selectedDate.month}, ${selectedTime?.time}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onPaymentClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedTime != null
                    ) {
                        Text("Перейти к оплате")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colors.background),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Service info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = serviceName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = churchName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Payments,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$price ₽",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                            }
                            if (duration > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Schedule,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$duration мин",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Date selection
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Выберите дату",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dateOptions) { date ->
                        DateCard(
                            date = date,
                            isSelected = selectedDate == date,
                            onClick = { selectedDate = date }
                        )
                    }
                }
            }

            // Time selection
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Выберите время",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // Time slots grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeSlots.chunked(4).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { slot ->
                                TimeSlotCard(
                                    slot = slot,
                                    isSelected = selectedTime == slot,
                                    onClick = {
                                        if (slot.isAvailable) {
                                            selectedTime = slot
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill empty spaces
                            repeat(4 - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Additional info
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Primary.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Пожалуйста, приходите за 15 минут до назначенного времени. При себе необходимо иметь документ, удостоверяющий личность.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateCard(
    date: DateOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val backgroundColor = if (isSelected) Primary else colors.surface
    val textColor = if (isSelected) TextOnPrimary else colors.textPrimary

    Card(
        modifier = Modifier
            .width(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfWeek,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) textColor.copy(alpha = 0.8f) else colors.textSecondary
            )
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = date.month,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) textColor.copy(alpha = 0.8f) else colors.textSecondary
            )
        }
    }
}

@Composable
private fun TimeSlotCard(
    slot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val backgroundColor = when {
        !slot.isAvailable -> colors.divider
        isSelected -> Primary
        else -> colors.surface
    }
    val textColor = when {
        !slot.isAvailable -> colors.textHint
        isSelected -> TextOnPrimary
        else -> colors.textPrimary
    }
    val borderColor = if (isSelected || !slot.isAvailable) backgroundColor else colors.divider

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = slot.isAvailable) { onClick() },
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = slot.time,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

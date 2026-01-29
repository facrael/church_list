package com.religiousmarket.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.religiousmarket.app.data.model.CalendarEvent
import com.religiousmarket.app.data.model.Religion
import com.religiousmarket.app.ui.components.*
import com.religiousmarket.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChurchClick: (String) -> Unit,
    onMapClick: () -> Unit,
    onDonateClick: () -> Unit,
    onCalendarClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    if (uiState.error != null) {
        ErrorScreen(
            message = uiState.error!!,
            onRetry = { viewModel.loadData() }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Welcome section
        item {
            WelcomeSection(userName = uiState.userName)
        }

        // Quick actions
        item {
            QuickActionsSection(
                onMapClick = onMapClick,
                onDonateClick = onDonateClick,
                onCalendarClick = onCalendarClick
            )
        }

        // Nearby churches
        item {
            SectionHeader(
                title = "Ближайшие храмы",
                actionText = "Все",
                onActionClick = onMapClick
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.nearbyChurches) { church ->
                    ChurchCard(
                        church = church,
                        onClick = { onChurchClick(church.id) }
                    )
                }
            }
        }

        // Popular services
        item {
            SectionHeader(title = "Популярные услуги")
        }

        item {
            PopularServicesSection(onMapClick = onMapClick)
        }

        // Upcoming events
        item {
            SectionHeader(
                title = "Ближайшие события",
                actionText = "Все",
                onActionClick = onCalendarClick
            )
        }

        items(uiState.upcomingEvents) { event ->
            EventCard(
                event = event,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // Daily quote
        item {
            DailyQuoteCard(
                quote = uiState.dailyQuote,
                source = uiState.quoteSource
            )
        }
    }
}

@Composable
private fun WelcomeSection(userName: String) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Добрый день,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextOnPrimary.copy(alpha = 0.8f)
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextOnPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = PrimaryDark,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onMapClick: () -> Unit,
    onDonateClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionButton(
            icon = Icons.Filled.LocationOn,
            label = "Найти храм",
            color = Orthodox,
            onClick = onMapClick
        )
        QuickActionButton(
            icon = Icons.Filled.List,
            label = "Услуги",
            color = Islam,
            onClick = onMapClick
        )
        QuickActionButton(
            icon = Icons.Outlined.FavoriteBorder,
            label = "Пожертвовать",
            color = Judaism,
            onClick = onDonateClick
        )
        QuickActionButton(
            icon = Icons.Filled.CalendarMonth,
            label = "Календарь",
            color = Accent,
            onClick = onCalendarClick
        )
    }
}

@Composable
private fun PopularServicesSection(onMapClick: () -> Unit) {
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ServiceMiniCard(
            icon = "✝",
            name = "Крещение",
            price = "от 3000 ₽",
            onClick = onMapClick,
            modifier = Modifier.weight(1f)
        )
        ServiceMiniCard(
            icon = "🙏",
            name = "Молебен",
            price = "от 500 ₽",
            onClick = onMapClick,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ServiceMiniCard(
            icon = "📜",
            name = "Записки",
            price = "от 50 ₽",
            onClick = onMapClick,
            modifier = Modifier.weight(1f)
        )
        ServiceMiniCard(
            icon = "🏠",
            name = "Освящение",
            price = "от 1000 ₽",
            onClick = onMapClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceMiniCard(
    icon: String,
    name: String,
    price: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = price,
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary
                )
            }
        }
    }
}

@Composable
private fun EventCard(
    event: CalendarEvent,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date box
            Column(
                modifier = Modifier
                    .background(
                        if (event.religion != null) getReligionColor(event.religion).copy(alpha = 0.2f)
                        else Primary.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val dateParts = event.date.split(" ")
                Text(
                    text = dateParts.getOrElse(0) { "" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (event.religion != null) getReligionColor(event.religion) else Primary
                )
                Text(
                    text = dateParts.getOrElse(1) { "" },
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = event.churchName,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyQuoteCard(
    quote: String,
    source: String
) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Accent.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.FormatQuote,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = source,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary
            )
        }
    }
}

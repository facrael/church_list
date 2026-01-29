package com.religiousmarket.app.ui.screens.church

import androidx.compose.foundation.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.religiousmarket.app.data.model.Church
import com.religiousmarket.app.data.model.Service
import com.religiousmarket.app.ui.components.*
import com.religiousmarket.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChurchScreen(
    onBackClick: () -> Unit,
    onServiceClick: (String, String) -> Unit,
    onDonateClick: (String) -> Unit,
    viewModel: ChurchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Услуги", "Расписание", "О храме", "Отзывы")

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    if (uiState.error != null || uiState.church == null) {
        ErrorScreen(
            message = uiState.error ?: "Church not found",
            onRetry = { viewModel.loadChurchDetails() }
        )
        return
    }

    val church = uiState.church!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        // Header image
        item {
            ChurchHeader(
                church = church,
                isFavorite = uiState.isFavorite,
                onBackClick = onBackClick,
                onFavoriteClick = { viewModel.toggleFavorite() }
            )
        }

        // Church info
        item {
            ChurchInfo(
                church = church,
                onDonateClick = { onDonateClick(church.id) }
            )
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { Text(title) }
                    )
                }
            }
        }

        // Tab content
        when (uiState.selectedTab) {
            0 -> {
                // Services tab
                item {
                    CategoryChips(
                        categories = viewModel.getServiceCategories(),
                        selectedCategory = uiState.selectedCategory,
                        onCategoryClick = { viewModel.selectCategory(it) }
                    )
                }
                items(uiState.filteredServices) { service ->
                    ServiceCard(
                        service = service,
                        onClick = { onServiceClick(church.id, service.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            1 -> {
                // Schedule tab
                item {
                    ScheduleTab()
                }
            }
            2 -> {
                // About tab
                item {
                    AboutTab(church = church)
                }
            }
            3 -> {
                // Reviews tab
                item {
                    ReviewsTab(church = church)
                }
            }
        }

        // Bottom padding
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ChurchHeader(
    church: Church,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        if (church.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = church.imageUrl,
                contentDescription = church.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(getReligionColor(church.religion).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = church.religion.icon,
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }

        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(LocalAppColors.current.surface.copy(alpha = 0.8f), CircleShape)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        // Favorite button
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
                .background(LocalAppColors.current.surface.copy(alpha = 0.8f), CircleShape)
        ) {
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Error else LocalAppColors.current.textPrimary
            )
        }
    }
}

@Composable
private fun ChurchInfo(
    church: Church,
    onDonateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-20).dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = church.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getReligionColor(church.religion).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = church.religion.displayName,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = getReligionColor(church.religion)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Star,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${church.rating}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${church.reviewsCount} отзывов)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalAppColors.current.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = LocalAppColors.current.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = church.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalAppColors.current.textSecondary,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { /* Open maps */ }) {
                    Icon(
                        Icons.Filled.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Маршрут")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Donate button
            Button(
                onClick = onDonateClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Пожертвовать храму")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategoryClick(category) },
                label = {
                    Text(if (category == "all") "Все" else category)
                }
            )
        }
    }
}

@Composable
private fun ScheduleTab() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Сегодня, 27 января",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ScheduleItem("08:00", "Утренняя литургия")
        ScheduleItem("12:00", "Молебен")
        ScheduleItem("17:00", "Вечернее богослужение")

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Завтра, 28 января",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ScheduleItem("09:00", "Воскресная литургия")
        ScheduleItem("11:00", "Крещение")
    }
}

@Composable
private fun ScheduleItem(time: String, event: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.titleSmall,
            color = Primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = event,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AboutTab(church: Church) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = church.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Контакты",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (church.phone.isNotEmpty()) {
            ContactItem(Icons.Filled.Phone, church.phone)
        }
        if (church.email.isNotEmpty()) {
            ContactItem(Icons.Filled.Email, church.email)
        }
        if (church.website.isNotEmpty()) {
            ContactItem(Icons.Filled.Language, church.website)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Часы работы",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        church.workingHours.forEach { (day, hours) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = hours,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalAppColors.current.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ContactItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ReviewsTab(church: Church) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${church.rating}",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Row {
            repeat(5) { index ->
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < church.rating.toInt()) Star else LocalAppColors.current.divider,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = "${church.reviewsCount} отзывов",
            style = MaterialTheme.typography.bodyMedium,
            color = LocalAppColors.current.textSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sample reviews
        ReviewCard(
            author = "Мария К.",
            date = "15 января 2024",
            rating = 5,
            text = "Прекрасный храм с удивительной атмосферой. Очень приветливые служители."
        )
        ReviewCard(
            author = "Алексей П.",
            date = "10 января 2024",
            rating = 5,
            text = "Красивое место с богатой историей. Рекомендую посетить."
        )
    }
}

@Composable
private fun ReviewCard(
    author: String,
    date: String,
    rating: Int,
    text: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = author,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalAppColors.current.textSecondary
                )
            }
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                repeat(5) { index ->
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (index < rating) Star else LocalAppColors.current.divider,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

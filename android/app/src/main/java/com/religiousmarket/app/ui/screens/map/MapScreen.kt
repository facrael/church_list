package com.religiousmarket.app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.religiousmarket.app.data.model.Church
import com.religiousmarket.app.data.model.Religion
import com.religiousmarket.app.ui.components.*
import com.religiousmarket.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onChurchClick: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Map placeholder section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            // Map Placeholder
            MapPlaceholder(
                churchesCount = uiState.filteredChurches.size
            )

            // Map legend
            MapLegend(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )

            // Location button
            FloatingActionButton(
                onClick = { /* TODO: Request location */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(48.dp),
                containerColor = colors.surface
            ) {
                Icon(
                    Icons.Filled.MyLocation,
                    contentDescription = "My location",
                    tint = Primary
                )
            }

            // Places count
            if (uiState.filteredChurches.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Primary
                ) {
                    Text(
                        text = "Найдено: ${uiState.filteredChurches.size}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        // Filter chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ReligionFilterChip(
                    religion = null,
                    label = "Все",
                    selected = uiState.selectedReligion == null,
                    onClick = { viewModel.selectReligion(null) }
                )
            }
            item {
                ReligionFilterChip(
                    religion = Religion.ORTHODOX,
                    label = "Православие",
                    selected = uiState.selectedReligion == Religion.ORTHODOX,
                    onClick = { viewModel.selectReligion(Religion.ORTHODOX) }
                )
            }
            item {
                ReligionFilterChip(
                    religion = Religion.ISLAM,
                    label = "Ислам",
                    selected = uiState.selectedReligion == Religion.ISLAM,
                    onClick = { viewModel.selectReligion(Religion.ISLAM) }
                )
            }
            item {
                ReligionFilterChip(
                    religion = Religion.JUDAISM,
                    label = "Иудаизм",
                    selected = uiState.selectedReligion == Religion.JUDAISM,
                    onClick = { viewModel.selectReligion(Religion.JUDAISM) }
                )
            }
        }

        // Search bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text(
                    "Поиск храма или адреса...",
                    color = colors.textHint
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = colors.textSecondary
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = "Clear",
                            tint = colors.textSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                focusedBorderColor = Primary,
                unfocusedBorderColor = colors.divider,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Churches list
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredChurches) { church ->
                    ChurchListItem(
                        church = church,
                        onClick = { onChurchClick(church.id) }
                    )
                }
            }
        }
    }
}

/**
 * Map placeholder - заглушка для карты
 * TODO: Заменить на 2GIS MapView когда SDK будет доступен
 */
@Composable
fun MapPlaceholder(
    churchesCount: Int
) {
    val colors = LocalAppColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        // Grid pattern to simulate map
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Map icon
            Icon(
                Icons.Filled.Map,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Primary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Карта 2GIS",
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Интеграция карты в разработке",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simulated markers
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MarkerIcon(color = Orthodox, symbol = "☦")
                MarkerIcon(color = Islam, symbol = "☪")
                MarkerIcon(color = Judaism, symbol = "✡")
            }
        }
    }
}

@Composable
private fun MarkerIcon(color: Color, symbol: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 4.dp, bottomStart = 20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun MapLegend(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LegendItem(color = Orthodox, label = "Православие")
            LegendItem(color = Islam, label = "Ислам")
            LegendItem(color = Judaism, label = "Иудаизм")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    val colors = LocalAppColors.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textPrimary
        )
    }
}

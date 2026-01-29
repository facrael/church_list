package com.religiousmarket.app.ui.screens.prayers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.religiousmarket.app.data.model.Prayer
import com.religiousmarket.app.data.model.Religion
import com.religiousmarket.app.data.repository.PrayerRepository
import com.religiousmarket.app.ui.components.*
import com.religiousmarket.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrayersUiState(
    val isLoading: Boolean = true,
    val prayers: List<Prayer> = emptyList(),
    val filteredPrayers: List<Prayer> = emptyList(),
    val userReligion: Religion = Religion.ORTHODOX, // Default religion from profile
    val error: String? = null
)

@HiltViewModel
class PrayersViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayersUiState())
    val uiState: StateFlow<PrayersUiState> = _uiState.asStateFlow()

    init {
        loadPrayers()
    }

    fun loadPrayers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                prayerRepository.getPrayers().collect { prayers ->
                    // Filter by user's religion from profile
                    val userReligion = _uiState.value.userReligion
                    val filtered = prayers.filter { it.religion == userReligion }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        prayers = prayers,
                        filteredPrayers = filtered
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun setUserReligion(religion: Religion) {
        _uiState.value = _uiState.value.copy(userReligion = religion)
        filterPrayers()
    }

    private fun filterPrayers() {
        val state = _uiState.value
        val filtered = state.prayers.filter { it.religion == state.userReligion }
        _uiState.value = _uiState.value.copy(filteredPrayers = filtered)
    }
}

@Composable
fun PrayersScreen(
    onPrayerClick: (String) -> Unit,
    viewModel: PrayersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Header with user's religion
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = getReligionColor(uiState.userReligion).copy(alpha = 0.15f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.userReligion.icon,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Молитвы",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.userReligion.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = getReligionColor(uiState.userReligion)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${uiState.filteredPrayers.size}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = getReligionColor(uiState.userReligion),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (uiState.isLoading) {
            LoadingScreen()
        } else if (uiState.filteredPrayers.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.userReligion.icon,
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Молитвы не найдены",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Скоро появятся молитвы для ${uiState.userReligion.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredPrayers) { prayer ->
                    PrayerCard(
                        prayer = prayer,
                        onClick = { onPrayerClick(prayer.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerCard(
    prayer: Prayer,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Religion icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        getReligionColor(prayer.religion).copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = prayer.religion.icon,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prayer.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = prayer.text.take(60) + "...",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.textSecondary
            )
        }
    }
}

// Prayer Detail Screen
@Composable
fun PrayerDetailScreen(
    prayerId: String,
    onBackClick: () -> Unit,
    viewModel: PrayersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val prayer = uiState.prayers.find { it.id == prayerId }
    val colors = LocalAppColors.current

    if (prayer == null) {
        LoadingScreen()
        return
    }

    var fontSize by remember { mutableStateOf(18) }
    var isFavorite by remember { mutableStateOf(prayer.isFavorite) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.textPrimary
                )
            }
            Row {
                IconButton(onClick = { fontSize = (fontSize - 2).coerceAtLeast(14) }) {
                    Icon(
                        Icons.Filled.TextDecrease,
                        contentDescription = "Decrease font",
                        tint = colors.textPrimary
                    )
                }
                IconButton(onClick = { fontSize = (fontSize + 2).coerceAtMost(28) }) {
                    Icon(
                        Icons.Filled.TextIncrease,
                        contentDescription = "Increase font",
                        tint = colors.textPrimary
                    )
                }
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Error else colors.textPrimary
                    )
                }
            }
        }

        // Prayer content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                Text(
                    text = prayer.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getReligionColor(prayer.religion).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = prayer.religion.displayName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = getReligionColor(prayer.religion)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Prayer text
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Text(
                        text = prayer.text,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize * 1.6).sp
                        ),
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}

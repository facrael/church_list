package com.religiousmarket.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.religiousmarket.app.data.model.Church
import com.religiousmarket.app.data.model.Religion
import com.religiousmarket.app.data.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val isLoading: Boolean = true,
    val churches: List<Church> = emptyList(),
    val filteredChurches: List<Church> = emptyList(),
    val selectedReligion: Religion? = null,
    val searchQuery: String = "",
    val selectedChurch: Church? = null,
    val userLatitude: Double = 55.7558,
    val userLongitude: Double = 37.6176,
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val churchRepository: ChurchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadChurches()
    }

    fun loadChurches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                churchRepository.getChurches().collect { churches ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        churches = churches,
                        filteredChurches = churches
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun selectReligion(religion: Religion?) {
        _uiState.value = _uiState.value.copy(selectedReligion = religion)
        filterChurches()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterChurches()
    }

    private fun filterChurches() {
        val state = _uiState.value
        var filtered = state.churches

        // Filter by religion
        if (state.selectedReligion != null) {
            filtered = filtered.filter { it.religion == state.selectedReligion }
        }

        // Filter by search query
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(state.searchQuery, ignoreCase = true) ||
                it.address.contains(state.searchQuery, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(filteredChurches = filtered)
    }

    fun selectChurch(church: Church?) {
        _uiState.value = _uiState.value.copy(selectedChurch = church)
    }

    fun updateUserLocation(latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value.copy(
            userLatitude = latitude,
            userLongitude = longitude
        )
    }
}

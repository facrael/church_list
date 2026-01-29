package com.religiousmarket.app.ui.screens.church

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.religiousmarket.app.data.model.Church
import com.religiousmarket.app.data.model.Service
import com.religiousmarket.app.data.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChurchUiState(
    val isLoading: Boolean = true,
    val church: Church? = null,
    val services: List<Service> = emptyList(),
    val filteredServices: List<Service> = emptyList(),
    val selectedTab: Int = 0,
    val selectedCategory: String = "all",
    val isFavorite: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChurchViewModel @Inject constructor(
    private val churchRepository: ChurchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val churchId: String = savedStateHandle["churchId"] ?: ""

    private val _uiState = MutableStateFlow(ChurchUiState())
    val uiState: StateFlow<ChurchUiState> = _uiState.asStateFlow()

    init {
        loadChurchDetails()
    }

    fun loadChurchDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                churchRepository.getChurchById(churchId).collect { church ->
                    if (church != null) {
                        churchRepository.getServicesForChurch(churchId).collect { services ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                church = church,
                                services = services,
                                filteredServices = services,
                                isFavorite = church.isFavorite
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Church not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        filterServices()
    }

    private fun filterServices() {
        val state = _uiState.value
        val filtered = if (state.selectedCategory == "all") {
            state.services
        } else {
            state.services.filter { it.category == state.selectedCategory }
        }
        _uiState.value = _uiState.value.copy(filteredServices = filtered)
    }

    fun toggleFavorite() {
        _uiState.value = _uiState.value.copy(
            isFavorite = !_uiState.value.isFavorite
        )
        // In production, save to repository
    }

    fun getServiceCategories(): List<String> {
        return listOf("all") + _uiState.value.services.map { it.category }.distinct()
    }
}

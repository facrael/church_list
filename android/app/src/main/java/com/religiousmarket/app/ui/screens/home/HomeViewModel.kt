package com.religiousmarket.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.religiousmarket.app.data.model.CalendarEvent
import com.religiousmarket.app.data.model.Church
import com.religiousmarket.app.data.model.Religion
import com.religiousmarket.app.data.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "Алексей",
    val nearbyChurches: List<Church> = emptyList(),
    val upcomingEvents: List<CalendarEvent> = emptyList(),
    val dailyQuote: String = "«Любите друг друга, как Я возлюбил вас»",
    val quoteSource: String = "Ин. 15:12",
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val churchRepository: ChurchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                churchRepository.getChurches().collect { churches ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        nearbyChurches = churches,
                        upcomingEvents = getSampleEvents()
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

    private fun getSampleEvents(): List<CalendarEvent> {
        return listOf(
            CalendarEvent(
                id = "e1",
                title = "Воскресная литургия",
                description = "Божественная литургия",
                date = "28 января",
                time = "09:00",
                churchName = "Храм Христа Спасителя",
                religion = Religion.ORTHODOX
            ),
            CalendarEvent(
                id = "e2",
                title = "Сретение Господне",
                description = "Великий праздник",
                date = "2 февраля",
                time = "Весь день",
                churchName = "Все православные храмы",
                religion = Religion.ORTHODOX
            ),
            CalendarEvent(
                id = "e3",
                title = "Джума-намаз",
                description = "Пятничная молитва",
                date = "31 января",
                time = "13:00",
                churchName = "Московская Соборная мечеть",
                religion = Religion.ISLAM
            )
        )
    }
}

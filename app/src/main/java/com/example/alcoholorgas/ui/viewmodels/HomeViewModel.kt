package com.example.alcoholorgas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alcoholorgas.data.ApplicationSettings
import com.example.alcoholorgas.data.model.Coordinates
import com.example.alcoholorgas.data.model.GasStation
import com.example.alcoholorgas.data.repository.GasStationRepository
import com.example.alcoholorgas.ui.state.HomeUiState
import com.example.alcoholorgas.util.calculateIsAlcoholBetter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel
// https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
class HomeViewModel(
    private val stationRepo: GasStationRepository,
    private val settingsRepo: ApplicationSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(checked = settingsRepo.isChecked()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onAlcoholPriceChange(value: String) {
        _uiState.update { it.copy(alcoholPrice = value) }
    }

    fun onGasolinePriceChange(value: String) {
        _uiState.update { it.copy(gasolinePrice = value) }
    }

    fun onStationNameChange(value: String) {
        _uiState.update { it.copy(station = value) }
    }

    fun onTogglePercentage(value: Boolean) {
        settingsRepo.setIsChecked(value)
        _uiState.update { it.copy(checked = value) }
    }

    fun onCalculateResult(): Boolean? {
        val state = _uiState.value
        val alcohol = state.alcoholPrice.toDoubleOrNull()
        val gasoline = state.gasolinePrice.toDoubleOrNull()

        if (alcohol == null || gasoline == null) return null

        val isAlcoholBetter = calculateIsAlcoholBetter(alcohol, gasoline, state.checked)

        _uiState.update { it.copy(result = isAlcoholBetter) }
        return isAlcoholBetter
    }

    fun saveStation(lat: Double, lng: Double, onSaved: () -> Unit) {
        val state = _uiState.value
        val coordinates = Coordinates(lat, lng)

        /*
            - Coordenadas da UFC para mock do video
            val mockCoordinates = Coordinates(-3.742644, -38.574753)
        */
        stationRepo.save(
            GasStation(
                name = state.station,
                coord = coordinates,
                alcoholPrice = state.alcoholPrice.toDoubleOrNull() ?: 0.0,
                gasolinePrice = state.gasolinePrice.toDoubleOrNull() ?: 0.0
            )
        )
        _uiState.update { it.copy(station = "") }
        onSaved()
    }

    companion object {
        fun provideFactory(
            stationRepo: GasStationRepository,
            settingsRepo: ApplicationSettings
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(stationRepo, settingsRepo)
            }
        }
    }
}

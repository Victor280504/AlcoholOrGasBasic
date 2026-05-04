package com.example.alcoholorgas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alcoholorgas.data.ApplicationSettings
import com.example.alcoholorgas.data.model.GasStation
import com.example.alcoholorgas.data.repository.GasStationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StationsViewModel(
    private val repository: GasStationRepository,
    private val settings: ApplicationSettings
) : ViewModel() {

    private val _stations = MutableStateFlow<List<GasStation>>(emptyList())
    val stations: StateFlow<List<GasStation>> = _stations.asStateFlow()

    val is75Percent = settings.isChecked()

    init {
        loadStations()
    }

    fun loadStations() {
        _stations.value = repository.findAll()
    }

    companion object {
        fun provideFactory(
            repository: GasStationRepository,
            settings: ApplicationSettings
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StationsViewModel(repository, settings)
            }
        }
    }
}

package com.example.alcoholorgas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alcoholorgas.data.model.Coordinates
import com.example.alcoholorgas.data.model.GasStation
import com.example.alcoholorgas.data.repository.GasStationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditViewModel(
    private val repository: GasStationRepository,
    initialStation: GasStation?
) : ViewModel() {

    private val _station = MutableStateFlow(initialStation)
    val station: StateFlow<GasStation?> = _station.asStateFlow()

    private val _name = MutableStateFlow(initialStation?.name ?: "")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _alcoholPrice = MutableStateFlow(initialStation?.alcoholPrice?.toString() ?: "")
    val alcoholPrice: StateFlow<String> = _alcoholPrice.asStateFlow()

    private val _gasolinePrice = MutableStateFlow(initialStation?.gasolinePrice?.toString() ?: "")
    val gasolinePrice: StateFlow<String> = _gasolinePrice.asStateFlow()

    fun onNameChange(value: String) { _name.value = value }
    fun onAlcoholPriceChange(value: String) { _alcoholPrice.value = value }
    fun onGasolinePriceChange(value: String) { _gasolinePrice.value = value }

    fun updateLocation(lat: Double, lng: Double) {
        val currentStation = _station.value ?: return
        _station.value = currentStation.copy(coord = Coordinates(lat, lng))
    }

    fun save(onSaved: () -> Unit) {
        val currentStation = _station.value ?: return
        val updated = currentStation.copy(
            name = _name.value,
            alcoholPrice = _alcoholPrice.value.toDoubleOrNull() ?: 0.0,
            gasolinePrice = _gasolinePrice.value.toDoubleOrNull() ?: 0.0
        )
        repository.update(updated)
        onSaved()
    }
    fun deleteStation(station: GasStation?, onDeleted: () -> Unit) {
        station?.let {
            repository.delete(it)
            onDeleted()
        }
    }
    companion object {
        fun provideFactory(
            repository: GasStationRepository,
            station: GasStation?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                EditViewModel(repository, station)
            }
        }
    }
}

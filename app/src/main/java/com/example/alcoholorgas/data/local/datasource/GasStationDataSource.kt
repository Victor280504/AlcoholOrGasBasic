package com.example.alcoholorgas.data.local.datasource

import com.example.alcoholorgas.data.model.GasStation
import java.util.UUID

interface GasStationDataSource {
    fun save(data: GasStation): UUID
    fun deleteById(id: UUID): Boolean
    fun findAll(): List<GasStation>
    fun update(data: GasStation): Boolean
}
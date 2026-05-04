package com.example.alcoholorgas.data.repository

import android.content.Context
import com.example.alcoholorgas.data.local.SharedPrefsWrapper
import com.example.alcoholorgas.data.local.datasource.GasStationDataSource
import com.example.alcoholorgas.data.local.datasource.SharedPrefsDataSource
import com.example.alcoholorgas.data.model.GasStation
import java.util.UUID

interface Repository<T, ID> {
    fun save(data: T): ID
    fun delete(data: T): Boolean
    fun deleteById(id: ID): Boolean
    fun findById(id: ID): T?
    fun findAll(): List<T>
    fun update(data: T): Boolean
}

class GasStationRepository(
    private val dataSource: GasStationDataSource,
) : Repository<GasStation, UUID> {

    override fun save(data: GasStation) = dataSource.save(data)
    override fun deleteById(id: UUID) = dataSource.deleteById(id)
    override fun findAll() = dataSource.findAll()
    override fun update(data: GasStation) = dataSource.update(data)
    override fun delete(data: GasStation): Boolean {
        return deleteById(data.uuid)
    }
    override fun findById(id: UUID): GasStation? {
        return findAll().find { station -> station.uuid == id }
    }
    companion object {
        fun createSharedPrefsStore(context: Context): GasStationRepository {
            return GasStationRepository(
                SharedPrefsDataSource(SharedPrefsWrapper(context, "gas_station_db_file")),
            )

        }
    }
}

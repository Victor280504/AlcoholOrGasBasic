package com.example.alcoholorgas.data.local.datasource

import com.example.alcoholorgas.data.local.SharedPrefsWrapper
import com.example.alcoholorgas.data.mapper.toGasStation
import com.example.alcoholorgas.data.mapper.toJson
import com.example.alcoholorgas.data.model.GasStation
import org.json.JSONArray
import java.util.UUID

class SharedPrefsDataSource(
    private val prefs: SharedPrefsWrapper,
    private val jsonKey: String = "shared_prefs_json_key"
) : GasStationDataSource {

    override fun findAll(): List<GasStation> {
        val array = prefs.getJSONArray(jsonKey)
        val list = mutableListOf<GasStation>()
        for (i in 0 until array.length()) {
            list.add(array.getJSONObject(i).toGasStation())
        }
        return list
    }

    override fun save(data: GasStation): UUID {
        val list = findAll().toMutableList()
        
        if (list.size >= 10) {
            list.removeAt(list.size - 1)
        }
        
        list.add(0, data)
        saveAll(list)
        return data.uuid
    }

    override fun deleteById(id: UUID): Boolean {
        val list = findAll().toMutableList()
        val removed = list.removeIf { it.uuid == id }
        if (removed) saveAll(list)
        return removed
    }

    override fun update(data: GasStation): Boolean {
        val list = findAll().toMutableList()
        val index = list.indexOfFirst { it.uuid == data.uuid }
        if (index != -1) {
            list[index] = data
            saveAll(list)
            return true
        }
        return false
    }

    private fun saveAll(list: List<GasStation>) {
        val array = JSONArray()
        list.forEach { array.put(it.toJson()) }
        prefs.save(jsonKey, array)
    }
}
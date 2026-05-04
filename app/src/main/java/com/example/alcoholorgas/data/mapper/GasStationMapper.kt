package com.example.alcoholorgas.data.mapper

import com.example.alcoholorgas.data.model.Coordinates
import com.example.alcoholorgas.data.model.GasStation
import org.json.JSONObject
import java.util.Date
import java.util.UUID

fun JSONObject.toGasStation(): GasStation {
    return GasStation(
        uuid = UUID.fromString(optString("uuid")),
        name = optString("name"),
        coord = Coordinates(optDouble("lat"), optDouble("lgt")),
        alcoholPrice = optDouble("alcoholPrice"),
        gasolinePrice = optDouble("gasolinePrice"),
        registerDate = Date(optLong("registerDate"))
    )
}

fun GasStation.toJson(): JSONObject {
    return JSONObject().apply {
        put("uuid", uuid.toString())
        put("name", name)
        put("lat", coord.lat)
        put("lgt", coord.lgt)
        put("alcoholPrice", alcoholPrice)
        put("gasolinePrice", gasolinePrice)
        put("registerDate", registerDate.time)
    }
}


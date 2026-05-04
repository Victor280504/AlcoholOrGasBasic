package com.example.alcoholorgas.data.model

import java.io.Serializable
import java.util.Date
import java.util.UUID

data class GasStation(
    var name: String,
    val coord: Coordinates,
    var alcoholPrice: Double = 0.0,
    var gasolinePrice: Double = 0.0,
    var registerDate: Date = Date(),
    val uuid: UUID = UUID.randomUUID()
) : Serializable {
    constructor(name: String) : this(
        name,
        Coordinates(41.40338, 2.17403),
        0.0,
        0.0
    )
}

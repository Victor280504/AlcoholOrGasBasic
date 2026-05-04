package com.example.alcoholorgas.ui.state

data class HomeUiState(
    val alcoholPrice: String = "",
    val gasolinePrice: String = "",
    val station: String = "",
    val checked: Boolean = false,
    val result: Boolean? = null
)
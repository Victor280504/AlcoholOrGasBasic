package com.example.alcoholorgas.util

fun calculateIsAlcoholBetter(alcohol: Double, gasoline: Double, is75Percent: Boolean): Boolean? {
    if (gasoline == 0.0) return null
    val percentage = if (is75Percent) 0.75 else 0.7
    return (alcohol / gasoline) <= percentage
}

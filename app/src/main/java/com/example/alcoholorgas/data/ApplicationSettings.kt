package com.example.alcoholorgas.data

import android.content.Context
import com.example.alcoholorgas.data.local.SharedPrefsWrapper

class ApplicationSettings(context: Context) {
    private val prefs = SharedPrefsWrapper(context, "app_settings")
    private val KEY_USE_75 = "use_75_percent"
    private val IS_FIRST_LAUNCH = "is_first_launch"
    fun isChecked(): Boolean = prefs.getBoolean(KEY_USE_75)
    fun isFirstLaunch(): Boolean = prefs.getBoolean(IS_FIRST_LAUNCH, true)
    fun setIsChecked(value: Boolean) = prefs.save(KEY_USE_75, value)
    fun setIsFirstLaunch(value: Boolean) = prefs.save(IS_FIRST_LAUNCH, value)
}
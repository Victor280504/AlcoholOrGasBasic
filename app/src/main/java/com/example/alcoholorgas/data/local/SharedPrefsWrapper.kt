package com.example.alcoholorgas.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONArray

class SharedPrefsWrapper(
    context: Context,
    sharedFileName: String = "app_data",
    mode: Int = Context.MODE_PRIVATE
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(sharedFileName, mode)

    fun save(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun save(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    fun save(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    fun save(key: String, value: JSONArray) {
        save(key, value.toString())
    }
    fun getString(key: String, default: String = ""): String? =
        prefs.getString(key, default)

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun getInt(key: String): Int =
        prefs.getInt(key, 0)

    fun getJSONArray(key: String): JSONArray {
        val raw = prefs.getString(key, "[]") ?: "[]"
        return JSONArray(raw)
    }
}
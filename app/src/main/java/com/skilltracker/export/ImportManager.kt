package com.skilltracker.export

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import java.io.IOException

class ImportManager(private val context: Context) {

    private val gson = Gson()

    fun importFromJson(uri: Uri): ExportData? {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader().readText()
            } ?: return null

            val data = gson.fromJson(json, ExportData::class.java)
            when {
                data.version <= 0 -> null
                data.skills.any { it.name.isBlank() } -> null
                else -> data
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

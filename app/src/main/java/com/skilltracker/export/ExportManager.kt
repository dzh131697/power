package com.skilltracker.export

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.skilltracker.domain.model.Skill
import java.io.IOException

class ExportManager(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun exportToJson(skills: List<Skill>, uri: Uri): Boolean {
        return try {
            val exportData = ExportData(version = 1, skills = skills)
            val json = gson.toJson(exportData)
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(json.toByteArray(Charsets.UTF_8))
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}

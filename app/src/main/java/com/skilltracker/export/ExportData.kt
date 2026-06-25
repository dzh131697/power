package com.skilltracker.export

import com.skilltracker.domain.model.Skill

data class ExportData(
    val version: Int = 1,
    val skills: List<Skill> = emptyList()
)

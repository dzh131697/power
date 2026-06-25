package com.skilltracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val value: Int,
    val color: Long,
    val strengths: String,
    val improvements: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

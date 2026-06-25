package com.skilltracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "skill_history",
    foreignKeys = [
        ForeignKey(
            entity = SkillEntity::class,
            parentColumns = ["id"],
            childColumns = ["skill_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["skill_id"])]
)
data class SkillHistoryEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "skill_id")
    val skillId: String,
    val timestamp: Long,
    val delta: Int,
    val reason: String?
)

package com.skilltracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skilltracker.data.database.dao.SkillDao
import com.skilltracker.data.database.dao.SkillHistoryDao
import com.skilltracker.data.database.entity.SkillEntity
import com.skilltracker.data.database.entity.SkillHistoryEntity

@Database(
    entities = [SkillEntity::class, SkillHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun skillDao(): SkillDao
    abstract fun skillHistoryDao(): SkillHistoryDao
}

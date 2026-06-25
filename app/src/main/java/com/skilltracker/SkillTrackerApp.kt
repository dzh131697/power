package com.skilltracker

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.skilltracker.data.database.AppDatabase
import com.skilltracker.data.database.entity.SkillEntity
import com.skilltracker.data.database.entity.SkillHistoryEntity
import com.skilltracker.data.repository.SkillRepositoryImpl
import com.skilltracker.domain.repository.SkillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

class SkillTrackerApp : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var repository: SkillRepository
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "skill_tracker.db"
        )
            .addCallback(SeedCallback())
            .build()

        repository = SkillRepositoryImpl(database.skillDao(), database.skillHistoryDao())
    }

    private inner class SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch {
                seedDatabase()
            }
        }
    }

    private suspend fun seedDatabase() {
        val gson = Gson()
        val skillDao = database.skillDao()
        val historyDao = database.skillHistoryDao()
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        val seedSkills = listOf(
            SeedSkill("Java", 75, 0xFFFF9800,
                listOf("OOP", "Collections", "Concurrency"),
                listOf("Streams API", "Records", "Virtual Threads"),
                listOf(5, 3, 7, 4, 6)),
            SeedSkill("System Design", 60, 0xFF2196F3,
                listOf("Scalability", "Caching", "Load Balancing"),
                listOf("Distributed Systems", "Event-Driven Architecture"),
                listOf(4, 6, 3, 5, 7)),
            SeedSkill("Architecture", 70, 0xFF4CAF50,
                listOf("Clean Architecture", "MVVM", "SOLID"),
                listOf("Microservices", "Domain-Driven Design"),
                listOf(5, 5, 5, 5, 5)),
            SeedSkill("English", 65, 0xFF9C27B0,
                listOf("Reading", "Writing", "Vocabulary"),
                listOf("Speaking Fluency", "Listening Comprehension"),
                listOf(3, 4, 5, 6, 7))
        )

        seedSkills.forEachIndexed { index, seed ->
            val skillId = (index + 1).toString()
            skillDao.insertSkill(
                SkillEntity(
                    id = skillId,
                    name = seed.name,
                    value = seed.value,
                    color = seed.color,
                    strengths = gson.toJson(seed.strengths),
                    improvements = gson.toJson(seed.improvements),
                    createdAt = now,
                    updatedAt = now
                )
            )
            seed.deltas.forEachIndexed { i, delta ->
                historyDao.insertHistory(
                    SkillHistoryEntity(
                        id = UUID.randomUUID().toString(),
                        skillId = skillId,
                        timestamp = now - (seed.deltas.size - i) * dayMs,
                        delta = delta,
                        reason = "Sample data"
                    )
                )
            }
        }
    }

    private data class SeedSkill(
        val name: String,
        val value: Int,
        val color: Long,
        val strengths: List<String>,
        val improvements: List<String>,
        val deltas: List<Int>
    )

    companion object {
        lateinit var instance: SkillTrackerApp
            private set
    }
}

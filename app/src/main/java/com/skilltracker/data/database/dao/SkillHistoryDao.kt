package com.skilltracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.skilltracker.data.database.entity.SkillHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillHistoryDao {
    @Query("SELECT * FROM skill_history WHERE skill_id = :skillId ORDER BY timestamp DESC")
    fun getHistoryForSkill(skillId: String): Flow<List<SkillHistoryEntity>>

    @Insert
    suspend fun insertHistory(history: SkillHistoryEntity)

    @Query("DELETE FROM skill_history WHERE skill_id = :skillId")
    suspend fun deleteHistoryForSkill(skillId: String)
}

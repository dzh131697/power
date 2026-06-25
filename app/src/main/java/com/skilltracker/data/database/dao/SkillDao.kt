package com.skilltracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.skilltracker.data.database.entity.SkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills ORDER BY name ASC")
    fun getAllSkills(): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills WHERE id = :id")
    fun getSkillById(id: String): Flow<SkillEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: SkillEntity)

    @Update
    suspend fun updateSkill(skill: SkillEntity)

    @Delete
    suspend fun deleteSkill(skill: SkillEntity)

    @Query("DELETE FROM skills WHERE id = :id")
    suspend fun deleteSkillById(id: String)
}

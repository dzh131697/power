package com.skilltracker.domain.repository

import com.skilltracker.domain.model.Skill
import com.skilltracker.domain.model.SkillHistory
import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    fun getAllSkills(): Flow<List<Skill>>
    fun getSkillById(id: String): Flow<Skill?>
    suspend fun insertSkill(skill: Skill)
    suspend fun updateSkill(skill: Skill)
    suspend fun deleteSkill(id: String)
    fun getHistoryForSkill(skillId: String): Flow<List<SkillHistory>>
    suspend fun addHistory(history: SkillHistory)
}

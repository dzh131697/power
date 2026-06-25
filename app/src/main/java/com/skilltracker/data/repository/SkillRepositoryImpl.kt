package com.skilltracker.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skilltracker.data.database.dao.SkillDao
import com.skilltracker.data.database.dao.SkillHistoryDao
import com.skilltracker.data.database.entity.SkillEntity
import com.skilltracker.data.database.entity.SkillHistoryEntity
import com.skilltracker.domain.model.Skill
import com.skilltracker.domain.model.SkillHistory
import com.skilltracker.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SkillRepositoryImpl(
    private val skillDao: SkillDao,
    private val historyDao: SkillHistoryDao
) : SkillRepository {

    private val gson = Gson()
    private val stringListType = object : TypeToken<List<String>>() {}.type

    override fun getAllSkills(): Flow<List<Skill>> {
        return skillDao.getAllSkills().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSkillById(id: String): Flow<Skill?> {
        return skillDao.getSkillById(id).map { it?.toDomain() }
    }

    override suspend fun insertSkill(skill: Skill) {
        skillDao.insertSkill(skill.toEntity())
    }

    override suspend fun updateSkill(skill: Skill) {
        skillDao.updateSkill(skill.toEntity())
    }

    override suspend fun deleteSkill(id: String) {
        skillDao.deleteSkillById(id)
    }

    override fun getHistoryForSkill(skillId: String): Flow<List<SkillHistory>> {
        return historyDao.getHistoryForSkill(skillId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addHistory(history: SkillHistory) {
        historyDao.insertHistory(history.toEntity())
    }

    private fun SkillEntity.toDomain() = Skill(
        id = id,
        name = name,
        value = value,
        color = color,
        strengths = gson.fromJson(strengths, stringListType),
        improvements = gson.fromJson(improvements, stringListType)
    )

    private fun Skill.toEntity() = SkillEntity(
        id = id,
        name = name,
        value = value,
        color = color,
        strengths = gson.toJson(strengths),
        improvements = gson.toJson(improvements),
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    private fun SkillHistoryEntity.toDomain() = SkillHistory(
        id = id,
        skillId = skillId,
        timestamp = timestamp,
        delta = delta,
        reason = reason
    )

    private fun SkillHistory.toEntity() = SkillHistoryEntity(
        id = id,
        skillId = skillId,
        timestamp = timestamp,
        delta = delta,
        reason = reason
    )
}

package com.skilltracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skilltracker.domain.model.Skill
import com.skilltracker.domain.model.SkillHistory
import com.skilltracker.domain.repository.SkillRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: SkillRepository,
    private val skillId: String
) : ViewModel() {

    val skill: StateFlow<Skill?> = repository.getSkillById(skillId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val history: StateFlow<List<SkillHistory>> = repository.getHistoryForSkill(skillId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateValue(delta: Int) {
        viewModelScope.launch {
            val current = skill.value ?: return@launch
            val newValue = (current.value + delta).coerceIn(0, 100)
            val updated = current.copy(value = newValue)
            repository.updateSkill(updated)
            repository.addHistory(
                SkillHistory(
                    skillId = skillId,
                    delta = delta,
                    reason = "Manual update"
                )
            )
        }
    }

    fun addStrength(strength: String) {
        viewModelScope.launch {
            val current = skill.value ?: return@launch
            val updated = current.copy(strengths = current.strengths + strength)
            repository.updateSkill(updated)
        }
    }

    fun removeStrength(strength: String) {
        viewModelScope.launch {
            val current = skill.value ?: return@launch
            val updated = current.copy(strengths = current.strengths - strength)
            repository.updateSkill(updated)
        }
    }

    fun addImprovement(improvement: String) {
        viewModelScope.launch {
            val current = skill.value ?: return@launch
            val updated = current.copy(improvements = current.improvements + improvement)
            repository.updateSkill(updated)
        }
    }

    fun removeImprovement(improvement: String) {
        viewModelScope.launch {
            val current = skill.value ?: return@launch
            val updated = current.copy(improvements = current.improvements - improvement)
            repository.updateSkill(updated)
        }
    }

    class Factory(
        private val repository: SkillRepository,
        private val skillId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(repository, skillId) as T
        }
    }
}

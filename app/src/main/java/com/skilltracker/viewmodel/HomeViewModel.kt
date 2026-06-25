package com.skilltracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skilltracker.domain.model.Skill
import com.skilltracker.domain.model.SkillHistory
import com.skilltracker.domain.repository.SkillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SkillRepository
) : ViewModel() {

    val skills: StateFlow<List<Skill>> = repository.getAllSkills()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSkillHistory = MutableStateFlow<List<SkillHistory>>(emptyList())
    val selectedSkillHistory: StateFlow<List<SkillHistory>> = _selectedSkillHistory

    private val _trendSkillId = MutableStateFlow<String?>(null)
    val trendSkillId: StateFlow<String?> = _trendSkillId

    fun loadHistoryForSkill(skillId: String) {
        _trendSkillId.value = skillId
        viewModelScope.launch {
            repository.getHistoryForSkill(skillId).collect {
                _selectedSkillHistory.value = it
            }
        }
    }

    fun deleteSkill(id: String) {
        viewModelScope.launch {
            repository.deleteSkill(id)
        }
    }

    fun importSkills(skills: List<Skill>) {
        viewModelScope.launch {
            skills.forEach { skill ->
                repository.insertSkill(skill)
            }
        }
    }

    class Factory(private val repository: SkillRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}

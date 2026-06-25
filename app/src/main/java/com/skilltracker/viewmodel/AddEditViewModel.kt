package com.skilltracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skilltracker.domain.model.Skill
import com.skilltracker.domain.repository.SkillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditViewModel(
    private val repository: SkillRepository,
    private val skillId: String? = null
) : ViewModel() {

    private val _skillName = MutableStateFlow("")
    val skillName: StateFlow<String> = _skillName

    private val _skillValue = MutableStateFlow(50)
    val skillValue: StateFlow<Int> = _skillValue

    private val _skillColor = MutableStateFlow(0xFF4CAF50L)
    val skillColor: StateFlow<Long> = _skillColor

    private val _strengths = MutableStateFlow<List<String>>(emptyList())
    val strengths: StateFlow<List<String>> = _strengths

    private val _improvements = MutableStateFlow<List<String>>(emptyList())
    val improvements: StateFlow<List<String>> = _improvements

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    init {
        if (skillId != null) {
            viewModelScope.launch {
                repository.getSkillById(skillId).first()?.let { skill ->
                    _skillName.value = skill.name
                    _skillValue.value = skill.value
                    _skillColor.value = skill.color
                    _strengths.value = skill.strengths
                    _improvements.value = skill.improvements
                    _isEditing.value = true
                }
            }
        }
    }

    fun updateName(name: String) { _skillName.value = name }
    fun updateValue(value: Int) { _skillValue.value = value.coerceIn(0, 100) }
    fun updateColor(color: Long) { _skillColor.value = color }

    fun addStrength(strength: String) {
        _strengths.value = _strengths.value + strength
    }

    fun removeStrength(strength: String) {
        _strengths.value = _strengths.value - strength
    }

    fun addImprovement(improvement: String) {
        _improvements.value = _improvements.value + improvement
    }

    fun removeImprovement(improvement: String) {
        _improvements.value = _improvements.value - improvement
    }

    fun saveSkill(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val skill = Skill(
                id = skillId ?: UUID.randomUUID().toString(),
                name = _skillName.value,
                value = _skillValue.value,
                color = _skillColor.value,
                strengths = _strengths.value,
                improvements = _improvements.value
            )
            if (_isEditing.value) {
                repository.updateSkill(skill)
            } else {
                repository.insertSkill(skill)
            }
            onSuccess()
        }
    }

    class Factory(
        private val repository: SkillRepository,
        private val skillId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddEditViewModel(repository, skillId) as T
        }
    }
}

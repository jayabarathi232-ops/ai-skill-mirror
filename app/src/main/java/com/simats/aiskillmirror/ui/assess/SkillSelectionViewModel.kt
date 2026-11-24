package com.simats.aiskillmirror.ui.assess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.UserSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SkillSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SkillSelectionUiState())
    val uiState: StateFlow<SkillSelectionUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SkillSelectionUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 1. Fetch saved user subcategories from backend
            val subcatResult = ApiClient.fetchUserSubcategories(UserSession.userId)
            if (subcatResult.isSuccess && subcatResult.data != null) {
                val (active, list) = subcatResult.data
                val currSkill = _uiState.value.selectedSkillId.lowercase()
                val matched = list.filter { sub ->
                    val m = sub.mainSkill.lowercase()
                    when {
                        currSkill.contains("music") -> m.contains("music") || m in listOf("guitar", "singing", "piano", "violin", "drums", "flute", "veena")
                        currSkill.contains("photo") -> m.contains("photo")
                        currSkill.contains("cod") || currSkill.contains("prog") -> m.contains("cod") || m.contains("prog")
                        else -> m == currSkill
                    }
                }.let { subList ->
                    subList.firstOrNull { it.isActive }
                        ?: subList.maxByOrNull { it.lessonsCompleted * 100 + it.xpEarned }
                        ?: subList.firstOrNull()
                } ?: active

                _uiState.value = _uiState.value.copy(
                    activeSavedSubcategory = matched,
                    savedSubcategories = list
                )
            }

            // 2. Fetch skills list to update baseline scores
            val skillsResult = ApiClient.fetchSkills(UserSession.userId)
            if (skillsResult.isSuccess && skillsResult.data != null) {
                val scoreMap = skillsResult.data.associate { it.id to it.lastScore }
                val updatedSkills = _uiState.value.skills.map { cat ->
                    val s = scoreMap[cat.id] ?: cat.lastScore
                    cat.copy(lastScore = s)
                }
                _uiState.value = _uiState.value.copy(skills = updatedSkills)
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun selectCategory(categoryId: String) {
        val catId = categoryId.lowercase()
        // Find if user has a saved subcategory matching this category
        val matchedSub = _uiState.value.savedSubcategories.filter { sub ->
            val mSkill = sub.mainSkill.lowercase()
            when {
                catId.contains("music") -> mSkill.contains("music") || mSkill in listOf("guitar", "singing", "piano", "violin", "drums", "flute", "veena")
                catId.contains("photo") -> mSkill.contains("photo")
                catId.contains("cod") || catId.contains("prog") -> mSkill.contains("cod") || mSkill.contains("prog")
                else -> mSkill == catId
            }
        }.let { list ->
            list.firstOrNull { it.isActive }
                ?: list.maxByOrNull { it.lessonsCompleted * 100 + it.xpEarned }
                ?: list.firstOrNull()
        }

        val categoryObj = _uiState.value.skills.firstOrNull { it.id.equals(categoryId, ignoreCase = true) }
        val fallback = if (matchedSub == null && categoryObj != null && categoryObj.subcategories.isNotEmpty()) {
            val firstSub = categoryObj.subcategories.first()
            com.simats.aiskillmirror.data.network.SubcategoryItemData(
                id = 0,
                mainSkill = categoryObj.id,
                subcategoryId = firstSub.id,
                subcategoryName = firstSub.name,
                proficiencyLevel = "Beginner",
                learningGoal = "Master Core Fundamentals",
                isActive = true,
                xpEarned = 0,
                lessonsCompleted = 0,
                practiceMinutes = 0
            )
        } else null

        _uiState.value = _uiState.value.copy(
            selectedSkillId = categoryId,
            selectedSubcategory = null,
            activeSavedSubcategory = matchedSub ?: fallback ?: _uiState.value.activeSavedSubcategory,
            isSubcategoryDialogOpen = false
        )
    }

    fun openSubcategoryOnboarding(subcategory: SkillSubcategory) {
        _uiState.value = _uiState.value.copy(
            selectedSubcategory = subcategory,
            selectedSkillId = subcategory.mainSkillId,
            selectedProficiencyLevel = "Beginner",
            selectedLearningGoal = "Master Core Fundamentals",
            isSubcategoryDialogOpen = true
        )
    }

    fun closeSubcategoryOnboarding() {
        _uiState.value = _uiState.value.copy(
            isSubcategoryDialogOpen = false,
            selectedSubcategory = null
        )
    }

    fun selectLevel(level: String) {
        _uiState.value = _uiState.value.copy(selectedProficiencyLevel = level)
    }

    fun selectGoal(goal: String) {
        _uiState.value = _uiState.value.copy(selectedLearningGoal = goal)
    }

    fun startLearning() {
        val sub = _uiState.value.selectedSubcategory ?: return
        val level = _uiState.value.selectedProficiencyLevel
        val goal = _uiState.value.selectedLearningGoal

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Save subcategory to database
            val result = ApiClient.saveUserSubcategory(
                userId = UserSession.userId,
                mainSkill = sub.mainSkillId,
                subcategoryId = sub.id,
                subcategoryName = sub.name,
                proficiencyLevel = level,
                learningGoal = goal
            )

            if (result.isSuccess && result.data != null) {
                _uiState.value = _uiState.value.copy(
                    activeSavedSubcategory = result.data,
                    isSubcategoryDialogOpen = false,
                    isLoading = false,
                    statusMessage = "Activated ${sub.name} training path!"
                )
                // Emit event to navigate to Daily Training loaded with this subcategory
                _eventChannel.send(
                    SkillSelectionUiEvent.StartTrainingWithSubcategory(
                        mainSkill = sub.mainSkillId,
                        subcategory = sub,
                        level = level,
                        goal = goal
                    )
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = result.message ?: "Failed to save selection"
                )
                // Still allow navigation locally
                _eventChannel.send(
                    SkillSelectionUiEvent.StartTrainingWithSubcategory(
                        mainSkill = sub.mainSkillId,
                        subcategory = sub,
                        level = level,
                        goal = goal
                    )
                )
            }
        }
    }

    fun onStartAssessmentClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillSelectionUiEvent.StartAssessment)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillSelectionUiEvent.NavigateBack)
        }
    }
}

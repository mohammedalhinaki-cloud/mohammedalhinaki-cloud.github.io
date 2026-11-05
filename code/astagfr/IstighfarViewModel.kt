package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.IstighfarMode
import com.example.data.IstighfarPreferences
import com.example.data.IstighfarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IstighfarViewModel(application: Application) : AndroidViewModel(application) {
  private val preferences = IstighfarPreferences(application)

  private val _uiState = MutableStateFlow(preferences.loadState())
  val uiState: StateFlow<IstighfarState> = _uiState.asStateFlow()

  val showResetConfirmation = MutableStateFlow(false)
  val showGoalReachedDialog = MutableStateFlow(false)
  val showCustomGoalDialog = MutableStateFlow(false)

  // Visual pulse trigger for subtle, smooth scale feedback on tap
  val tapAnimationTrigger = MutableStateFlow(0L)

  fun onTapCounter() {
    tapAnimationTrigger.value = System.currentTimeMillis()

    when (_uiState.value.mode) {
      IstighfarMode.GENERAL -> {
        val newCount = _uiState.value.generalCount + 1
        _uiState.update { it.copy(generalCount = newCount) }
        preferences.saveGeneralCount(newCount)
      }

      IstighfarMode.TARGET -> {
        val current = _uiState.value.targetCount
        val goal = _uiState.value.targetGoal
        val newCount = current + 1
        val reached = newCount >= goal

        _uiState.update {
          it.copy(
            targetCount = newCount,
            isGoalReached = reached
          )
        }
        preferences.saveTargetCount(newCount)

        // If this tap reached the target for the first time
        if (newCount == goal) {
          showGoalReachedDialog.value = true
        }
      }
    }
  }

  fun setMode(mode: IstighfarMode) {
    if (_uiState.value.mode != mode) {
      _uiState.update { it.copy(mode = mode) }
      preferences.saveMode(mode)
    }
  }

  fun setTargetGoal(goal: Int) {
    if (goal > 0) {
      val reached = _uiState.value.targetCount >= goal
      _uiState.update {
        it.copy(
          targetGoal = goal,
          isGoalReached = reached
        )
      }
      preferences.saveTargetGoal(goal)
    }
  }

  fun confirmReset() {
    when (_uiState.value.mode) {
      IstighfarMode.GENERAL -> {
        _uiState.update { it.copy(generalCount = 0L) }
        preferences.saveGeneralCount(0L)
      }
      IstighfarMode.TARGET -> {
        _uiState.update {
          it.copy(
            targetCount = 0,
            isGoalReached = false
          )
        }
        preferences.saveTargetCount(0)
      }
    }
    showResetConfirmation.value = false
  }

  fun startNewTargetRound() {
    _uiState.update {
      it.copy(
        targetCount = 0,
        isGoalReached = false
      )
    }
    preferences.saveTargetCount(0)
    showGoalReachedDialog.value = false
  }

  fun dismissGoalDialog() {
    showGoalReachedDialog.value = false
  }

  fun toggleTheme() {
    val nextTheme = when (_uiState.value.darkThemeSetting) {
      "light" -> "dark"
      "dark" -> "system"
      else -> "dark"
    }
    _uiState.update { it.copy(darkThemeSetting = nextTheme) }
    preferences.saveTheme(nextTheme)
  }
}

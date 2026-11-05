package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class IstighfarMode {
  GENERAL,
  TARGET
}

data class IstighfarState(
  val mode: IstighfarMode = IstighfarMode.GENERAL,
  val generalCount: Long = 0L,
  val targetCount: Int = 0,
  val targetGoal: Int = 100,
  val isGoalReached: Boolean = false,
  val darkThemeSetting: String = "system" // "system", "light", "dark"
)

class IstighfarPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("istighfar_prefs", Context.MODE_PRIVATE)

  companion object {
    private const val KEY_MODE = "key_mode"
    private const val KEY_GENERAL_COUNT = "key_general_count"
    private const val KEY_TARGET_COUNT = "key_target_count"
    private const val KEY_TARGET_GOAL = "key_target_goal"
    private const val KEY_THEME = "key_theme"
  }

  fun loadState(): IstighfarState {
    val modeString = prefs.getString(KEY_MODE, IstighfarMode.GENERAL.name) ?: IstighfarMode.GENERAL.name
    val mode = try {
      IstighfarMode.valueOf(modeString)
    } catch (_: Exception) {
      IstighfarMode.GENERAL
    }
    val generalCount = prefs.getLong(KEY_GENERAL_COUNT, 0L)
    val targetCount = prefs.getInt(KEY_TARGET_COUNT, 0)
    val targetGoal = prefs.getInt(KEY_TARGET_GOAL, 100).coerceAtLeast(1)
    val theme = prefs.getString(KEY_THEME, "system") ?: "system"

    return IstighfarState(
      mode = mode,
      generalCount = generalCount,
      targetCount = targetCount,
      targetGoal = targetGoal,
      isGoalReached = targetCount >= targetGoal,
      darkThemeSetting = theme
    )
  }

  fun saveMode(mode: IstighfarMode) {
    prefs.edit().putString(KEY_MODE, mode.name).apply()
  }

  fun saveGeneralCount(count: Long) {
    prefs.edit().putLong(KEY_GENERAL_COUNT, count).apply()
  }

  fun saveTargetCount(count: Int) {
    prefs.edit().putInt(KEY_TARGET_COUNT, count).apply()
  }

  fun saveTargetGoal(goal: Int) {
    prefs.edit().putInt(KEY_TARGET_GOAL, goal).apply()
  }

  fun saveTheme(theme: String) {
    prefs.edit().putString(KEY_THEME, theme).apply()
  }
}

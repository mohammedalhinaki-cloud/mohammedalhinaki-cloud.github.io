package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.IstighfarScreen
import com.example.ui.IstighfarViewModel
import com.example.ui.theme.IstighfarTheme

class MainActivity : ComponentActivity() {
  private val viewModel: IstighfarViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val state by viewModel.uiState.collectAsState()
      val systemDark = isSystemInDarkTheme()
      val isDarkTheme = when (state.darkThemeSetting) {
        "dark" -> true
        "light" -> false
        else -> systemDark
      }

      IstighfarTheme(darkTheme = isDarkTheme) {
        IstighfarScreen(
          viewModel = viewModel,
          isDarkTheme = isDarkTheme
        )
      }
    }
  }
}

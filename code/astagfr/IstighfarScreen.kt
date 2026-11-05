package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IstighfarMode
import com.example.ui.components.CustomGoalDialog
import com.example.ui.components.GoalReachedDialog
import com.example.ui.components.ResetConfirmDialog
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.TajawalFontFamily
import java.text.NumberFormat
import java.util.Locale

@Composable
fun IstighfarScreen(
  viewModel: IstighfarViewModel,
  isDarkTheme: Boolean,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsState()
  val showResetDialog by viewModel.showResetConfirmation.collectAsState()
  val showGoalDialog by viewModel.showGoalReachedDialog.collectAsState()
  val showCustomGoalDialog by viewModel.showCustomGoalDialog.collectAsState()
  val tapTimestamp by viewModel.tapAnimationTrigger.collectAsState()

  // Smooth visual scale animation on each tap
  val scaleAnim = remember { Animatable(1f) }
  LaunchedEffect(tapTimestamp) {
    if (tapTimestamp > 0L) {
      scaleAnim.animateTo(0.94f, animationSpec = tween(durationMillis = 50))
      scaleAnim.animateTo(1f, animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
      ))
    }
  }

  val arabicFormatter = remember { NumberFormat.getInstance(Locale("ar")) }

  val targetProgress by animateFloatAsState(
    targetValue = if (state.mode == IstighfarMode.TARGET && state.targetGoal > 0) {
      (state.targetCount.toFloat() / state.targetGoal.toFloat()).coerceIn(0f, 1f)
    } else {
      0f
    },
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "target_progress"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Top Bar: Header with Mode Segmented Control, Theme Toggle & Reset Button
      TopActionHeader(
        currentMode = state.mode,
        onModeSelected = { viewModel.setMode(it) },
        isDarkTheme = isDarkTheme,
        onToggleTheme = { viewModel.toggleTheme() },
        onResetClicked = { viewModel.showResetConfirmation.value = true }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Main Tap Zone (HUGE clickable area for single-handed use)
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .clip(RoundedCornerShape(32.dp))
          .background(
            brush = Brush.verticalGradient(
              colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
              )
            )
          )
          .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            shape = RoundedCornerShape(32.dp)
          )
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null, // Custom visual feedback without harsh ripples
            onClick = { viewModel.onTapCounter() }
          )
          .testTag("main_tap_area"),
        contentAlignment = Alignment.Center
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          // Top Phrase: Astaghfirullah
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
          ) {
            Text(
              text = "أَسْتَغْفِرُ اللَّهَ",
              fontFamily = AmiriFontFamily,
              fontSize = 38.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "وَأَتُوبُ إِلَيْهِ",
              fontFamily = AmiriFontFamily,
              fontSize = 24.sp,
              color = MaterialTheme.colorScheme.tertiary,
              textAlign = TextAlign.Center
            )
          }

          // Center: Large Animated Counter Display
          Box(
            modifier = Modifier
              .scale(scaleAnim.value)
              .size(240.dp),
            contentAlignment = Alignment.Center
          ) {
            // Target progress circle (when in TARGET mode)
            if (state.mode == IstighfarMode.TARGET) {
              CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
              )
              CircularProgressIndicator(
                progress = { targetProgress },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
              )
            } else {
              // Subtle decorative ambient circle in General Mode
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .clip(CircleShape)
                  .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    shape = CircleShape
                  )
              )
            }

            // Central Counter Number
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              val currentCount = if (state.mode == IstighfarMode.GENERAL) {
                state.generalCount
              } else {
                state.targetCount.toLong()
              }

              AnimatedContent(
                targetState = currentCount,
                transitionSpec = {
                  fadeIn(animationSpec = tween(120)) togetherWith
                      fadeOut(animationSpec = tween(80))
                },
                label = "count_anim"
              ) { countVal ->
                Text(
                  text = arabicFormatter.format(countVal),
                  fontFamily = TajawalFontFamily,
                  fontSize = if (countVal >= 10000) 56.sp else 68.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  textAlign = TextAlign.Center
                )
              }

              Text(
                text = "استغفار",
                fontFamily = TajawalFontFamily,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Bottom instruction hint inside the tap card
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 8.dp)
          ) {
            if (state.mode == IstighfarMode.TARGET) {
              val remaining = (state.targetGoal - state.targetCount).coerceAtLeast(0)
              Text(
                text = if (remaining > 0) {
                  "المتبقي للهدف: ${arabicFormatter.format(remaining)} من ${arabicFormatter.format(state.targetGoal)}"
                } else {
                  "اكتمل الهدف بنجاح (${arabicFormatter.format(state.targetGoal)})"
                },
                fontFamily = TajawalFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (remaining == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            } else {
              Text(
                text = "المس أي مكان على الشاشة لزيادة العداد",
                fontFamily = TajawalFontFamily,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            }
          }
        }
      }

      // 3. Target Mode Preset Selectors (Shown when in TARGET mode)
      AnimatedVisibility(
        visible = state.mode == IstighfarMode.TARGET,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        TargetPresetsBar(
          currentTargetGoal = state.targetGoal,
          onSelectGoal = { viewModel.setTargetGoal(it) },
          onOpenCustomDialog = { viewModel.showCustomGoalDialog.value = true }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))
    }
  }

  // Dialogs
  if (showResetDialog) {
    ResetConfirmDialog(
      onConfirm = { viewModel.confirmReset() },
      onDismiss = { viewModel.showResetConfirmation.value = false }
    )
  }

  if (showGoalDialog) {
    GoalReachedDialog(
      targetGoal = state.targetGoal,
      onStartNewRound = { viewModel.startNewTargetRound() },
      onDismiss = { viewModel.dismissGoalDialog() }
    )
  }

  if (showCustomGoalDialog) {
    CustomGoalDialog(
      currentGoal = state.targetGoal,
      onGoalSelected = {
        viewModel.setTargetGoal(it)
        viewModel.showCustomGoalDialog.value = false
      },
      onDismiss = { viewModel.showCustomGoalDialog.value = false }
    )
  }
}

@Composable
private fun TopActionHeader(
  currentMode: IstighfarMode,
  onModeSelected: (IstighfarMode) -> Unit,
  isDarkTheme: Boolean,
  onToggleTheme: () -> Unit,
  onResetClicked: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Reset Button
    IconButton(
      onClick = onResetClicked,
      modifier = Modifier
        .size(44.dp)
        .testTag("reset_button")
    ) {
      Icon(
        imageVector = Icons.Outlined.RestartAlt,
        contentDescription = "تصفير العداد",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Centered Mode Pills
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ModeChip(
        text = "الوضع العام",
        selected = currentMode == IstighfarMode.GENERAL,
        onClick = { onModeSelected(IstighfarMode.GENERAL) },
        tag = "mode_general"
      )

      ModeChip(
        text = "الوضع المخصص",
        selected = currentMode == IstighfarMode.TARGET,
        onClick = { onModeSelected(IstighfarMode.TARGET) },
        tag = "mode_target"
      )
    }

    // Theme Toggle Button
    IconButton(
      onClick = onToggleTheme,
      modifier = Modifier
        .size(44.dp)
        .testTag("theme_toggle_button")
    ) {
      Icon(
        imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
        contentDescription = "تبديل المظهر",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun ModeChip(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  val bgColor by animateColorAsState(
    targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
    label = "chip_bg"
  )
  val textColor by animateColorAsState(
    targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
    label = "chip_text"
  )

  Surface(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag(tag),
    color = bgColor,
    shape = RoundedCornerShape(16.dp)
  ) {
    Text(
      text = text,
      fontFamily = TajawalFontFamily,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
      fontSize = 14.sp,
      color = textColor,
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
    )
  }
}

@Composable
private fun TargetPresetsBar(
  currentTargetGoal: Int,
  onSelectGoal: (Int) -> Unit,
  onOpenCustomDialog: () -> Unit
) {
  val presets = listOf(33, 100, 500, 1000)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    presets.forEach { preset ->
      val isSelected = currentTargetGoal == preset
      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { onSelectGoal(preset) }
          .testTag("preset_$preset"),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = "$preset",
          fontFamily = TajawalFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp,
          color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
      }
    }

    // Custom Goal Button
    val isCustom = !presets.contains(currentTargetGoal)
    Surface(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable { onOpenCustomDialog() }
        .testTag("preset_custom"),
      color = if (isCustom) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
      border = if (isCustom) null else androidx.compose.foundation.BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
      ),
      shape = RoundedCornerShape(12.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Rounded.Edit,
          contentDescription = null,
          modifier = Modifier.size(14.dp),
          tint = if (isCustom) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (isCustom) "$currentTargetGoal" else "مخصص",
          fontFamily = TajawalFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp,
          color = if (isCustom) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}

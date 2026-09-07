package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.ThreeDLogo
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  currentLanguage: AppLanguage,
  reducedMotion: Boolean,
  onSplashFinished: () -> Unit
) {
  var stage by remember { mutableIntStateOf(0) }

  val logoAlpha by animateFloatAsState(
    targetValue = if (stage >= 1) 1f else 0f,
    animationSpec = tween(700, easing = FastOutSlowInEasing),
    label = "logoAlpha"
  )
  val titleAlpha by animateFloatAsState(
    targetValue = if (stage >= 2) 1f else 0f,
    animationSpec = tween(700, easing = FastOutSlowInEasing),
    label = "titleAlpha"
  )
  val taglineAlpha by animateFloatAsState(
    targetValue = if (stage >= 3) 1f else 0f,
    animationSpec = tween(700, easing = FastOutSlowInEasing),
    label = "taglineAlpha"
  )

  LaunchedEffect(Unit) {
    stage = 1
    delay(500)
    stage = 2
    delay(600)
    stage = 3
    delay(1100)
    onSplashFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(ObsidianBg, DarkSurface, Color(0xFF141318))
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // 3D Rotating Crest with Lighting
      Box(
        modifier = Modifier
          .alpha(logoAlpha)
          .padding(bottom = 24.dp)
      ) {
        ThreeDLogo(
          size = 140.dp,
          reducedMotion = reducedMotion,
          isInteractive = false
        )
      }

      // Odia Brand Title
      Text(
        text = "ଜାଗୃତି",
        color = LavenderPrimary,
        fontSize = 42.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp,
        modifier = Modifier.alpha(titleAlpha)
      )

      Text(
        text = "JAGRUTIPATRIKA",
        color = OnVioletContainer,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 4.sp,
        modifier = Modifier
          .alpha(titleAlpha)
          .padding(top = 4.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Tagline
      Text(
        text = "“ସଚେତନ ସମାଜର ସ୍ୱର”",
        color = GoldLight,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.alpha(taglineAlpha)
      )

      Text(
        text = "Digital Magazine • Literature • Social Publishing",
        color = TextDarkMuted,
        fontSize = 12.sp,
        modifier = Modifier
          .alpha(taglineAlpha)
          .padding(top = 6.dp)
      )
    }

    // Bottom decorative Odisha motif caption
    Text(
      text = "ଓଡ଼ିଶାର ସାଂସ୍କୃତିକ ଓ ସାହିତ୍ୟିକ ଡିଜିଟାଲ୍ ମଞ୍ଚ",
      color = TextDarkMuted.copy(alpha = 0.7f),
      fontSize = 11.sp,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 32.dp)
        .alpha(taglineAlpha)
    )
  }
}

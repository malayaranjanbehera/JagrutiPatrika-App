package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun CreateSpeedDialDialog(
  currentLanguage: AppLanguage,
  onDismiss: () -> Unit,
  onSubmitWork: () -> Unit,
  onTextToImage: () -> Unit,
  onAddStory: () -> Unit,
  onCompetitionEntry: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("create_speed_dial_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Text(
          text = if (currentLanguage == AppLanguage.ODIA) "ନୂତନ ସୃଷ୍ଟି (Create New)" else "Create New",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = CrimsonPrimary
        )
        Text(
          text = if (currentLanguage == AppLanguage.ODIA) "ଜାଗୃତି ମଞ୍ଚରେ ଆପଣଙ୍କ ପ୍ରତିଭା ପ୍ରକାଶ କରନ୍ତୁ" else "Express your creative voice on Jagrutipatrika",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Actions grid / column
        SpeedDialActionItem(
          icon = Icons.Default.EditNote,
          title = if (currentLanguage == AppLanguage.ODIA) "ଲେଖା ଦାଖଲ (Submit Work)" else "Submit Work",
          subtitle = if (currentLanguage == AppLanguage.ODIA) "କବିତା, ଗଳ୍ପ, ପ୍ରବନ୍ଧ ସମ୍ପାଦକୀୟ ସମୀକ୍ଷା ପାଇଁ" else "Poems, stories, essays for editorial review",
          color = CrimsonPrimary,
          onClick = {
            onDismiss()
            onSubmitWork()
          }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SpeedDialActionItem(
          icon = Icons.Default.Image,
          title = if (currentLanguage == AppLanguage.ODIA) "ଟେକ୍ସଟ୍ ଟୁ ଇମେଜ୍ (Text to Image)" else "Text to Image Creator",
          subtitle = if (currentLanguage == AppLanguage.ODIA) "କବିତା ବା ଉକ୍ତିର ବ୍ରାଣ୍ଡେଡ୍ ଫଟୋ ପ୍ରସ୍ତୁତ କରନ୍ତୁ" else "Create beautiful branded visual quotes/verses",
          color = GoldPrimary,
          onClick = {
            onDismiss()
            onTextToImage()
          }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SpeedDialActionItem(
          icon = Icons.Default.CameraAlt,
          title = if (currentLanguage == AppLanguage.ODIA) "ଷ୍ଟୋରି ଯୋଡ଼ନ୍ତୁ (Add Story)" else "Add 24h Story",
          subtitle = if (currentLanguage == AppLanguage.ODIA) "ସାମୟିକ ଗଳ୍ପ ବା ଉଦ୍ଧୃତି ପୋଷ୍ଟ କରନ୍ତୁ" else "Share quick verse or photo story for 24 hours",
          color = LotusCoral,
          onClick = {
            onDismiss()
            onAddStory()
          }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SpeedDialActionItem(
          icon = Icons.Default.EmojiEvents,
          title = if (currentLanguage == AppLanguage.ODIA) "ପ୍ରତିଯୋଗିତା ଏଣ୍ଟ୍ରି (Contest Entry)" else "Competition Entry",
          subtitle = if (currentLanguage == AppLanguage.ODIA) "ସାହିତ୍ୟ ପ୍ରତିଯୋଗିତାରେ ଭାଗ ନିଅନ୍ତୁ" else "Submit your entry to active literary contests",
          color = Color(0xFF2E7D32),
          onClick = {
            onDismiss()
            onCompetitionEntry()
          }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onDismiss) {
          Text(
            text = if (currentLanguage == AppLanguage.ODIA) "ବନ୍ଦ କରନ୍ତୁ" else "Cancel",
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun SpeedDialActionItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  color: Color,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(color.copy(alpha = 0.08f))
      .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(color),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(22.dp))
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 15.sp
      )
    }

    Icon(
      imageVector = Icons.Default.ChevronRight,
      contentDescription = null,
      tint = color,
      modifier = Modifier.size(20.dp)
    )
  }
}

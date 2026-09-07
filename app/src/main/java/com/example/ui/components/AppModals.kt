package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppStrings
import com.example.model.AppLanguage
import com.example.model.NotificationItem
import com.example.ui.theme.*

@Composable
fun NotificationsDialog(
  notifications: List<NotificationItem>,
  currentLanguage: AppLanguage,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("notifications_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = AppStrings.notificationsTitle(currentLanguage),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = LavenderPrimary
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkMuted)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notifications.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(100.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (currentLanguage == AppLanguage.ODIA) "କୌଣସି ବିଜ୍ଞପ୍ତି ନାହିଁ।" else "No notifications.",
              color = TextDarkMuted,
              fontSize = 13.sp
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(max = 320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(notifications, key = { it.id }) { notif ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (notif.isRead) DarkSurfaceElevated else DeepVioletContainer)
                  .padding(10.dp),
                verticalAlignment = Alignment.Top
              ) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(LavenderPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (notif.type) {
                      "LIKE" -> Icons.Default.Favorite
                      "COMMENT" -> Icons.Default.ChatBubble
                      "EDITORIAL" -> Icons.Default.Verified
                      "COMPETITION" -> Icons.Default.EmojiEvents
                      else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = OnLavenderPrimary,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = notif.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextDarkHighContrast
                  )
                  Text(
                    text = notif.body,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = TextDarkMuted
                  )
                  Text(
                    text = notif.timestamp,
                    fontSize = 10.sp,
                    color = GoldPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun SettingsDialog(
  currentLanguage: AppLanguage,
  reducedMotion: Boolean,
  onLanguageChange: (AppLanguage) -> Unit,
  onReducedMotionToggle: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("settings_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = AppStrings.settingsTitle(currentLanguage),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = LavenderPrimary
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkMuted)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language selection
        Text(
          text = if (currentLanguage == AppLanguage.ODIA) "ଭାଷା ବାଛନ୍ତୁ (Language Preference):" else "Language Preference:",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextDarkHighContrast
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = currentLanguage == AppLanguage.ODIA,
            onClick = { onLanguageChange(AppLanguage.ODIA) },
            label = { Text("ଓଡ଼ିଆ (Odia)") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = LavenderPrimary,
              selectedLabelColor = OnLavenderPrimary
            )
          )
          FilterChip(
            selected = currentLanguage == AppLanguage.ENGLISH,
            onClick = { onLanguageChange(AppLanguage.ENGLISH) },
            label = { Text("English") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = LavenderPrimary,
              selectedLabelColor = OnLavenderPrimary
            )
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reduced Motion Toggle
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onReducedMotionToggle() },
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (currentLanguage == AppLanguage.ODIA) "୩ଡି ଗତି ସୀମିତ କରନ୍ତୁ (Reduce 3D Motion)" else "Reduce 3D Motion",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = TextDarkHighContrast
            )
            Text(
              text = if (currentLanguage == AppLanguage.ODIA) "ବ୍ୟାଟେରୀ ବଞ୍ଚାଇବା ପାଇଁ ୩ଡି ଆନିମେସନ୍ ବନ୍ଦ କରନ୍ତୁ" else "Disable continuous 3D rotation for accessibility & battery saving",
              fontSize = 10.sp,
              color = TextDarkMuted
            )
          }
          Switch(
            checked = reducedMotion,
            onCheckedChange = { onReducedMotionToggle() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = LavenderPrimary,
              checkedTrackColor = DeepVioletContainer
            )
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About Jagrutipatrika
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "ଜାଗୃତି ପତ୍ରିକା (Jagrutipatrika)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = LavenderPrimary
            )
            Text(
              text = "“ସଚେତନ ସମାଜର ସ୍ୱର” • Version 2.0.0 (2026)\nAn Odia digital magazine & literature publishing platform.",
              fontSize = 11.sp,
              color = TextDarkMuted,
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  }
}

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppStrings
import com.example.model.AppLanguage
import com.example.model.Competition
import com.example.ui.components.ThreeDLogo
import com.example.ui.theme.*

@Composable
fun CompetitionsScreen(
  competitions: List<Competition>,
  activeCompetition: Competition?,
  currentLanguage: AppLanguage,
  onOpenCompetition: (Competition) -> Unit,
  onCloseCompetition: () -> Unit,
  onSubmitEntry: (String, String, String) -> Unit
) {
  val context = LocalContext.current
  var showCertificateDialog by remember { mutableStateOf(false) }

  // Entry submission form state
  var entryTitle by remember { mutableStateOf("") }
  var entryContent by remember { mutableStateOf("") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .padding(horizontal = 16.dp)
      .testTag("competitions_screen")
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = AppStrings.competitionsTitle(currentLanguage),
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LavenderPrimary
          )
          Text(
            text = if (currentLanguage == AppLanguage.ODIA) "ସାହିତ୍ୟିକ ପ୍ରତିଭା ଅନ୍ୱେଷଣ ଓ ପୁରସ୍କାର" else "Discover literary talent & win honors",
            fontSize = 12.sp,
            color = TextDarkMuted
          )
        }

        IconButton(
          onClick = { showCertificateDialog = true },
          modifier = Modifier
            .clip(CircleShape)
            .background(DeepVioletContainer)
        ) {
          Icon(Icons.Default.CardGiftcard, contentDescription = "Certificates", tint = OnVioletContainer)
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Active Competitions List
    items(competitions, key = { it.id }) { comp ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Status and Category
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1B3828))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "● ସକ୍ରିୟ (ACTIVE)",
                color = Color(0xFF81C784),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "${comp.entriesCount} ପ୍ରତିଯୋଗୀ ଅଂଶଗ୍ରହଣ କରିଛନ୍ତି",
              fontSize = 11.sp,
              color = TextDarkMuted
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Title
          Text(
            text = comp.titleOr,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextDarkHighContrast
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Prize Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(DarkSurfaceElevated)
              .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = AppStrings.prize(currentLanguage),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldPrimary
                )
                Text(
                  text = comp.prizeOr,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextDarkHighContrast
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Deadline
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${AppStrings.deadline(currentLanguage)}: ${comp.deadline}",
              fontSize = 12.sp,
              color = TextDarkMuted
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Button to register / participate
          Button(
            onClick = { onOpenCompetition(comp) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = OnLavenderPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(AppStrings.registerEntry(currentLanguage), fontWeight = FontWeight.Bold, color = OnLavenderPrimary)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(88.dp))
    }
  }

  // Competition Entry Submission Dialog
  activeCompetition?.let { comp ->
    AlertDialog(
      onDismissRequest = onCloseCompetition,
      title = { Text(comp.titleOr, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            text = "ନିୟମାବଳୀ:\n${comp.rulesOr}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = entryTitle,
            onValueChange = { entryTitle = it },
            label = { Text("ଏଣ୍ଟ୍ରିର ଶୀର୍ଷକ (Entry Title)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = entryContent,
            onValueChange = { entryContent = it },
            label = { Text("ଆପଣଙ୍କ ସୃଷ୍ଟି (Content / Verses)") },
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (entryTitle.isNotBlank() && entryContent.isNotBlank()) {
              onSubmitEntry(comp.id, entryTitle, entryContent)
              entryTitle = ""
              entryContent = ""
              Toast.makeText(context, "ଏଣ୍ଟ୍ରି ସଫଳତାର ସହ ଗୃହୀତ ହେଲା! (Entry submitted)", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
        ) {
          Text("ଦାଖଲ କରନ୍ତୁ (Submit)")
        }
      },
      dismissButton = {
        TextButton(onClick = onCloseCompetition) {
          Text(AppStrings.cancel(currentLanguage))
        }
      }
    )
  }

  // Certificate Sample Dialog
  if (showCertificateDialog) {
    Dialog(onDismissRequest = { showCertificateDialog = false }) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF9))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(3.dp, GoldPrimary, RoundedCornerShape(16.dp))
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          ThreeDLogo(size = 64.dp, isInteractive = false)
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "ଜାଗୃତି ପତ୍ରିକା ଇ-ପ୍ରମାଣପତ୍ର",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = CrimsonPrimary
          )
          Text(
            text = "CERTIFICATE OF EXCELLENCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = GoldDark
          )
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "ପ୍ରମାଣିତ କରାଯାଉଛି ଯେ ଆପଣ ରାଜ୍ୟସ୍ତରୀୟ ଓଡ଼ିଆ କବିତା ପ୍ରତିଯୋଗିତାରେ ଉଚ୍ଚକୋଟୀର ମୌଳିକ ସାହିତ୍ୟ ସୃଷ୍ଟି ପ୍ରଦାନ କରି ପ୍ରଶଂସିତ ହୋଇଛନ୍ତି।",
            fontSize = 12.sp,
            color = TextDarkSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = {
              showCertificateDialog = false
              Toast.makeText(context, "ସାର୍ଟିଫିକେଟ୍ ସାଇତାଗଲା (Certificate saved)", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
          ) {
            Icon(Icons.Outlined.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("ଡାଉନଲୋଡ୍ କରନ୍ତୁ")
          }
        }
      }
    }
  }
}

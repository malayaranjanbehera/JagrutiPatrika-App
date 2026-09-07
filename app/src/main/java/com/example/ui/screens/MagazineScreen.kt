package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppStrings
import com.example.model.*
import com.example.ui.components.ThreeDMagazineCard
import com.example.ui.theme.*

@Composable
fun MagazineScreen(
  issues: List<MagazineIssue>,
  activeIssue: MagazineIssue?,
  activeArticle: MagazineArticle?,
  currentLanguage: AppLanguage,
  onOpenIssue: (MagazineIssue) -> Unit,
  onCloseIssue: () -> Unit,
  onOpenArticle: (MagazineArticle) -> Unit,
  onCloseArticle: () -> Unit
) {
  val context = LocalContext.current
  val current = issues.firstOrNull() ?: return

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .padding(horizontal = 16.dp)
      .testTag("magazine_screen")
  ) {
    // Top Section Header
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = AppStrings.digitalMagazine(currentLanguage),
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LavenderPrimary
          )
          Text(
            text = if (currentLanguage == AppLanguage.ODIA) "ତ୍ରୈମାସିକ ଡିଜିଟାଲ୍ ସାହିତ୍ୟ ପତ୍ରିକା" else "Quarterly Digital Literary Journal",
            fontSize = 12.sp,
            color = TextDarkMuted
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DeepVioletContainer)
            .border(1.dp, LavenderPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = "Vol. 5 (2026)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = OnVioletContainer
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // 3D Magazine Cover Showpiece
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        ThreeDMagazineCard(
          issue = current,
          onClick = { onOpenIssue(current) }
        )
      }
      Text(
        text = if (currentLanguage == AppLanguage.ODIA) "👆 ୩ଡି ପତ୍ରିକା ଉପରେ ଟ୍ୟାପ୍ କରନ୍ତୁ ବା ତଳେ ଥିବା ଲେଖାଗୁଡ଼ିକ ପଢ଼ନ୍ତୁ" else "👆 Tap 3D Cover to interact or read articles below",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Current Issue Info Card & Download Button
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = current.titleOr,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = current.descriptionOr,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = { onOpenIssue(current) },
              colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(AppStrings.readArticles(currentLanguage), fontSize = 13.sp)
            }

            OutlinedButton(
              onClick = {
                Toast.makeText(context, "ଇ-ପତ୍ରିକା ଡାଉନଲୋଡ୍ ଆରମ୍ଭ ହେଲା (${current.downloadSizeMb})", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Outlined.Download, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(current.downloadSizeMb, fontSize = 12.sp)
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(24.dp))
    }

    // Featured Articles of this issue
    item {
      Text(
        text = if (currentLanguage == AppLanguage.ODIA) "ଚଳିତ ସଂଖ୍ୟାର ବିଶେଷ ଲେଖାଗୁଡ଼ିକ" else "Featured Articles in this Issue",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = CrimsonPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(current.articles, key = { it.id }) { article ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { onOpenArticle(article) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = article.titleOr,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = TextDarkHighContrast
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "— ${article.author} • ${article.category}",
              fontSize = 12.sp,
              color = LavenderPrimary,
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = article.excerpt,
              fontSize = 12.sp,
              maxLines = 2,
              color = TextDarkMuted
            )
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LavenderPrimary)
        }
      }
    }

    // Previous Issues Archive
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Text(
        text = AppStrings.pastIssues(currentLanguage),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = LavenderPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(issues.drop(1), key = { it.id }) { issue ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .clickable { onOpenIssue(issue) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(DeepVioletContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = OnVioletContainer)
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = issue.titleOr, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDarkHighContrast)
            Text(text = issue.monthYear, fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.SemiBold)
            Text(text = issue.descriptionOr, fontSize = 11.sp, maxLines = 1, color = TextDarkMuted)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(88.dp))
    }
  }

  // Article Reader Dialog (Clean reader mode with typography controls)
  activeArticle?.let { article ->
    var readerFontSize by remember { mutableFloatStateOf(16f) }

    Dialog(
      onDismissRequest = onCloseArticle,
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = ObsidianBg
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
        ) {
          // Reader Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = onCloseArticle) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LavenderPrimary)
            }
            Text(
              text = "ପାଠକ ମୋଡ୍ (Reader Mode)",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = LavenderPrimary
            )
            Row {
              IconButton(onClick = { readerFontSize = (readerFontSize - 2f).coerceAtLeast(12f) }) {
                Text("A-", fontWeight = FontWeight.Bold, color = LavenderPrimary)
              }
              IconButton(onClick = { readerFontSize = (readerFontSize + 2f).coerceAtMost(28f) }) {
                Text("A+", fontWeight = FontWeight.Bold, color = LavenderPrimary)
              }
            }
          }

          Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

          LazyColumn(modifier = Modifier.weight(1f)) {
            item {
              Text(
                text = article.titleOr,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LavenderPrimary,
                lineHeight = 30.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "— ${article.author} • ${article.category}",
                fontSize = 14.sp,
                color = GoldPrimary,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = article.fullText,
                fontSize = readerFontSize.sp,
                lineHeight = (readerFontSize * 1.6f).sp,
                color = TextDarkHighContrast
              )
              Spacer(modifier = Modifier.height(32.dp))
            }
          }
        }
      }
    }
  }

  // Issue View Dialog
  activeIssue?.let { issue ->
    AlertDialog(
      onDismissRequest = onCloseIssue,
      title = { Text(issue.titleOr) },
      text = {
        Column {
          Text(text = issue.monthYear, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = issue.descriptionOr, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(text = "${issue.articles.size} ରଚନା ସଂକଳିତ | ଡାଉନଲୋଡ୍ ଆକାର: ${issue.downloadSizeMb}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onCloseIssue()
            Toast.makeText(context, "ଇ-ପତ୍ରିକା ଡାଉନଲୋଡ୍ ହେଉଛି...", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
        ) {
          Text(AppStrings.downloadPdf(currentLanguage))
        }
      },
      dismissButton = {
        TextButton(onClick = onCloseIssue) {
          Text(AppStrings.close(currentLanguage))
        }
      }
    )
  }
}

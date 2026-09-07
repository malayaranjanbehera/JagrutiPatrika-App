package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppStrings
import com.example.model.*
import com.example.ui.components.ThreeDLogo
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
  currentUser: UserProfile?,
  submissions: List<Submission>,
  posts: List<Post>,
  auditLogs: List<AuditLog>,
  currentLanguage: AppLanguage,
  onBack: () -> Unit,
  onUpdateSubmissionStatus: (String, SubmissionStatus, String) -> Unit,
  onPublishSubmission: (String) -> Unit,
  onDeletePost: (String, Boolean) -> Unit,
  onToggleFeaturePost: (String) -> Unit
) {
  val context = LocalContext.current
  var adminTab by remember { mutableIntStateOf(0) } // 0: Submissions, 1: Posts Moderation, 2: Audit Logs

  var correctionDialogSub by remember { mutableStateOf<Submission?>(null) }
  var correctionNotes by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = LavenderPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = AppStrings.adminPanelTitle(currentLanguage),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = LavenderPrimary
              )
              Text(
                text = "ସର୍ବାଧିକ ୪ ପ୍ରଶାସକ କ୍ୟାପ୍ (Max 4 Admins)",
                fontSize = 10.sp,
                color = GoldPrimary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LavenderPrimary)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
      )
    },
    containerColor = ObsidianBg
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .testTag("admin_screen")
    ) {
      // Security Whitelist Notice Banner
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DeepVioletContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderPrimary.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "ସୁରକ୍ଷିତ ପ୍ରଶାସକ ସିଷ୍ଟମ୍ (Whitelisted Admins)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = OnVioletContainer
            )
            Text(
              text = "ପ୍ରାଥମିକ ପ୍ରଶାସକ: zoyaadahlia09@gmail.com (ସକ୍ରିୟ: 1/4)",
              fontSize = 11.sp,
              color = TextDarkMuted
            )
          }
        }
      }

      // Tab selector
      TabRow(
        selectedTabIndex = adminTab,
        containerColor = DarkSurface,
        contentColor = LavenderPrimary
      ) {
        Tab(
          selected = adminTab == 0,
          onClick = { adminTab = 0 },
          text = { Text("ଦାଖଲ ଲେଖା (${submissions.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = adminTab == 1,
          onClick = { adminTab = 1 },
          text = { Text("ପୋଷ୍ଟ ନିୟନ୍ତ୍ରଣ (${posts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = adminTab == 2,
          onClick = { adminTab = 2 },
          text = { Text("ଅଡିଟ୍ ଲଗ୍ (${auditLogs.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
      }

      // Tab Content
      when (adminTab) {
        0 -> {
          // Submissions list
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(submissions, key = { it.id }) { sub ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = sub.title,
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp,
                      color = TextDarkHighContrast,
                      modifier = Modifier.weight(1f)
                    )
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepVioletContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text(text = sub.status.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnVioletContainer)
                    }
                  }

                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "ଲେଖକ: ${sub.authorName} • ବିଭାଗ: ${sub.category.labelOr} • ${sub.submittedAt}",
                    fontSize = 11.sp,
                    color = TextDarkMuted
                  )

                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = sub.content,
                    fontSize = 12.sp,
                    maxLines = 3,
                    lineHeight = 17.sp,
                    color = TextDarkHighContrast
                  )

                  Spacer(modifier = Modifier.height(12.dp))

                  // Actions: Approve, Request Fix, Reject, Publish to Feed
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    // Approve
                    OutlinedButton(
                      onClick = {
                        onUpdateSubmissionStatus(sub.id, SubmissionStatus.APPROVED, "Approved by Editorial Board")
                        Toast.makeText(context, "ଲେଖା ଅନୁମୋଦିତ (Approved)", Toast.LENGTH_SHORT).show()
                      },
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                      Text("ଅନୁମୋଦନ", fontSize = 11.sp, color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
                    }

                    // Request Correction
                    OutlinedButton(
                      onClick = {
                        correctionDialogSub = sub
                        correctionNotes = sub.editorialNotes
                      },
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                      Text("ସଂଶୋଧନ", fontSize = 11.sp, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                    }

                    // Reject
                    OutlinedButton(
                      onClick = {
                        onUpdateSubmissionStatus(sub.id, SubmissionStatus.REJECTED, "Does not meet editorial guidelines")
                        Toast.makeText(context, "ପ୍ରତ୍ୟାଖ୍ୟାତ (Rejected)", Toast.LENGTH_SHORT).show()
                      },
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                      Text("ବାତିଲ", fontSize = 11.sp, color = Color(0xFFE57373), fontWeight = FontWeight.Bold)
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  // Publish to Social Feed Button
                  Button(
                    onClick = {
                      onPublishSubmission(sub.id)
                      Toast.makeText(context, "ଲେଖା ସାମାଜିକ ଫିଡ୍‌ରେ ପ୍ରକାଶିତ ହେଲା! (Published to Feed)", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = OnLavenderPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                  ) {
                    Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp), tint = OnLavenderPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ଫିଡ୍‌ରେ ପ୍ରକାଶ କରନ୍ତୁ (Publish to Feed)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnLavenderPrimary)
                  }
                }
              }
            }
          }
        }

        1 -> {
          // Posts Moderation
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(posts, key = { it.id }) { p ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(text = p.titleOr, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDarkHighContrast)
                    Text(text = "ଲେଖକ: ${p.authorName} • Likes: ${p.likeCount} • Comments: ${p.commentCount}", fontSize = 11.sp, color = TextDarkMuted)
                  }

                  Row {
                    IconButton(onClick = { onToggleFeaturePost(p.id) }) {
                      Icon(
                        imageVector = if (p.isFeatured) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Feature",
                        tint = GoldPrimary
                      )
                    }

                    IconButton(
                      onClick = {
                        onDeletePost(p.id, true)
                        Toast.makeText(context, "ପୋଷ୍ଟ ବାତିଲ କରାଗଲା (Post deleted by Admin)", Toast.LENGTH_SHORT).show()
                      }
                    ) {
                      Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                  }
                }
              }
            }
          }
        }

        2 -> {
          // Audit logs
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(auditLogs, key = { it.id }) { log ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LavenderPrimary)
                    Text(text = "୨୦୨୬-୦୪-୧୨", fontSize = 10.sp, color = TextDarkMuted)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(text = log.details, fontSize = 12.sp, color = TextDarkHighContrast)
                  Text(text = "ପ୍ରଶାସକ: ${log.adminEmail}", fontSize = 10.sp, color = GoldPrimary, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }
        }
      }
    }
  }

  // Request Correction Dialog
  correctionDialogSub?.let { sub ->
    AlertDialog(
      onDismissRequest = { correctionDialogSub = null },
      title = { Text("ସଂଶୋଧନ ନିର୍ଦ୍ଦେଶାବଳୀ (Request Correction)") },
      text = {
        Column {
          Text(text = "ଲେଖକଙ୍କ ପାଇଁ ସମ୍ପାଦକୀୟ ଟିପ୍ପଣୀ ଲେଖନ୍ତୁ:", fontSize = 12.sp)
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = correctionNotes,
            onValueChange = { correctionNotes = it },
            placeholder = { Text("ଉଦାହରଣ: ବନାନ ଶୁଦ୍ଧି କରନ୍ତୁ କିମ୍ବା ଶେଷ ଦୁଇ ପଦକୁ ସଂଶୋଧନ କରନ୍ତୁ...") },
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateSubmissionStatus(sub.id, SubmissionStatus.CORRECTION_REQUIRED, correctionNotes)
            correctionDialogSub = null
            Toast.makeText(context, "ସଂଶୋଧନ ନିର୍ଦ୍ଦେଶ ପ୍ରେରିତ!", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
        ) {
          Text("ପଠାନ୍ତୁ")
        }
      },
      dismissButton = {
        TextButton(onClick = { correctionDialogSub = null }) {
          Text("ବାତିଲ")
        }
      }
    )
  }
}

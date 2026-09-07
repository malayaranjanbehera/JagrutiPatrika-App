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
import com.example.data.AppStrings
import com.example.model.*
import com.example.ui.components.PostCard
import com.example.ui.components.ThreeDLogo
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
  currentUser: UserProfile?,
  submissions: List<Submission>,
  bookmarkedPosts: List<Post>,
  myPublishedPosts: List<Post>,
  currentLanguage: AppLanguage,
  onNavigateToAdmin: () -> Unit,
  onNavigateToSubmit: () -> Unit,
  onResubmitWork: (String, String) -> Unit,
  onRoleSwitch: (UserRole) -> Unit,
  onSignOut: () -> Unit,
  onLikeClick: (String) -> Unit,
  onCommentClick: (Post) -> Unit,
  onShareClick: (Post) -> Unit,
  onBookmarkClick: (String) -> Unit,
  onDeletePost: (String) -> Unit,
  onEditPost: (String, String, String) -> Unit
) {
  val context = LocalContext.current
  val user = currentUser ?: return

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Submissions, 1: Bookmarks, 2: Published
  var resubmittingSub by remember { mutableStateOf<Submission?>(null) }
  var resubmitContent by remember { mutableStateOf("") }

  val mySubmissions = remember(submissions, user.uid) {
    submissions.filter { it.authorId == user.uid }
  }

  // Calculate author dashboard metrics
  val totalSubs = mySubmissions.size
  val underReviewCount = mySubmissions.count { it.status == SubmissionStatus.SUBMITTED || it.status == SubmissionStatus.UNDER_REVIEW }
  val correctionCount = mySubmissions.count { it.status == SubmissionStatus.CORRECTION_REQUIRED }
  val approvedCount = mySubmissions.count { it.status == SubmissionStatus.APPROVED }
  val publishedCount = mySubmissions.count { it.status == SubmissionStatus.PUBLISHED }
  val rejectedCount = mySubmissions.count { it.status == SubmissionStatus.REJECTED }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .padding(horizontal = 16.dp)
      .testTag("profile_screen")
  ) {
    // Header Profile Card
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(74.dp)
              .clip(CircleShape)
              .background(Brush.sweepGradient(listOf(LavenderPrimary, GoldLight, DeepVioletContainer, GoldLight)))
              .padding(3.dp)
              .clip(CircleShape)
              .background(DeepVioletContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = user.displayName.take(1),
              color = OnVioletContainer,
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = user.displayName,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextDarkHighContrast
          )

          Text(
            text = user.email,
            fontSize = 12.sp,
            color = TextDarkMuted
          )

          Spacer(modifier = Modifier.height(6.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(DeepVioletContainer)
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = if (currentLanguage == AppLanguage.ODIA) user.userType.labelOr else user.userType.labelEn,
                color = OnVioletContainer,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            if (user.role == UserRole.ADMIN) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(GoldPrimary)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "★ CHIEF ADMIN",
                  color = DeepCharcoal,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Followers / Following / Published counters
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            StatItem(number = "${myPublishedPosts.size}", label = if (currentLanguage == AppLanguage.ODIA) "ପ୍ରକାଶିତ" else "Published")
            StatItem(number = "${user.followersCount}", label = if (currentLanguage == AppLanguage.ODIA) "ଅନୁଗାମୀ" else "Followers")
            StatItem(number = "${user.followingCount}", label = if (currentLanguage == AppLanguage.ODIA) "ଅନୁସରଣ" else "Following")
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Admin panel trigger if admin
          if (user.role == UserRole.ADMIN) {
            Button(
              onClick = onNavigateToAdmin,
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.buttonColors(containerColor = DeepVioletContainer, contentColor = OnVioletContainer),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = LavenderPrimary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(AppStrings.adminPanelTitle(currentLanguage), color = OnVioletContainer, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          // Submit Work quick button
          Button(
            onClick = onNavigateToSubmit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = OnLavenderPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.EditNote, contentDescription = null, tint = OnLavenderPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(AppStrings.submitWork(currentLanguage), fontWeight = FontWeight.Bold, color = OnLavenderPrimary)
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Author Dashboard Counter Grid (Section 9 & 10)
    item {
      Text(
        text = AppStrings.authorDashboard(currentLanguage),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = CrimsonPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DashboardMetricCard(
          title = AppStrings.totalSubmissions(currentLanguage),
          count = totalSubs,
          color = CrimsonPrimary,
          modifier = Modifier.weight(1f)
        )
        DashboardMetricCard(
          title = AppStrings.underReview(currentLanguage),
          count = underReviewCount,
          color = GoldDark,
          modifier = Modifier.weight(1f)
        )
        DashboardMetricCard(
          title = AppStrings.correctionRequired(currentLanguage),
          count = correctionCount,
          color = Color(0xFFE65100),
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DashboardMetricCard(
          title = AppStrings.approved(currentLanguage),
          count = approvedCount,
          color = Color(0xFF2E7D32),
          modifier = Modifier.weight(1f)
        )
        DashboardMetricCard(
          title = AppStrings.published(currentLanguage),
          count = publishedCount,
          color = Color(0xFF00695C),
          modifier = Modifier.weight(1f)
        )
        DashboardMetricCard(
          title = AppStrings.rejected(currentLanguage),
          count = rejectedCount,
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }

    // Tabs: Submissions / Bookmarks / Published
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = CrimsonPrimary
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text(AppStrings.mySubmissions(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text(AppStrings.bookmarked(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text(AppStrings.myPublishedWorks(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    // Tab Contents
    when (selectedTab) {
      0 -> {
        // My Submissions
        if (mySubmissions.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (currentLanguage == AppLanguage.ODIA) "କୌଣସି ଦାଖଲ ଲେଖା ନାହିଁ।" else "No submissions yet. Tap 'Submit Work' to get started!",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
              )
            }
          }
        } else {
          items(mySubmissions, key = { it.id }) { sub ->
            SubmissionItemCard(
              submission = sub,
              currentLanguage = currentLanguage,
              onResubmitClick = {
                resubmittingSub = sub
                resubmitContent = sub.content
              }
            )
          }
        }
      }

      1 -> {
        // Bookmarks
        if (bookmarkedPosts.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (currentLanguage == AppLanguage.ODIA) "କୌଣସି ସାଇତା ଲେଖା ନାହିଁ।" else "No bookmarked posts yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
              )
            }
          }
        } else {
          items(bookmarkedPosts, key = { it.id }) { post ->
            PostCard(
              post = post,
              currentUser = currentUser,
              currentLanguage = currentLanguage,
              onLikeClick = { onLikeClick(post.id) },
              onCommentClick = { onCommentClick(post) },
              onShareClick = { onShareClick(post) },
              onBookmarkClick = { onBookmarkClick(post.id) },
              onEditClick = { onEditPost(post.id, post.titleOr, post.contentOr) },
              onDeleteClick = { onDeletePost(post.id) }
            )
          }
        }
      }

      2 -> {
        // My Published Works
        if (myPublishedPosts.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (currentLanguage == AppLanguage.ODIA) "କୌଣସି ପ୍ରକାଶିତ ରଚନା ନାହିଁ।" else "No published posts yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
              )
            }
          }
        } else {
          items(myPublishedPosts, key = { it.id }) { post ->
            PostCard(
              post = post,
              currentUser = currentUser,
              currentLanguage = currentLanguage,
              onLikeClick = { onLikeClick(post.id) },
              onCommentClick = { onCommentClick(post) },
              onShareClick = { onShareClick(post) },
              onBookmarkClick = { onBookmarkClick(post.id) },
              onEditClick = { onEditPost(post.id, post.titleOr, post.contentOr) },
              onDeleteClick = { onDeletePost(post.id) }
            )
          }
        }
      }
    }

    // Role switcher & Sign Out Section
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "ଡେମୋ ରୋଲ୍ ସୁଇଚ୍ (Demo Role Switcher):",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CrimsonPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
              selected = user.role == UserRole.ADMIN,
              onClick = { onRoleSwitch(UserRole.ADMIN) },
              label = { Text("Admin") }
            )
            FilterChip(
              selected = user.role == UserRole.AUTHOR,
              onClick = { onRoleSwitch(UserRole.AUTHOR) },
              label = { Text("Author / Writer") }
            )
            FilterChip(
              selected = user.role == UserRole.READER,
              onClick = { onRoleSwitch(UserRole.READER) },
              label = { Text("Reader") }
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
          ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(AppStrings.signOut(currentLanguage), fontWeight = FontWeight.Bold)
          }
        }
      }
      Spacer(modifier = Modifier.height(88.dp))
    }
  }

  // Resubmit with edits dialog
  resubmittingSub?.let { sub ->
    AlertDialog(
      onDismissRequest = { resubmittingSub = null },
      title = { Text("ରଚନା ସଂଶୋଧନ (Resubmit Work)") },
      text = {
        Column {
          Text(
            text = "ସମ୍ପାଦକୀୟ ମତାମତ:\n${sub.editorialNotes}",
            fontSize = 12.sp,
            color = Color(0xFFE65100),
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = resubmitContent,
            onValueChange = { resubmitContent = it },
            label = { Text("ସଂଶୋଧିତ ରଚନା (Updated Content)") },
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (resubmitContent.isNotBlank()) {
              onResubmitWork(sub.id, resubmitContent)
              resubmittingSub = null
              Toast.makeText(context, "ସଂଶୋଧିତ ରଚନା ପୁନର୍ବାର ଦାଖଲ ହେଲା!", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
        ) {
          Text("ପୁନର୍ବାର ଦାଖଲ କରନ୍ତୁ")
        }
      },
      dismissButton = {
        TextButton(onClick = { resubmittingSub = null }) {
          Text(AppStrings.cancel(currentLanguage))
        }
      }
    )
  }
}

@Composable
private fun StatItem(number: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = number, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LavenderPrimary)
    Text(text = label, fontSize = 11.sp, color = TextDarkMuted)
  }
}

@Composable
private fun DashboardMetricCard(
  title: String,
  count: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "$count",
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = color
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        lineHeight = 13.sp,
        color = TextDarkHighContrast
      )
    }
  }
}

@Composable
private fun SubmissionItemCard(
  submission: Submission,
  currentLanguage: AppLanguage,
  onResubmitClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
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
          text = submission.title,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = TextDarkHighContrast,
          modifier = Modifier.weight(1f)
        )

        // Status Badge
        val (bgColor, textColor, label) = when (submission.status) {
          SubmissionStatus.SUBMITTED -> Triple(DeepVioletContainer, OnVioletContainer, "ଦାଖଲ ହୋଇଛି (Submitted)")
          SubmissionStatus.UNDER_REVIEW -> Triple(GoldContainer, OnGoldContainer, "ପର୍ଯ୍ୟାଲୋଚନା (Under Review)")
          SubmissionStatus.CORRECTION_REQUIRED -> Triple(Color(0xFF4A3418), Color(0xFFFFB74D), "ସଂଶୋଧନ ଆବଶ୍ୟକ (Fix)")
          SubmissionStatus.APPROVED -> Triple(Color(0xFF1B3828), Color(0xFF81C784), "ଅନୁମୋଦିତ (Approved)")
          SubmissionStatus.PUBLISHED -> Triple(Color(0xFF163E3A), Color(0xFF4DB6AC), "ପ୍ରକାଶିତ (Published)")
          SubmissionStatus.REJECTED -> Triple(Color(0xFF421C1F), Color(0xFFE57373), "ପ୍ରତ୍ୟାଖ୍ୟାତ (Rejected)")
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "ବିଭାଗ: ${submission.category.labelOr} • ${submission.submittedAt}",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = submission.content,
        fontSize = 12.sp,
        maxLines = 2,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      // Editorial Notes if any
      if (submission.editorialNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp)
        ) {
          Text(
            text = "ସମ୍ପାଦକଙ୍କ ଟିପ୍ପଣୀ: ${submission.editorialNotes}",
            fontSize = 11.sp,
            color = CrimsonPrimary,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Resubmit action if correction required
      if (submission.status == SubmissionStatus.CORRECTION_REQUIRED) {
        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = onResubmitClick,
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("ସଂଶୋଧନ କରି ପଠାନ୍ତୁ", fontSize = 12.sp)
        }
      }
    }
  }
}

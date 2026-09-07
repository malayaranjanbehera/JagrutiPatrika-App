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
import androidx.compose.ui.draw.shadow
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
import com.example.ui.components.StoriesRow
import com.example.ui.components.ThreeDLogo
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  posts: List<Post>,
  stories: List<Story>,
  currentUser: UserProfile?,
  currentLanguage: AppLanguage,
  selectedCategory: PostCategory,
  searchQuery: String,
  isRefreshing: Boolean,
  unreadNotificationsCount: Int,
  onCategorySelected: (PostCategory) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onRefresh: () -> Unit,
  onLanguageToggle: () -> Unit,
  onNotificationsClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onAdminClick: () -> Unit,
  onAddStoryClick: () -> Unit,
  onStoryClick: (Story) -> Unit,
  onLikeClick: (String) -> Unit,
  onCommentClick: (Post) -> Unit,
  onShareClick: (Post) -> Unit,
  onBookmarkClick: (String) -> Unit,
  onDeletePost: (String) -> Unit,
  onEditPost: (String, String, String) -> Unit,
  onMagazineBannerClick: () -> Unit
) {
  val context = LocalContext.current
  var editingPost by remember { mutableStateOf<Post?>(null) }
  var editTitle by remember { mutableStateOf("") }
  var editContent by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      Surface(
        color = DarkSurface,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
      ) {
        Column {
          // Top Branding Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .statusBarsPadding()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Brand & 3D Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
              ThreeDLogo(size = 38.dp, isInteractive = false)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "ଜାଗୃତି",
                  color = LavenderPrimary,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.ExtraBold
                )
                Text(
                  text = "JAGRUTIPATRIKA",
                  color = TextDarkMuted,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.5.sp
                )
              }
            }

            // Action icons: Admin badge, Language, Notifications, Settings
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (currentUser?.role == UserRole.ADMIN) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldPrimary)
                    .clickable { onAdminClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = DeepCharcoal, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ADMIN", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = DeepCharcoal)
                  }
                }
                Spacer(modifier = Modifier.width(8.dp))
              }

              // Language toggle chip
              OutlinedButton(
                onClick = onLanguageToggle,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(DarkBorder, LavenderPrimary))),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary)
              ) {
                Text(text = AppStrings.languageToggle(currentLanguage), fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.width(6.dp))

              // Notifications Bell with Badge
              IconButton(onClick = onNotificationsClick, modifier = Modifier.size(36.dp)) {
                BadgedBox(
                  badge = {
                    if (unreadNotificationsCount > 0) {
                      Badge(containerColor = LavenderPrimary, contentColor = OnLavenderPrimary) {
                        Text("$unreadNotificationsCount")
                      }
                    }
                  }
                ) {
                  Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = TextDarkHighContrast)
                }
              }

              // Settings
              IconButton(onClick = onSettingsClick, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = TextDarkHighContrast)
              }
            }
          }

          // Search Bar in Elegant Dark
          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text(AppStrings.searchPlaceholder(currentLanguage), fontSize = 13.sp, color = TextDarkMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LavenderPrimary) },
            trailingIcon = {
              if (searchQuery.isNotBlank()) {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextDarkMuted)
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp)
              .testTag("search_bar"),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = LavenderPrimary,
              unfocusedBorderColor = DarkBorder,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurface,
              focusedTextColor = TextDarkHighContrast,
              unfocusedTextColor = TextDarkHighContrast
            )
          )

          // Category Chips
          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(PostCategory.values().toList()) { category ->
              val isSelected = selectedCategory == category
              FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                  Text(
                    text = if (currentLanguage == AppLanguage.ODIA) category.labelOr else category.labelEn,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = DeepVioletContainer,
                  selectedLabelColor = OnVioletContainer,
                  containerColor = DarkSurfaceElevated
                ),
                border = FilterChipDefaults.filterChipBorder(
                  enabled = true,
                  selected = isSelected,
                  borderColor = if (isSelected) LavenderPrimary else DarkBorder
                )
              )
            }
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(ObsidianBg)
        .padding(paddingValues)
        .testTag("posts_feed_list")
    ) {
      // Pull to Refresh indicator
      if (isRefreshing) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(DeepVioletContainer)
              .padding(10.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = LavenderPrimary, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = AppStrings.refreshing(currentLanguage),
                color = OnVioletContainer,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      // Stories Row
      item {
        StoriesRow(
          stories = stories,
          onAddStoryClick = onAddStoryClick,
          onStoryClick = onStoryClick,
          currentLanguage = currentLanguage
        )
      }

      // 3D Hero Banner: Featured Digital Magazine Issue in Elegant Dark
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onMagazineBannerClick() },
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                brush = Brush.horizontalGradient(
                  listOf(DarkSurfaceElevated, Color(0xFF3C344D), DarkSurface)
                )
              )
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .background(DeepVioletContainer, RoundedCornerShape(6.dp))
                  .border(1.dp, LavenderPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "★ ନୂତନ ଇ-ପତ୍ରିକା ଉନ୍ମୋଚିତ",
                  color = OnVioletContainer,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "ଜାଗୃତି - ବସନ୍ତ ବିଶେଷାଙ୍କ ୨୦୨୬",
                color = TextDarkHighContrast,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "ଓଡ଼ିଶାର ଶ୍ରେଷ୍ଠ କବିତା, ଗଳ୍ପ ଓ ଐତିହ୍ୟ ପ୍ରବନ୍ଧ ସଙ୍କଳନ",
                color = GoldContainer,
                fontSize = 12.sp
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            ThreeDLogo(size = 54.dp, isInteractive = false)
          }
        }
      }

      // Feed Posts
      if (posts.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(48.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = if (currentLanguage == AppLanguage.ODIA) "କୌଣସି ଲେଖା ମିଳିଲା ନାହିଁ" else "No publications found for this filter",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
              )
            }
          }
        }
      } else {
        items(posts, key = { it.id }) { post ->
          PostCard(
            post = post,
            currentUser = currentUser,
            currentLanguage = currentLanguage,
            onLikeClick = { onLikeClick(post.id) },
            onCommentClick = { onCommentClick(post) },
            onShareClick = { onShareClick(post) },
            onBookmarkClick = { onBookmarkClick(post.id) },
            onEditClick = { p ->
              editingPost = p
              editTitle = if (currentLanguage == AppLanguage.ODIA && p.titleOr.isNotBlank()) p.titleOr else p.titleEn
              editContent = if (currentLanguage == AppLanguage.ODIA && p.contentOr.isNotBlank()) p.contentOr else p.contentEn
            },
            onDeleteClick = { p -> onDeletePost(p.id) }
          )
        }
      }

      // Bottom Spacer for Bottom Navigation
      item {
        Spacer(modifier = Modifier.height(84.dp))
      }
    }
  }

  // Edit Post Dialog
  editingPost?.let { post ->
    AlertDialog(
      onDismissRequest = { editingPost = null },
      title = { Text(AppStrings.editPost(currentLanguage)) },
      text = {
        Column {
          OutlinedTextField(
            value = editTitle,
            onValueChange = { editTitle = it },
            label = { Text(AppStrings.titleField(currentLanguage)) },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = editContent,
            onValueChange = { editContent = it },
            label = { Text(AppStrings.contentField(currentLanguage)) },
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editTitle.isNotBlank() && editContent.isNotBlank()) {
              onEditPost(post.id, editTitle, editContent)
              editingPost = null
              Toast.makeText(context, "ସଫଳତାର ସହ ସଂଶୋଧିତ (Post updated)", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
        ) {
          Text(AppStrings.save(currentLanguage))
        }
      },
      dismissButton = {
        TextButton(onClick = { editingPost = null }) {
          Text(AppStrings.cancel(currentLanguage))
        }
      }
    )
  }
}

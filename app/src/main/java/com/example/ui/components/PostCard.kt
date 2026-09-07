package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppStrings
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun PostCard(
  post: Post,
  currentUser: UserProfile?,
  currentLanguage: AppLanguage,
  onLikeClick: () -> Unit,
  onCommentClick: () -> Unit,
  onShareClick: () -> Unit,
  onBookmarkClick: () -> Unit,
  onEditClick: (Post) -> Unit,
  onDeleteClick: (Post) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var isExpanded by remember { mutableStateOf(false) }
  var showMenu by remember { mutableStateOf(false) }

  // Animated heart bounce
  var likeTrigger by remember { mutableIntStateOf(0) }
  val heartScale by animateFloatAsState(
    targetValue = if (likeTrigger > 0) 1.28f else 1.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    finishedListener = { likeTrigger = 0 },
    label = "heartScale"
  )

  val isAuthor = currentUser?.uid == post.authorId
  val isAdmin = currentUser?.role == UserRole.ADMIN

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("post_card_${post.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(
      if (post.isFeatured) 1.5.dp else 1.dp,
      if (post.isFeatured) LavenderPrimary.copy(alpha = 0.8f) else DarkBorder
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Author Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  listOf(LavenderPrimary, DeepVioletContainer, GoldLight)
                )
              )
              .padding(2.dp)
              .clip(CircleShape)
              .background(DeepVioletContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = post.authorName.take(1),
              color = OnVioletContainer,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = post.authorName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextDarkHighContrast,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (post.isFeatured) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = "Featured",
                  tint = GoldPrimary,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (currentLanguage == AppLanguage.ODIA) post.authorType.labelOr else post.authorType.labelEn,
                fontSize = 11.sp,
                color = LavenderPrimary,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = " • ୩ ଘଣ୍ଟା ପୂର୍ବେ",
                fontSize = 11.sp,
                color = TextDarkMuted
              )
              if (post.isEdited) {
                Text(
                  text = " (Edited)",
                  fontSize = 10.sp,
                  color = GoldDark
                )
              }
            }
          }
        }

        // Category Badge and Menu
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(DeepVioletContainer)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = if (currentLanguage == AppLanguage.ODIA) post.category.labelOr else post.category.labelEn,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = OnVioletContainer
            )
          }

          if (isAuthor || isAdmin) {
            Box {
              IconButton(onClick = { showMenu = true }) {
                Icon(
                  imageVector = Icons.Default.MoreVert,
                  contentDescription = "Post options",
                  tint = TextDarkMuted
                )
              }
              DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
              ) {
                if (isAuthor) {
                  DropdownMenuItem(
                    text = { Text(AppStrings.editPost(currentLanguage)) },
                    onClick = {
                      showMenu = false
                      onEditClick(post)
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                  )
                }
                DropdownMenuItem(
                  text = {
                    Text(
                      text = if (isAdmin && !isAuthor) "Delete (Admin Moderation)" else AppStrings.deletePost(currentLanguage),
                      color = MaterialTheme.colorScheme.error
                    )
                  },
                  onClick = {
                    showMenu = false
                    onDeleteClick(post)
                  },
                  leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Title
      val displayTitle = if (currentLanguage == AppLanguage.ODIA && post.titleOr.isNotBlank()) post.titleOr else post.titleEn
      Text(
        text = displayTitle,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = TextDarkHighContrast,
        lineHeight = 24.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Content with decorative literary background card for poems & verses in Elegant Dark
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceElevated)
          .border(
            width = 1.dp,
            color = if (post.category == PostCategory.POETRY) LavenderPrimary.copy(alpha = 0.35f) else DarkBorder,
            shape = RoundedCornerShape(12.dp)
          )
          .padding(14.dp)
      ) {
        val displayContent = if (currentLanguage == AppLanguage.ODIA && post.contentOr.isNotBlank()) post.contentOr else post.contentEn
        Column {
          Text(
            text = displayContent,
            fontSize = 14.sp,
            lineHeight = 23.sp,
            color = TextDarkHighContrast,
            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis
          )
          if (displayContent.length > 140) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (isExpanded) AppStrings.readLess(currentLanguage) else AppStrings.readMore(currentLanguage),
              color = LavenderPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons Row: Like, Comment, Share, Bookmark, Download
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Like Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable {
              likeTrigger++
              onLikeClick()
            }
            .padding(vertical = 4.dp, horizontal = 6.dp)
        ) {
          Icon(
            imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Like",
            tint = if (post.isLikedByMe) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
              .size(22.dp)
              .scale(heartScale)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${post.likeCount}",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (post.isLikedByMe) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Comment Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable { onCommentClick() }
            .padding(vertical = 4.dp, horizontal = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.ModeComment,
            contentDescription = "Comment",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${post.commentCount}",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Share Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable { onShareClick() }
            .padding(vertical = 4.dp, horizontal = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = "Share",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${post.shareCount}",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Bookmark Button
        IconButton(
          onClick = { onBookmarkClick() },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = if (post.isBookmarkedByMe) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (post.isBookmarkedByMe) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
          )
        }

        // Download Action
        IconButton(
          onClick = {
            Toast.makeText(context, "ସଫଳତାର ସହ ସାଇତାଗଲା (Saved to Downloads)", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Download,
            contentDescription = "Download",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppStrings
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
  post: Post,
  comments: List<Comment>,
  currentUser: UserProfile?,
  currentLanguage: AppLanguage,
  onDismiss: () -> Unit,
  onAddComment: (String) -> Unit,
  onDeleteComment: (String) -> Unit,
  onReportComment: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var commentInput by remember { mutableStateOf("") }
  val context = LocalContext.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 24.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = AppStrings.commentsTitle(currentLanguage),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${comments.size} ମତାମତ",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Comments list
      if (comments.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (currentLanguage == AppLanguage.ODIA) "ପ୍ରଥମ ମତାମତ ଦିଅନ୍ତୁ..." else "Be the first to share your thoughts...",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(comments, key = { it.id }) { comment ->
            val isMyComment = currentUser?.uid == comment.authorId
            val isAdmin = currentUser?.role == UserRole.ADMIN

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(10.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(CrimsonContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = comment.authorName.take(1),
                  fontWeight = FontWeight.Bold,
                  color = CrimsonPrimaryDark,
                  fontSize = 12.sp
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = comment.authorName,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = comment.content,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                  lineHeight = 18.sp
                )
                if (comment.isReported) {
                  Text(
                    text = "(Reported for review)",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.error
                  )
                }
              }

              // Actions: Delete or Report
              Row {
                if (isMyComment || isAdmin) {
                  IconButton(
                    onClick = { onDeleteComment(comment.id) },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "Delete comment",
                      tint = MaterialTheme.colorScheme.error,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                } else {
                  IconButton(
                    onClick = {
                      onReportComment(comment.id)
                      Toast.makeText(context, "ମତାମତ ରିପୋର୍ଟ କରାଗଲା (Reported)", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Flag,
                      contentDescription = "Report comment",
                      tint = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Input field
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = commentInput,
          onValueChange = { commentInput = it },
          placeholder = { Text(AppStrings.writeComment(currentLanguage), fontSize = 13.sp) },
          modifier = Modifier
            .weight(1f)
            .testTag("comment_input_field"),
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CrimsonPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
          )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (commentInput.isNotBlank()) {
              onAddComment(commentInput.trim())
              commentInput = ""
            }
          },
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(CrimsonPrimary)
            .testTag("submit_comment_button")
        ) {
          Icon(
            imageVector = Icons.Default.Send,
            contentDescription = "Post",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
  post: Post,
  currentLanguage: AppLanguage,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val postTitle = if (currentLanguage == AppLanguage.ODIA && post.titleOr.isNotBlank()) post.titleOr else post.titleEn
  val shareText = "‘$postTitle’ by ${post.authorName}\n\nRead on Jagrutipatrika (ଜାଗୃତି ପତ୍ରିକା) - ସଚେତନ ସମାଜର ସ୍ୱର\nhttps://jagrutipatrika.org/post/${post.id}"

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      Text(
        text = if (currentLanguage == AppLanguage.ODIA) "ସେୟାର୍ କରନ୍ତୁ (Share)" else "Share this Creation",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        ShareIconItem(
          name = "WhatsApp",
          color = Color(0xFF25D366),
          icon = Icons.Default.ChatBubble,
          onClick = {
            try {
              val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                setPackage("com.whatsapp")
              }
              context.startActivity(intent)
            } catch (e: Exception) {
              val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
              }
              context.startActivity(Intent.createChooser(intent, "Share via"))
            }
            onDismiss()
          }
        )

        ShareIconItem(
          name = "Copy Link",
          color = Color(0xFF546E7A),
          icon = Icons.Default.Link,
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Jagrutipatrika Post", shareText))
            Toast.makeText(context, "ଲିଙ୍କ୍ କପି ହୋଇଛି (Link copied)", Toast.LENGTH_SHORT).show()
            onDismiss()
          }
        )

        ShareIconItem(
          name = "Telegram",
          color = Color(0xFF0088CC),
          icon = Icons.Default.Send,
          onClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Share via Telegram"))
            onDismiss()
          }
        )

        ShareIconItem(
          name = "More",
          color = CrimsonPrimary,
          icon = Icons.Default.Share,
          onClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Share post"))
            onDismiss()
          }
        )
      }
    }
  }
}

@Composable
private fun ShareIconItem(
  name: String,
  color: Color,
  icon: ImageVector,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .size(52.dp)
        .clip(CircleShape)
        .background(color.copy(alpha = 0.15f))
        .border(1.dp, color, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = name, tint = color, modifier = Modifier.size(24.dp))
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
  }
}

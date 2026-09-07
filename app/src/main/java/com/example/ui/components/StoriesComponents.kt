package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.Story
import com.example.ui.theme.*

@Composable
fun StoriesRow(
  stories: List<Story>,
  onAddStoryClick: () -> Unit,
  onStoryClick: (Story) -> Unit,
  currentLanguage: AppLanguage,
  modifier: Modifier = Modifier
) {
  LazyRow(
    modifier = modifier.fillMaxWidth(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Add Story Button
    item {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clickable { onAddStoryClick() }
          .width(68.dp)
      ) {
        Box(
          modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(CrimsonContainer)
            .border(2.dp, CrimsonPrimary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Story",
            tint = CrimsonPrimary,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (currentLanguage == AppLanguage.ODIA) "ଷ୍ଟୋରି ଯୋଡ଼ନ୍ତୁ" else "Add Story",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          textAlign = TextAlign.Center
        )
      }
    }

    // Published Stories
    items(stories, key = { it.id }) { story ->
      val ringColors = listOf(
        CrimsonPrimary,
        GoldLight,
        LotusCoral,
        CrimsonLight
      )
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clickable { onStoryClick(story) }
          .width(68.dp)
      ) {
        Box(
          modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(Brush.sweepGradient(ringColors))
            .padding(3.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
              brush = Brush.linearGradient(
                when (story.bgGradientIndex % 4) {
                  0 -> listOf(CrimsonPrimary, CrimsonPrimaryDark)
                  1 -> listOf(GoldPrimary, CrimsonLight)
                  2 -> listOf(Color(0xFF1B5E20), Color(0xFF004D40))
                  else -> listOf(Color(0xFF311B92), Color(0xFF4A148C))
                }
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = story.authorName.take(1),
            color = GoldLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = story.authorName.split(" ").firstOrNull() ?: story.authorName,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

/**
 * Full Screen Instagram-style Story Viewer with progress timer and tap left/right
 */
@Composable
fun StoryViewerDialog(
  story: Story,
  onDismiss: () -> Unit,
  onNext: () -> Unit = onDismiss,
  onPrev: () -> Unit = onDismiss
) {
  var isPaused by remember { mutableStateOf(false) }
  var progress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(story.id, isPaused) {
    if (!isPaused) {
      while (progress < 1f) {
        kotlinx.coroutines.delay(50)
        progress += 0.012f
      }
      onNext()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .pointerInput(Unit) {
          detectTapGestures(
            onPress = {
              isPaused = true
              tryAwaitRelease()
              isPaused = false
            },
            onTap = { offset ->
              if (offset.x < size.width * 0.35f) {
                onPrev()
              } else {
                onNext()
              }
            }
          )
        }
    ) {
      // Background gradient canvas
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            brush = Brush.verticalGradient(
              when (story.bgGradientIndex % 4) {
                0 -> listOf(CrimsonPrimaryDark, CrimsonPrimary, Color(0xFF200508))
                1 -> listOf(Color(0xFF3E2723), Color(0xFF6D4C41), Color(0xFF21130D))
                2 -> listOf(Color(0xFF1B5E20), Color(0xFF004D40), Color(0xFF00251A))
                else -> listOf(Color(0xFF311B92), Color(0xFF512DA8), Color(0xFF1A0A45))
              }
            )
          )
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        ) {
          Text(
            text = story.title,
            color = GoldLight,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(20.dp))
          Text(
            text = story.textContent,
            color = Color.White,
            fontSize = 20.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(28.dp))
          Box(
            modifier = Modifier
              .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "— ${story.authorName} | ଜାଗୃତି ଷ୍ଟୋରି —",
              color = GoldContainer,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // Top Progress and Header overlay
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 24.dp)
      ) {
        // Progress bar
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = GoldLight,
          trackColor = Color.White.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GoldLight),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = story.authorName.take(1),
                color = DeepCharcoal,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = story.authorName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "୨୪ ଘଣ୍ଟା ମଧ୍ୟରେ ସମାପ୍ତ (24h story)",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Story",
              tint = Color.White
            )
          }
        }
      }

      // Bottom Viewers badge
      Row(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(20.dp)
          .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Visibility,
          contentDescription = null,
          tint = GoldLight,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${story.viewersCount + 12} views",
          color = Color.White,
          fontSize = 12.sp
        )
      }
    }
  }
}

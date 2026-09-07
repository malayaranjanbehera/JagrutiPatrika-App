package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MagazineIssue
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3D Rotating & Illuminated Jagrutipatrika Crest
 * Symbolizes the eternal flame of literature, Konark sun wheel spokes, and open palm-leaf book.
 */
@Composable
fun ThreeDLogo(
  modifier: Modifier = Modifier,
  size: Dp = 100.dp,
  reducedMotion: Boolean = false,
  isInteractive: Boolean = true
) {
  val infiniteTransition = rememberInfiniteTransition(label = "3DLogoTransition")
  val rotationY by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = if (reducedMotion) 0f else 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 16000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotationY"
  )
  val pulseGlow by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = if (reducedMotion) 1f else 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseGlow"
  )

  var manualTiltX by remember { mutableFloatStateOf(0f) }
  var manualTiltY by remember { mutableFloatStateOf(0f) }

  Box(
    modifier = modifier
      .size(size)
      .pointerInput(isInteractive) {
        if (isInteractive) {
          detectTapGestures(
            onPress = { offset ->
              manualTiltX = ((offset.y / size.toPx()) - 0.5f) * 30f
              manualTiltY = ((offset.x / size.toPx()) - 0.5f) * -30f
              tryAwaitRelease()
              manualTiltX = 0f
              manualTiltY = 0f
            }
          )
        }
      }
      .graphicsLayer {
        rotationZ = if (reducedMotion) 0f else rotationY * 0.1f
        this.rotationX = manualTiltX
        this.rotationY = if (reducedMotion) 0f else (rotationY % 360f - 180f) * 0.2f + manualTiltY
        cameraDistance = 16f * density
        scaleX = pulseGlow * 0.95f
        scaleY = pulseGlow * 0.95f
      },
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      draw3DCrest(rotationY, size.toPx())
    }

    // Inner Emblem Odia Letter 'ଜ' in golden embossed relief
    Box(
      modifier = Modifier
        .size(size * 0.46f)
        .clip(CircleShape)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(DeepVioletContainer, DarkSurface),
            center = Offset.Zero
          )
        )
        .border(2.dp, Brush.linearGradient(listOf(LavenderPrimary, GoldLight)), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "ଜ",
        color = GoldLight,
        fontSize = (size.value * 0.26f).sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.graphicsLayer {
          shadowElevation = 6f
        }
      )
    }
  }
}

private fun DrawScope.draw3DCrest(angle: Float, dimension: Float) {
  val center = Offset(dimension / 2f, dimension / 2f)
  val radius = dimension / 2f * 0.88f

  // Outer ambient 3D shadow
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(LavenderPrimary.copy(alpha = 0.28f), Color.Transparent),
      center = center,
      radius = radius * 1.25f
    ),
    center = center,
    radius = radius * 1.25f
  )

  // Lavender & Gold Outer Ring
  drawCircle(
    brush = Brush.sweepGradient(
      listOf(LavenderPrimary, GoldLight, DeepVioletContainer, GoldPrimary, LavenderPrimary),
      center = center
    ),
    center = center,
    radius = radius,
    style = Stroke(width = dimension * 0.06f)
  )

  // 12 Konark Spokes / Sun Rays
  val spokeCount = 12
  for (i in 0 until spokeCount) {
    val spokeAngle = (i * 360f / spokeCount) + angle * 0.5f
    val rad = Math.toRadians(spokeAngle.toDouble())
    val startRadius = radius * 0.52f
    val endRadius = radius * 0.92f

    val start = Offset(
      center.x + (startRadius * cos(rad)).toFloat(),
      center.y + (startRadius * sin(rad)).toFloat()
    )
    val end = Offset(
      center.x + (endRadius * cos(rad)).toFloat(),
      center.y + (endRadius * sin(rad)).toFloat()
    )

    drawLine(
      brush = Brush.linearGradient(listOf(LavenderPrimary, GoldLight)),
      start = start,
      end = end,
      strokeWidth = dimension * 0.024f,
      cap = StrokeCap.Round
    )

    // Spoke tip bead
    drawCircle(
      color = LavenderPrimary,
      radius = dimension * 0.02f,
      center = end
    )
  }
}

/**
 * 3D Interactive Magazine Cover with real depth perspective, page sheen, and elevation shadow
 */
@Composable
fun ThreeDMagazineCard(
  issue: MagazineIssue,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  var tiltX by remember { mutableFloatStateOf(0f) }
  var tiltY by remember { mutableFloatStateOf(0f) }

  val animatedTiltX by animateFloatAsState(targetValue = tiltX, animationSpec = spring(), label = "tiltX")
  val animatedTiltY by animateFloatAsState(targetValue = tiltY, animationSpec = spring(), label = "tiltY")

  Card(
    modifier = modifier
      .width(260.dp)
      .height(360.dp)
      .pointerInput(Unit) {
        detectTapGestures(
          onPress = { offset ->
            tiltX = ((offset.y / size.height) - 0.5f) * -18f
            tiltY = ((offset.x / size.width) - 0.5f) * 18f
            tryAwaitRelease()
            tiltX = 0f
            tiltY = 0f
          },
          onTap = { onClick() }
        )
      }
      .graphicsLayer {
        rotationX = animatedTiltX
        rotationY = animatedTiltY
        cameraDistance = 14f * density
        shadowElevation = 24f
      }
      .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = LavenderPrimary.copy(alpha = 0.5f)),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.linearGradient(
            colors = when (issue.coverArtIndex % 3) {
              0 -> listOf(DeepVioletContainer, Color(0xFF2B2930), Color(0xFF1C1B1F))
              1 -> listOf(Color(0xFF261D36), Color(0xFF332D41), Color(0xFF1E1A24))
              else -> listOf(Color(0xFF1B2338), Color(0xFF282838), Color(0xFF1C1B1F))
            },
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
          )
        )
        .border(
          width = 2.dp,
          brush = Brush.linearGradient(listOf(LavenderPrimary, Color(0xFFEADDFF), GoldLight)),
          shape = RoundedCornerShape(16.dp)
        )
        .padding(18.dp)
    ) {
      // 3D Lighting Sheen overlay
      Box(
        modifier = Modifier
          .fillMaxSize()
          .drawBehind {
            drawRect(
              brush = Brush.linearGradient(
                colors = listOf(
                  Color.White.copy(alpha = 0.18f),
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.35f)
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
              )
            )
            // Left Spine Groove line
            drawLine(
              color = GoldLight.copy(alpha = 0.6f),
              start = Offset(12f, 0f),
              end = Offset(12f, size.height),
              strokeWidth = 3f
            )
          }
      )

      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Header
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ଜାଗୃତି ପତ୍ରିକା",
              color = GoldLight,
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp
            )
            Box(
              modifier = Modifier
                .background(GoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .border(1.dp, GoldLight, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "3D ଇ-ପତ୍ରିକା",
                color = GoldLight,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Text(
            text = issue.issueNumber,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
          )
          Text(
            text = issue.monthYear,
            color = GoldContainer,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Center 3D Crest inside cover
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          contentAlignment = Alignment.Center
        ) {
          ThreeDLogo(size = 86.dp, isInteractive = false)
        }

        // Footer Title & Action
        Column {
          Text(
            text = issue.titleOr,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${issue.articles.size} ବିଶେଷ ସାହିତ୍ୟ ରଚନା | ଖୋଲିବାକୁ ଟ୍ୟାପ୍ କରନ୍ତୁ",
            color = GoldLight,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

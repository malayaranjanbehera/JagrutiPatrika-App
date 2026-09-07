package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppStrings
import com.example.model.AppLanguage
import com.example.ui.components.ThreeDLogo
import com.example.ui.theme.*
import com.example.viewmodel.TextToImageState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextToImageScreen(
  state: TextToImageState,
  currentLanguage: AppLanguage,
  onUpdateState: ((TextToImageState) -> TextToImageState) -> Unit,
  onBack: () -> Unit,
  onExportAsStory: () -> Unit,
  onExportAsSubmission: () -> Unit
) {
  val context = LocalContext.current

  val gradientPresets = listOf(
    // 0: Royal Crimson & Gold
    listOf(CrimsonPrimaryDark, CrimsonPrimary, Color(0xFF3B0609)),
    // 1: Temple Terracotta
    listOf(Color(0xFF3E2723), Color(0xFF5D4037), Color(0xFF2E1C14)),
    // 2: Konark Midnight
    listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF415A77)),
    // 3: Chilika Teal
    listOf(Color(0xFF004D40), Color(0xFF00695C), Color(0xFF00251A)),
    // 4: Golden Harvest
    listOf(Color(0xFFE65100), Color(0xFFEF6C00), Color(0xFFBF360C)),
    // 5: Mystic Purple
    listOf(Color(0xFF311B92), Color(0xFF4A148C), Color(0xFF1A0A45))
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = AppStrings.textToImageTitle(currentLanguage),
            fontWeight = FontWeight.Bold,
            color = LavenderPrimary
          )
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
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
        .testTag("text_to_image_screen"),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Live Visual Canvas Preview
      val chosenGradient = gradientPresets.getOrElse(state.bgGradientIndex) { gradientPresets[0] }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(if (state.isStoryAspect) 380.dp else 280.dp)
          .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(chosenGradient))
            .border(2.dp, LavenderPrimary.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(20.dp)
        ) {
          // Top Watermark & 3D Brand Emblem
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              ThreeDLogo(size = 32.dp, isInteractive = false)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "ଜାଗୃତି ପତ୍ରିକା",
                color = GoldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = if (state.isStoryAspect) "9:16 STORY" else "1:1 POST",
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          // Center Verses
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.Center)
              .padding(horizontal = 8.dp),
            horizontalAlignment = when (state.textAlignIndex) {
              0 -> Alignment.Start
              2 -> Alignment.End
              else -> Alignment.CenterHorizontally
            }
          ) {
            if (state.title.isNotBlank()) {
              Text(
                text = "“${state.title}”",
                color = GoldLight,
                fontSize = (state.fontSizeSp + 3f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = when (state.textAlignIndex) {
                  0 -> TextAlign.Start
                  2 -> TextAlign.End
                  else -> TextAlign.Center
                }
              )
              Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
              text = state.verses,
              color = Color.White,
              fontSize = state.fontSizeSp.sp,
              lineHeight = (state.fontSizeSp * 1.5f).sp,
              fontWeight = FontWeight.Medium,
              textAlign = when (state.textAlignIndex) {
                0 -> TextAlign.Start
                2 -> TextAlign.End
                else -> TextAlign.Center
              }
            )
          }

          // Bottom Signature
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "— ${state.author}",
              color = GoldContainer,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "jagrutipatrika.org",
              color = Color.White.copy(alpha = 0.5f),
              fontSize = 10.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Aspect Ratio & Alignment Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Aspect switch
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "ଆକାର (Format):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(8.dp))
          FilterChip(
            selected = !state.isStoryAspect,
            onClick = { onUpdateState { it.copy(isStoryAspect = false) } },
            label = { Text("1:1") }
          )
          Spacer(modifier = Modifier.width(6.dp))
          FilterChip(
            selected = state.isStoryAspect,
            onClick = { onUpdateState { it.copy(isStoryAspect = true) } },
            label = { Text("9:16") }
          )
        }

        // Text Alignment
        Row {
          IconButton(onClick = { onUpdateState { it.copy(textAlignIndex = 0) } }) {
            Icon(Icons.Default.FormatAlignLeft, contentDescription = null, tint = if (state.textAlignIndex == 0) CrimsonPrimary else Color.Gray)
          }
          IconButton(onClick = { onUpdateState { it.copy(textAlignIndex = 1) } }) {
            Icon(Icons.Default.FormatAlignCenter, contentDescription = null, tint = if (state.textAlignIndex == 1) CrimsonPrimary else Color.Gray)
          }
          IconButton(onClick = { onUpdateState { it.copy(textAlignIndex = 2) } }) {
            Icon(Icons.Default.FormatAlignRight, contentDescription = null, tint = if (state.textAlignIndex == 2) CrimsonPrimary else Color.Gray)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Gradient Theme Selector
      Text(
        text = if (currentLanguage == AppLanguage.ODIA) "ରଙ୍ଗ ଥିମ୍ ବାଛନ୍ତୁ (Color Theme)" else "Color Theme",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.Start)
      )
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        itemsIndexed(gradientPresets) { index, colors ->
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(colors))
              .border(
                width = if (state.bgGradientIndex == index) 3.dp else 1.dp,
                color = if (state.bgGradientIndex == index) GoldLight else Color.Transparent,
                shape = CircleShape
              )
              .clickable { onUpdateState { it.copy(bgGradientIndex = index) } }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Font size slider
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "ଅକ୍ଷର ଆକାର (Font size):", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Slider(
          value = state.fontSizeSp,
          onValueChange = { onUpdateState { prev -> prev.copy(fontSizeSp = it) } },
          valueRange = 14f..26f,
          modifier = Modifier.weight(1f),
          colors = SliderDefaults.colors(
            thumbColor = LavenderPrimary,
            activeTrackColor = LavenderPrimary,
            inactiveTrackColor = DeepVioletContainer
          )
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Text Input fields
      OutlinedTextField(
        value = state.title,
        onValueChange = { newT -> onUpdateState { it.copy(title = newT) } },
        label = { Text("ଶୀର୍ଷକ (Title)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = state.verses,
        onValueChange = { newV -> onUpdateState { it.copy(verses = newV) } },
        label = { Text("କବିତା / ଉକ୍ତି (Poem / Quote)") },
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = state.author,
        onValueChange = { newA -> onUpdateState { it.copy(author = newA) } },
        label = { Text("ଲେଖକଙ୍କ ନାମ (Author Signature)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons: Save image, Export to Story, Submit to Review
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = {
            Toast.makeText(context, "ବ୍ରାଣ୍ଡେଡ୍ ଫଟୋ ସାଇତାଗଲା! (Saved Image)", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated, contentColor = TextDarkHighContrast),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Outlined.Download, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("ଫଟୋ ସାଇତନ୍ତୁ", fontSize = 12.sp)
        }

        Button(
          onClick = onExportAsStory,
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = OnLavenderPrimary),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = OnLavenderPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("ଷ୍ଟୋରିରେ ଦିଅନ୍ତୁ", fontSize = 12.sp, color = OnLavenderPrimary)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedButton(
        onClick = onExportAsSubmission,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary),
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderPrimary.copy(alpha = 0.5f))
      ) {
        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp), tint = LavenderPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("ସମ୍ପାଦକୀୟ ସମୀକ୍ଷା ପାଇଁ ଦାଖଲ କରନ୍ତୁ", fontSize = 13.sp, color = LavenderPrimary)
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

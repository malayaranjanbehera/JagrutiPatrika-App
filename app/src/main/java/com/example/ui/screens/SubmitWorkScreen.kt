package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
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
import com.example.model.AppLanguage
import com.example.model.PostCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitWorkScreen(
  currentLanguage: AppLanguage,
  onBack: () -> Unit,
  onSubmit: (String, PostCategory, AppLanguage, String, Boolean, () -> Unit) -> Unit
) {
  val context = LocalContext.current
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(PostCategory.POETRY) }
  var language by remember { mutableStateOf(currentLanguage) }
  var content by remember { mutableStateOf("") }
  var originalityDeclared by remember { mutableStateOf(false) }
  var errorText by remember { mutableStateOf<String?>(null) }

  val wordCount = remember(content) {
    if (content.isBlank()) 0 else content.trim().split("\\s+".toRegex()).size
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = AppStrings.submitWork(currentLanguage),
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
        .testTag("submit_work_screen")
    ) {
      // Info card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DeepVioletContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderPrimary.copy(alpha = 0.3f))
      ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = if (currentLanguage == AppLanguage.ODIA)
              "ଆପଣଙ୍କ ରଚନା ସମ୍ପାଦକ ମଣ୍ଡଳୀଙ୍କ ସମୀକ୍ଷା (Editorial Review) ପରେ ପତ୍ରିକା ଓ ଫିଡ୍‌ରେ ପ୍ରକାଶିତ ହେବ।"
            else
              "Your submission will be reviewed by the editorial board before appearing on the magazine and feed.",
            fontSize = 12.sp,
            color = OnVioletContainer,
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Category selector
      Text(
        text = if (currentLanguage == AppLanguage.ODIA) "ବିଭାଗ ବାଛନ୍ତୁ (Select Category)" else "Select Category",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = LavenderPrimary
      )
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(PostCategory.values().filter { it != PostCategory.ALL }) { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = {
              Text(
                text = if (currentLanguage == AppLanguage.ODIA) cat.labelOr else cat.labelEn,
                fontSize = 12.sp
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = LavenderPrimary,
              selectedLabelColor = OnLavenderPrimary
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Language selection
      Text(
        text = if (currentLanguage == AppLanguage.ODIA) "ଭାଷା (Language)" else "Submission Language",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = LavenderPrimary
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
          selected = language == AppLanguage.ODIA,
          onClick = { language = AppLanguage.ODIA },
          label = { Text("ଓଡ଼ିଆ (Odia)") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = LavenderPrimary,
            selectedLabelColor = OnLavenderPrimary
          )
        )
        FilterChip(
          selected = language == AppLanguage.ENGLISH,
          onClick = { language = AppLanguage.ENGLISH },
          label = { Text("English") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = LavenderPrimary,
            selectedLabelColor = OnLavenderPrimary
          )
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Title
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text(AppStrings.titleField(currentLanguage)) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("submission_title_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Content
      OutlinedTextField(
        value = content,
        onValueChange = { content = it },
        label = { Text(AppStrings.contentField(currentLanguage)) },
        placeholder = {
          Text(
            text = if (currentLanguage == AppLanguage.ODIA) "ଏଠାରେ ଆପଣଙ୍କ କବିତା, ଗଳ୍ପ ବା ପ୍ରବନ୍ଧ ଲେଖନ୍ତୁ..." else "Write your poetry, story, or article here...",
            fontSize = 13.sp
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .testTag("submission_content_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "$wordCount ଶବ୍ଦ (Words) • ${content.length} ଅକ୍ଷର",
        fontSize = 11.sp,
        color = TextDarkMuted,
        modifier = Modifier.align(Alignment.End)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Originality Checkbox
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(DarkSurfaceElevated)
          .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
          .clickable { originalityDeclared = !originalityDeclared }
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = originalityDeclared,
          onCheckedChange = { originalityDeclared = it },
          colors = CheckboxDefaults.colors(checkedColor = LavenderPrimary)
        )
        Text(
          text = AppStrings.originalityDeclaration(currentLanguage),
          fontSize = 12.sp,
          color = TextDarkHighContrast,
          lineHeight = 16.sp
        )
      }

      errorText?.let {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Submit Button
      Button(
        onClick = {
          errorText = null
          if (title.isBlank()) {
            errorText = "Please enter a title"
            return@Button
          }
          if (content.length < 30) {
            errorText = "Please enter at least 30 characters"
            return@Button
          }
          if (!originalityDeclared) {
            errorText = "Please confirm the originality declaration"
            return@Button
          }

          onSubmit(title.trim(), category, language, content.trim(), originalityDeclared) {
            Toast.makeText(context, "ରଚନା ସଫଳତାର ସହ ସମୀକ୍ଷା ପାଇଁ ଦାଖଲ ହେଲା!", Toast.LENGTH_LONG).show()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("submit_work_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = OnLavenderPrimary)
      ) {
        Icon(Icons.Default.Send, contentDescription = null, tint = OnLavenderPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = AppStrings.submitButton(currentLanguage),
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = OnLavenderPrimary
        )
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppStrings
import com.example.model.*
import com.example.ui.components.ThreeDLogo
import com.example.ui.theme.*

@Composable
fun AuthScreen(
  currentLanguage: AppLanguage,
  onLanguageToggle: () -> Unit,
  onSignIn: (String, String, (Boolean, String?) -> Unit) -> Unit,
  onSignUp: (String, String, String, String, UserType, AppLanguage, (Boolean, String?) -> Unit) -> Unit,
  onGoogleSignIn: () -> Unit,
  onContinueAsGuest: () -> Unit
) {
  val context = LocalContext.current
  var isSignUp by remember { mutableStateOf(false) }

  // Fields
  var fullName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("zoyaadahlia09@gmail.com") }
  var password by remember { mutableStateOf("Jagruti@2026") }
  var confirmPassword by remember { mutableStateOf("Jagruti@2026") }
  var phone by remember { mutableStateOf("") }
  var selectedUserType by remember { mutableStateOf(UserType.WRITER) }
  var selectedLang by remember { mutableStateOf(currentLanguage) }
  var acceptedTerms by remember { mutableStateOf(true) }

  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showResetDialog by remember { mutableStateOf(false) }
  var resetEmail by remember { mutableStateOf("") }

  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          listOf(ObsidianBg, DarkSurface, Color(0xFF1B1922))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top language toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        OutlinedButton(
          onClick = onLanguageToggle,
          shape = RoundedCornerShape(20.dp),
          border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(DarkBorder, LavenderPrimary))),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = AppStrings.languageToggle(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 3D Emblem and Header
      ThreeDLogo(size = 90.dp)
      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "ଜାଗୃତି ପତ୍ରିକା",
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        color = LavenderPrimary
      )
      Text(
        text = AppStrings.tagline(currentLanguage),
        fontSize = 13.sp,
        color = TextDarkMuted,
        fontWeight = FontWeight.SemiBold
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Sign In / Sign Up Tab Selector in Elegant Dark
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceElevated)
          .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (!isSignUp) DeepVioletContainer else Color.Transparent)
            .clickable { isSignUp = false; errorMessage = null }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = AppStrings.signIn(currentLanguage),
            color = if (!isSignUp) OnVioletContainer else TextDarkMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSignUp) DeepVioletContainer else Color.Transparent)
            .clickable { isSignUp = true; errorMessage = null }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = AppStrings.signUp(currentLanguage),
            color = if (isSignUp) OnVioletContainer else TextDarkMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Error message if any
      errorMessage?.let {
        Text(
          text = it,
          color = MaterialTheme.colorScheme.error,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(bottom = 12.dp)
        )
      }

      // Sign Up extra fields
      if (isSignUp) {
        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = { Text(AppStrings.fullName(currentLanguage)) },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = LavenderPrimary) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("full_name_field"),
          shape = RoundedCornerShape(14.dp),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LavenderPrimary,
            unfocusedBorderColor = DarkBorder,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurface
          )
        )
        Spacer(modifier = Modifier.height(12.dp))
      }

      // Email field
      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text(AppStrings.email(currentLanguage)) },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = LavenderPrimary) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("email_field"),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LavenderPrimary,
          unfocusedBorderColor = DarkBorder,
          focusedContainerColor = DarkSurfaceElevated,
          unfocusedContainerColor = DarkSurface
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Password field
      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(AppStrings.password(currentLanguage)) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LavenderPrimary) },
        trailingIcon = {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
              imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = null,
              tint = TextDarkMuted
            )
          }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("password_field"),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LavenderPrimary,
          unfocusedBorderColor = DarkBorder,
          focusedContainerColor = DarkSurfaceElevated,
          unfocusedContainerColor = DarkSurface
        )
      )

      if (isSignUp) {
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = confirmPassword,
          onValueChange = { confirmPassword = it },
          label = { Text(AppStrings.confirmPassword(currentLanguage)) },
          leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = LavenderPrimary) },
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("confirm_password_field"),
          shape = RoundedCornerShape(14.dp),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LavenderPrimary,
            unfocusedBorderColor = DarkBorder,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurface
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text(AppStrings.phoneNumber(currentLanguage)) },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = LavenderPrimary) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LavenderPrimary,
            unfocusedBorderColor = DarkBorder,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurface
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Profile Type Selection
        Text(
          text = AppStrings.userTypeLabel(currentLanguage),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = LavenderPrimary,
          modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(UserType.values().toList()) { type ->
            FilterChip(
              selected = selectedUserType == type,
              onClick = { selectedUserType = type },
              label = {
                Text(
                  text = if (currentLanguage == AppLanguage.ODIA) type.labelOr else type.labelEn,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = DeepVioletContainer,
                selectedLabelColor = OnVioletContainer,
                containerColor = DarkSurfaceElevated
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedUserType == type,
                borderColor = if (selectedUserType == type) LavenderPrimary else DarkBorder
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Terms Checkbox
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { acceptedTerms = !acceptedTerms }
        ) {
          Checkbox(
            checked = acceptedTerms,
            onCheckedChange = { acceptedTerms = it },
            colors = CheckboxDefaults.colors(checkedColor = LavenderPrimary)
          )
          Text(
            text = AppStrings.termsAcceptance(currentLanguage),
            fontSize = 12.sp,
            color = TextDarkHighContrast
          )
        }
      } else {
        // Forgot Password
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = { showResetDialog = true }) {
            Text(
              text = AppStrings.forgotPassword(currentLanguage),
              color = LavenderPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Main Action Button (Sign In or Sign Up)
      Button(
        onClick = {
          errorMessage = null
          if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all required credentials"
            return@Button
          }
          if (isSignUp) {
            if (fullName.isBlank()) {
              errorMessage = "Please enter your full name"
              return@Button
            }
            if (password != confirmPassword) {
              errorMessage = "Passwords do not match"
              return@Button
            }
            if (password.length < 8) {
              errorMessage = "Password must be at least 8 characters"
              return@Button
            }
            if (!acceptedTerms) {
              errorMessage = "Please accept Terms & Privacy"
              return@Button
            }
            onSignUp(fullName, email, password, phone, selectedUserType, selectedLang) { ok, err ->
              if (!ok) errorMessage = err
            }
          } else {
            onSignIn(email, password) { ok, err ->
              if (!ok) errorMessage = err
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("auth_submit_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = OnLavenderPrimary)
      ) {
        Text(
          text = if (isSignUp) AppStrings.signUp(currentLanguage) else AppStrings.signIn(currentLanguage),
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = OnLavenderPrimary
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Google Sign-In Button
      OutlinedButton(
        onClick = onGoogleSignIn,
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("google_signin_button"),
        shape = RoundedCornerShape(14.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(DarkBorder, LavenderPrimary)))
      ) {
        Icon(
          imageVector = Icons.Default.AccountCircle,
          contentDescription = "Google",
          tint = LavenderPrimary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = AppStrings.continueWithGoogle(currentLanguage),
          color = TextDarkHighContrast,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Direct explore as reader button
      TextButton(onClick = onContinueAsGuest) {
        Text(
          text = if (currentLanguage == AppLanguage.ODIA) "ଅତିଥି ଭାବରେ ପଢ଼ନ୍ତୁ (Explore as Reader)" else "Explore directly as Reader",
          color = TextDarkMuted,
          fontSize = 13.sp
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Admin role disclaimer
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = AppStrings.adminLimitNotice(currentLanguage),
            fontSize = 11.sp,
            color = TextDarkMuted,
            lineHeight = 15.sp
          )
        }
      }
    }
  }

  // Password Reset Dialog
  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = { Text(AppStrings.forgotPassword(currentLanguage)) },
      text = {
        Column {
          Text(
            text = if (currentLanguage == AppLanguage.ODIA) "ଆପଣଙ୍କ ଇମେଲ୍ ଦିଅନ୍ତୁ, ଆମେ ପାସୱାର୍ଡ ରିସେଟ୍ ଲିଙ୍କ୍ ପଠାଇବୁ।" else "Enter your registered email to receive a password reset link.",
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = resetEmail,
            onValueChange = { resetEmail = it },
            placeholder = { Text("email@example.com") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showResetDialog = false
            Toast.makeText(context, "ରିସେଟ୍ ଲିଙ୍କ୍ ପ୍ରେରିତ (Reset link sent)", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
        ) {
          Text(AppStrings.resetPassword(currentLanguage))
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text(AppStrings.cancel(currentLanguage))
        }
      }
    )
  }
}

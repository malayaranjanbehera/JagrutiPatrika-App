package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppStrings
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        JagrutipatrikaApp()
      }
    }
  }
}

@Composable
fun JagrutipatrikaApp(viewModel: AppViewModel = viewModel()) {
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
  val posts by viewModel.filteredPosts.collectAsStateWithLifecycle()
  val allPosts by viewModel.posts.collectAsStateWithLifecycle()
  val stories by viewModel.stories.collectAsStateWithLifecycle()
  val submissions by viewModel.submissions.collectAsStateWithLifecycle()
  val magazineIssues by viewModel.magazineIssues.collectAsStateWithLifecycle()
  val competitions by viewModel.competitions.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()
  val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
  val commentsMap by viewModel.comments.collectAsStateWithLifecycle()

  val activeStory by viewModel.activeStory.collectAsStateWithLifecycle()
  val activeCommentsPost by viewModel.activeCommentsPost.collectAsStateWithLifecycle()
  val activeSharePost by viewModel.activeSharePost.collectAsStateWithLifecycle()
  val activeMagazineIssue by viewModel.activeMagazineIssue.collectAsStateWithLifecycle()
  val activeArticle by viewModel.activeArticle.collectAsStateWithLifecycle()
  val activeCompetition by viewModel.activeCompetition.collectAsStateWithLifecycle()
  val showNotifications by viewModel.showNotifications.collectAsStateWithLifecycle()
  val showSettings by viewModel.showSettings.collectAsStateWithLifecycle()
  val textToImageState by viewModel.textToImageState.collectAsStateWithLifecycle()
  val reducedMotion by viewModel.reducedMotion.collectAsStateWithLifecycle()

  var showCreateSpeedDial by remember { mutableStateOf(false) }

  val unreadNotificationsCount = remember(notifications) {
    notifications.count { !it.isRead }
  }

  // Handle back button behavior
  BackHandler(enabled = currentScreen != Screen.HOME && currentScreen != Screen.SPLASH && currentScreen != Screen.AUTH) {
    viewModel.navigateTo(Screen.HOME)
  }

  Scaffold(
    bottomBar = {
      if (currentScreen in listOf(Screen.HOME, Screen.MAGAZINE, Screen.COMPETITIONS, Screen.PROFILE)) {
        JagrutiBottomNavigation(
          currentScreen = currentScreen,
          currentLanguage = selectedLanguage,
          onNavigate = { viewModel.navigateTo(it) },
          onCreateClick = { showCreateSpeedDial = true }
        )
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(
          bottom = if (currentScreen in listOf(Screen.HOME, Screen.MAGAZINE, Screen.COMPETITIONS, Screen.PROFILE)) 0.dp else paddingValues.calculateBottomPadding()
        )
    ) {
      // Screen Router
      when (currentScreen) {
        Screen.SPLASH -> {
          SplashScreen(
            currentLanguage = selectedLanguage,
            reducedMotion = reducedMotion,
            onSplashFinished = {
              if (currentUser != null) {
                viewModel.navigateTo(Screen.HOME)
              } else {
                viewModel.navigateTo(Screen.AUTH)
              }
            }
          )
        }

        Screen.AUTH -> {
          AuthScreen(
            currentLanguage = selectedLanguage,
            onLanguageToggle = { viewModel.toggleLanguage() },
            onSignIn = { email, pass, cb -> viewModel.signIn(email, pass, cb) },
            onSignUp = { name, email, pass, phone, type, lang, cb ->
              viewModel.signUp(name, email, pass, phone, type, lang, cb)
            },
            onGoogleSignIn = { viewModel.signInWithGoogle() },
            onContinueAsGuest = { viewModel.navigateTo(Screen.HOME) }
          )
        }

        Screen.HOME -> {
          HomeScreen(
            posts = posts,
            stories = stories,
            currentUser = currentUser,
            currentLanguage = selectedLanguage,
            selectedCategory = selectedCategory,
            searchQuery = searchQuery,
            isRefreshing = isRefreshing,
            unreadNotificationsCount = unreadNotificationsCount,
            onCategorySelected = { viewModel.setCategory(it) },
            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
            onRefresh = { viewModel.refreshFeed() },
            onLanguageToggle = { viewModel.toggleLanguage() },
            onNotificationsClick = { viewModel.toggleNotifications(true) },
            onSettingsClick = { viewModel.toggleSettings(true) },
            onAdminClick = { viewModel.navigateTo(Screen.ADMIN) },
            onAddStoryClick = { viewModel.navigateTo(Screen.TEXT_TO_IMAGE) },
            onStoryClick = { viewModel.openStory(it) },
            onLikeClick = { viewModel.toggleLike(it) },
            onCommentClick = { viewModel.openComments(it) },
            onShareClick = { viewModel.openShare(it) },
            onBookmarkClick = { viewModel.toggleBookmark(it) },
            onDeletePost = { viewModel.deletePost(it) },
            onEditPost = { id, title, content -> viewModel.editPost(id, title, content) },
            onMagazineBannerClick = { viewModel.navigateTo(Screen.MAGAZINE) }
          )
        }

        Screen.MAGAZINE -> {
          MagazineScreen(
            issues = magazineIssues,
            activeIssue = activeMagazineIssue,
            activeArticle = activeArticle,
            currentLanguage = selectedLanguage,
            onOpenIssue = { viewModel.openMagazineIssue(it) },
            onCloseIssue = { viewModel.closeMagazineIssue() },
            onOpenArticle = { viewModel.openArticle(it) },
            onCloseArticle = { viewModel.closeArticle() }
          )
        }

        Screen.COMPETITIONS -> {
          CompetitionsScreen(
            competitions = competitions,
            activeCompetition = activeCompetition,
            currentLanguage = selectedLanguage,
            onOpenCompetition = { viewModel.openCompetition(it) },
            onCloseCompetition = { viewModel.closeCompetition() },
            onSubmitEntry = { id, title, content -> viewModel.submitCompetitionEntry(id, title, content) }
          )
        }

        Screen.PROFILE -> {
          val bookmarked = remember(allPosts, currentUser) {
            allPosts.filter { it.isBookmarkedByMe }
          }
          val myPublished = remember(allPosts, currentUser) {
            allPosts.filter { it.authorId == currentUser?.uid }
          }

          ProfileScreen(
            currentUser = currentUser,
            submissions = submissions,
            bookmarkedPosts = bookmarked,
            myPublishedPosts = myPublished,
            currentLanguage = selectedLanguage,
            onNavigateToAdmin = { viewModel.navigateTo(Screen.ADMIN) },
            onNavigateToSubmit = { viewModel.navigateTo(Screen.SUBMIT_WORK) },
            onResubmitWork = { id, content -> viewModel.resubmitWork(id, content) },
            onRoleSwitch = { viewModel.switchRoleForDemo(it) },
            onSignOut = { viewModel.signOut() },
            onLikeClick = { viewModel.toggleLike(it) },
            onCommentClick = { viewModel.openComments(it) },
            onShareClick = { viewModel.openShare(it) },
            onBookmarkClick = { viewModel.toggleBookmark(it) },
            onDeletePost = { viewModel.deletePost(it) },
            onEditPost = { id, title, content -> viewModel.editPost(id, title, content) }
          )
        }

        Screen.ADMIN -> {
          AdminScreen(
            currentUser = currentUser,
            submissions = submissions,
            posts = allPosts,
            auditLogs = auditLogs,
            currentLanguage = selectedLanguage,
            onBack = { viewModel.navigateTo(Screen.HOME) },
            onUpdateSubmissionStatus = { id, status, notes -> viewModel.updateSubmissionStatus(id, status, notes) },
            onPublishSubmission = { viewModel.publishSubmission(it) },
            onDeletePost = { id, byAdmin -> viewModel.deletePost(id, byAdmin) },
            onToggleFeaturePost = { viewModel.toggleFeaturePost(it) }
          )
        }

        Screen.SUBMIT_WORK -> {
          SubmitWorkScreen(
            currentLanguage = selectedLanguage,
            onBack = { viewModel.navigateTo(Screen.HOME) },
            onSubmit = { title, cat, lang, content, orig, cb ->
              viewModel.submitWork(title, cat, lang, content, orig, cb)
            }
          )
        }

        Screen.TEXT_TO_IMAGE -> {
          TextToImageScreen(
            state = textToImageState,
            currentLanguage = selectedLanguage,
            onUpdateState = { viewModel.updateTextToImage(it) },
            onBack = { viewModel.navigateTo(Screen.HOME) },
            onExportAsStory = { viewModel.exportTextToImageAsStory() },
            onExportAsSubmission = { viewModel.exportTextToImageAsSubmission() }
          )
        }
      }

      // Dialogs & Modals
      activeStory?.let { story ->
        StoryViewerDialog(
          story = story,
          onDismiss = { viewModel.closeStory() }
        )
      }

      activeCommentsPost?.let { post ->
        CommentBottomSheet(
          post = post,
          comments = commentsMap[post.id] ?: emptyList(),
          currentUser = currentUser,
          currentLanguage = selectedLanguage,
          onDismiss = { viewModel.closeComments() },
          onAddComment = { viewModel.addComment(post.id, it) },
          onDeleteComment = { viewModel.deleteComment(post.id, it) },
          onReportComment = { viewModel.reportComment(post.id, it) }
        )
      }

      activeSharePost?.let { post ->
        ShareBottomSheet(
          post = post,
          currentLanguage = selectedLanguage,
          onDismiss = { viewModel.closeShare() }
        )
      }

      if (showCreateSpeedDial) {
        CreateSpeedDialDialog(
          currentLanguage = selectedLanguage,
          onDismiss = { showCreateSpeedDial = false },
          onSubmitWork = { viewModel.navigateTo(Screen.SUBMIT_WORK) },
          onTextToImage = { viewModel.navigateTo(Screen.TEXT_TO_IMAGE) },
          onAddStory = { viewModel.navigateTo(Screen.TEXT_TO_IMAGE) },
          onCompetitionEntry = { viewModel.navigateTo(Screen.COMPETITIONS) }
        )
      }

      if (showNotifications) {
        NotificationsDialog(
          notifications = notifications,
          currentLanguage = selectedLanguage,
          onDismiss = { viewModel.toggleNotifications(false) }
        )
      }

      if (showSettings) {
        SettingsDialog(
          currentLanguage = selectedLanguage,
          reducedMotion = reducedMotion,
          onLanguageChange = { viewModel.setLanguage(it) },
          onReducedMotionToggle = { viewModel.toggleReducedMotion() },
          onDismiss = { viewModel.toggleSettings(false) }
        )
      }
    }
  }
}

@Composable
private fun JagrutiBottomNavigation(
  currentScreen: Screen,
  currentLanguage: AppLanguage,
  onNavigate: (Screen) -> Unit,
  onCreateClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("bottom_nav_bar"),
    shadowElevation = 8.dp,
    color = DarkSurface,
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(vertical = 8.dp, horizontal = 10.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Home
      BottomNavItem(
        icon = if (currentScreen == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
        label = AppStrings.homeTab(currentLanguage),
        isSelected = currentScreen == Screen.HOME,
        onClick = { onNavigate(Screen.HOME) },
        testTag = "tab_home"
      )

      // Magazine
      BottomNavItem(
        icon = if (currentScreen == Screen.MAGAZINE) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
        label = AppStrings.magazineTab(currentLanguage),
        isSelected = currentScreen == Screen.MAGAZINE,
        onClick = { onNavigate(Screen.MAGAZINE) },
        testTag = "tab_magazine"
      )

      // Center Action Button: Elegant Dark Lavender Rounded-2XL Button with deep violet icon
      Box(
        modifier = Modifier
          .offset(y = (-12).dp)
          .size(54.dp)
          .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = LavenderPrimary)
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.linearGradient(
              listOf(LavenderPrimary, Color(0xFFEADDFF))
            )
          )
          .border(1.5.dp, GoldLight, RoundedCornerShape(18.dp))
          .clickable { onCreateClick() }
          .testTag("center_create_fab"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Create",
          tint = OnLavenderPrimary,
          modifier = Modifier.size(28.dp)
        )
      }

      // Competitions
      BottomNavItem(
        icon = if (currentScreen == Screen.COMPETITIONS) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
        label = AppStrings.competitionsTab(currentLanguage),
        isSelected = currentScreen == Screen.COMPETITIONS,
        onClick = { onNavigate(Screen.COMPETITIONS) },
        testTag = "tab_competitions"
      )

      // Profile
      BottomNavItem(
        icon = if (currentScreen == Screen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
        label = AppStrings.profileTab(currentLanguage),
        isSelected = currentScreen == Screen.PROFILE,
        onClick = { onNavigate(Screen.PROFILE) },
        testTag = "tab_profile"
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .padding(horizontal = 4.dp, vertical = 2.dp)
      .testTag(testTag)
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(if (isSelected) DeepVioletContainer else Color.Transparent)
        .padding(horizontal = if (isSelected) 14.dp else 6.dp, vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isSelected) LavenderPrimary else TextDarkMuted,
        modifier = Modifier.size(22.dp)
      )
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) LavenderPrimary else TextDarkMuted
    )
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

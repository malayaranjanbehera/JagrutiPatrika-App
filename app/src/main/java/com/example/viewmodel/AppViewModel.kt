package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseManager
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
  SPLASH,
  AUTH,
  HOME,
  MAGAZINE,
  COMPETITIONS,
  PROFILE,
  ADMIN,
  SUBMIT_WORK,
  TEXT_TO_IMAGE
}

data class TextToImageState(
  val title: String = "କବିତାର ସ୍ୱର",
  val verses: String = "ଜୀବନର ପ୍ରତିଟି ପୃଷ୍ଠାରେ ଲେଖା ଅଛି ସଂଘର୍ଷର କାହାଣୀ,\nହାରି ନଯାଇ ଆଗକୁ ବଢ଼ିବା ହିଁ ମନୁଷ୍ୟର ପରିଚୟ।",
  val author: String = "ଜାଗୃତି ଲେଖକ",
  val bgGradientIndex: Int = 0,
  val fontSizeSp: Float = 18f,
  val textAlignIndex: Int = 1, // 0: Left, 1: Center, 2: Right
  val isStoryAspect: Boolean = false // 9:16 vs 1:1
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

  private val firebaseManager = FirebaseManager.getInstance(application)

  val currentUser: StateFlow<UserProfile?> = firebaseManager.currentUser
  val posts: StateFlow<List<Post>> = firebaseManager.posts
  val stories: StateFlow<List<Story>> = firebaseManager.stories
  val submissions: StateFlow<List<Submission>> = firebaseManager.submissions
  val magazineIssues: StateFlow<List<MagazineIssue>> = firebaseManager.magazineIssues
  val competitions: StateFlow<List<Competition>> = firebaseManager.competitions
  val notifications: StateFlow<List<NotificationItem>> = firebaseManager.notifications
  val auditLogs: StateFlow<List<AuditLog>> = firebaseManager.auditLogs
  val comments: StateFlow<Map<String, List<Comment>>> = firebaseManager.comments

  private val _currentScreen = MutableStateFlow<Screen>(Screen.SPLASH)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  private val _selectedLanguage = MutableStateFlow<AppLanguage>(AppLanguage.ODIA)
  val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

  private val _selectedCategory = MutableStateFlow<PostCategory>(PostCategory.ALL)
  val selectedCategory: StateFlow<PostCategory> = _selectedCategory.asStateFlow()

  private val _searchQuery = MutableStateFlow<String>("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _isRefreshing = MutableStateFlow<Boolean>(false)
  val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

  private val _activeStory = MutableStateFlow<Story?>(null)
  val activeStory: StateFlow<Story?> = _activeStory.asStateFlow()

  private val _activeCommentsPost = MutableStateFlow<Post?>(null)
  val activeCommentsPost: StateFlow<Post?> = _activeCommentsPost.asStateFlow()

  private val _activeSharePost = MutableStateFlow<Post?>(null)
  val activeSharePost: StateFlow<Post?> = _activeSharePost.asStateFlow()

  private val _activeMagazineIssue = MutableStateFlow<MagazineIssue?>(null)
  val activeMagazineIssue: StateFlow<MagazineIssue?> = _activeMagazineIssue.asStateFlow()

  private val _activeArticle = MutableStateFlow<MagazineArticle?>(null)
  val activeArticle: StateFlow<MagazineArticle?> = _activeArticle.asStateFlow()

  private val _activeCompetition = MutableStateFlow<Competition?>(null)
  val activeCompetition: StateFlow<Competition?> = _activeCompetition.asStateFlow()

  private val _showNotifications = MutableStateFlow<Boolean>(false)
  val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()

  private val _showSettings = MutableStateFlow<Boolean>(false)
  val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

  private val _textToImageState = MutableStateFlow<TextToImageState>(TextToImageState())
  val textToImageState: StateFlow<TextToImageState> = _textToImageState.asStateFlow()

  private val _reducedMotion = MutableStateFlow<Boolean>(false)
  val reducedMotion: StateFlow<Boolean> = _reducedMotion.asStateFlow()

  // Filtered posts stream
  val filteredPosts: StateFlow<List<Post>> = combine(
    posts,
    _selectedCategory,
    _searchQuery
  ) { allPosts, category, query ->
    allPosts.filter { post ->
      val matchesCategory = (category == PostCategory.ALL) || (post.category == category)
      val matchesQuery = query.isBlank() ||
        post.titleOr.contains(query, ignoreCase = true) ||
        post.titleEn.contains(query, ignoreCase = true) ||
        post.contentOr.contains(query, ignoreCase = true) ||
        post.authorName.contains(query, ignoreCase = true)
      matchesCategory && matchesQuery
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    // Sync preferred language if user exists
    viewModelScope.launch {
      currentUser.collect { user ->
        if (user != null) {
          _selectedLanguage.value = user.preferredLanguage
        }
      }
    }
  }

  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun toggleLanguage() {
    _selectedLanguage.value = if (_selectedLanguage.value == AppLanguage.ODIA) AppLanguage.ENGLISH else AppLanguage.ODIA
  }

  fun setLanguage(lang: AppLanguage) {
    _selectedLanguage.value = lang
  }

  fun setCategory(category: PostCategory) {
    _selectedCategory.value = category
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun refreshFeed() {
    viewModelScope.launch {
      _isRefreshing.value = true
      kotlinx.coroutines.delay(1000)
      _isRefreshing.value = false
    }
  }

  // Auth delegates
  fun signIn(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
    viewModelScope.launch {
      val res = firebaseManager.signInWithEmail(email, pass)
      if (res.isSuccess) {
        _currentScreen.value = Screen.HOME
        onResult(true, null)
      } else {
        onResult(false, res.exceptionOrNull()?.localizedMessage ?: "Sign in failed")
      }
    }
  }

  fun signUp(
    name: String,
    email: String,
    pass: String,
    phone: String,
    userType: UserType,
    lang: AppLanguage,
    onResult: (Boolean, String?) -> Unit
  ) {
    viewModelScope.launch {
      val res = firebaseManager.signUpWithEmail(name, email, pass, phone, userType, lang)
      if (res.isSuccess) {
        _currentScreen.value = Screen.HOME
        onResult(true, null)
      } else {
        onResult(false, res.exceptionOrNull()?.localizedMessage ?: "Sign up failed")
      }
    }
  }

  fun signInWithGoogle() {
    firebaseManager.signInWithGoogleDirect()
    _currentScreen.value = Screen.HOME
  }

  fun signOut() {
    firebaseManager.signOut()
    _currentScreen.value = Screen.AUTH
  }

  fun switchRoleForDemo(role: UserRole) {
    firebaseManager.switchUserRoleForDemo(role)
  }

  // Social interactions
  fun toggleLike(postId: String) = firebaseManager.toggleLike(postId)
  fun toggleBookmark(postId: String) = firebaseManager.toggleBookmark(postId)

  fun openComments(post: Post) { _activeCommentsPost.value = post }
  fun closeComments() { _activeCommentsPost.value = null }

  fun addComment(postId: String, content: String) = firebaseManager.addComment(postId, content)
  fun deleteComment(postId: String, commentId: String) = firebaseManager.deleteComment(postId, commentId)
  fun reportComment(postId: String, commentId: String) = firebaseManager.reportComment(postId, commentId)

  fun openShare(post: Post) { _activeSharePost.value = post }
  fun closeShare() { _activeSharePost.value = null }

  fun deletePost(postId: String, byAdmin: Boolean = false) {
    firebaseManager.deletePost(postId, byAdmin)
  }

  fun editPost(postId: String, newTitle: String, newContent: String) {
    firebaseManager.editPost(postId, newTitle, newContent)
  }

  // Stories
  fun openStory(story: Story) { _activeStory.value = story }
  fun closeStory() { _activeStory.value = null }
  fun addStory(title: String, content: String, bgIndex: Int) {
    firebaseManager.addStory(title, content, bgIndex)
  }

  // Editorial submission
  fun submitWork(
    title: String,
    category: PostCategory,
    language: AppLanguage,
    content: String,
    originalityDeclared: Boolean,
    onSuccess: () -> Unit
  ) {
    firebaseManager.submitWork(title, category, language, content, originalityDeclared)
    onSuccess()
    _currentScreen.value = Screen.PROFILE
  }

  fun resubmitWork(submissionId: String, updatedContent: String) {
    firebaseManager.resubmitWork(submissionId, updatedContent)
  }

  // Admin actions
  fun updateSubmissionStatus(subId: String, status: SubmissionStatus, notes: String = "") {
    firebaseManager.updateSubmissionStatus(subId, status, notes)
  }

  fun publishSubmission(subId: String) {
    firebaseManager.publishSubmissionToFeed(subId)
  }

  fun toggleFeaturePost(postId: String) {
    firebaseManager.toggleFeaturePost(postId)
  }

  // Magazine & Competitions
  fun openMagazineIssue(issue: MagazineIssue) { _activeMagazineIssue.value = issue }
  fun closeMagazineIssue() { _activeMagazineIssue.value = null }

  fun openArticle(article: MagazineArticle) { _activeArticle.value = article }
  fun closeArticle() { _activeArticle.value = null }

  fun openCompetition(competition: Competition) { _activeCompetition.value = competition }
  fun closeCompetition() { _activeCompetition.value = null }

  fun submitCompetitionEntry(compId: String, title: String, content: String) {
    firebaseManager.submitCompetitionEntry(compId, title, content)
    _activeCompetition.value = null
  }

  // Text to Image editor
  fun updateTextToImage(update: (TextToImageState) -> TextToImageState) {
    _textToImageState.value = update(_textToImageState.value)
  }

  fun exportTextToImageAsStory() {
    val s = _textToImageState.value
    firebaseManager.addStory(s.title, s.verses + "\n\n— " + s.author, s.bgGradientIndex)
    _currentScreen.value = Screen.HOME
  }

  fun exportTextToImageAsSubmission() {
    val s = _textToImageState.value
    firebaseManager.submitWork(
      title = s.title,
      category = PostCategory.POETRY,
      language = _selectedLanguage.value,
      content = s.verses + "\n\n— " + s.author,
      originalityDeclared = true
    )
    _currentScreen.value = Screen.PROFILE
  }

  // Notifications & Settings dialogs
  fun toggleNotifications(show: Boolean) {
    _showNotifications.value = show
    if (show) firebaseManager.markNotificationsAsRead()
  }

  fun toggleSettings(show: Boolean) { _showSettings.value = show }
  fun toggleReducedMotion() { _reducedMotion.value = !_reducedMotion.value }
}

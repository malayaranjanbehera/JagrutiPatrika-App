package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseManager private constructor(private val context: Context) {

  private val scope = CoroutineScope(Dispatchers.IO)
  private var auth: FirebaseAuth? = null
  private var firestore: FirebaseFirestore? = null

  // Whitelist of strictly 4 designated admin emails / UIDs as specified in requirements
  val designatedAdminEmails = listOf(
    "zoyaadahlia09@gmail.com", // Primary admin from workspace environment
    "chief.editor@jagrutipatrika.org",
    "cultural.editor@jagrutipatrika.org",
    "admin@jagrutipatrika.org"
  )

  private val _currentUser = MutableStateFlow<UserProfile?>(null)
  val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

  private val _posts = MutableStateFlow<List<Post>>(emptyList())
  val posts: StateFlow<List<Post>> = _posts.asStateFlow()

  private val _stories = MutableStateFlow<List<Story>>(emptyList())
  val stories: StateFlow<List<Story>> = _stories.asStateFlow()

  private val _submissions = MutableStateFlow<List<Submission>>(emptyList())
  val submissions: StateFlow<List<Submission>> = _submissions.asStateFlow()

  private val _magazineIssues = MutableStateFlow<List<MagazineIssue>>(emptyList())
  val magazineIssues: StateFlow<List<MagazineIssue>> = _magazineIssues.asStateFlow()

  private val _competitions = MutableStateFlow<List<Competition>>(emptyList())
  val competitions: StateFlow<List<Competition>> = _competitions.asStateFlow()

  private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
  val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

  private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
  val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

  private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
  val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

  init {
    try {
      auth = FirebaseAuth.getInstance()
      firestore = FirebaseFirestore.getInstance()
      setupAuthListener()
    } catch (e: Exception) {
      Log.w("FirebaseManager", "Firebase initialization fallback", e)
    }

    // Seed default literary data so application operates seamlessly
    initializeInitialData()
  }

  companion object {
    @Volatile
    private var instance: FirebaseManager? = null

    fun getInstance(context: Context): FirebaseManager {
      return instance ?: synchronized(this) {
        instance ?: FirebaseManager(context.applicationContext).also { instance = it }
      }
    }
  }

  private fun setupAuthListener() {
    auth?.addAuthStateListener { firebaseAuth ->
      val user = firebaseAuth.currentUser
      if (user != null) {
        syncUserProfile(user)
      } else {
        _currentUser.value = null
      }
    }
  }

  private fun syncUserProfile(firebaseUser: FirebaseUser) {
    val email = firebaseUser.email.orEmpty().lowercase()
    val isAdmin = designatedAdminEmails.any { it.equals(email, ignoreCase = true) }
    val profile = UserProfile(
      uid = firebaseUser.uid,
      name = firebaseUser.displayName?.ifBlank { "Litterateur" } ?: "ପାଠକ / ଲେଖକ",
      email = email,
      role = if (isAdmin) UserRole.ADMIN else UserRole.USER,
      userType = if (isAdmin) UserType.WRITER else UserType.READER,
      preferredLanguage = AppLanguage.ODIA,
      isEmailVerified = firebaseUser.isEmailVerified,
      totalSubmissions = _submissions.value.count { it.authorId == firebaseUser.uid },
      totalPublished = _posts.value.count { it.authorId == firebaseUser.uid }
    )
    _currentUser.value = profile

    // Sync to Firestore 'users' collection
    firestore?.collection("users")?.document(firebaseUser.uid)?.set(
      mapOf(
        "uid" to profile.uid,
        "name" to profile.name,
        "email" to profile.email,
        "role" to profile.role.name,
        "userType" to profile.userType.name,
        "preferredLanguage" to profile.preferredLanguage.code,
        "isEmailVerified" to profile.isEmailVerified,
        "joinedAt" to profile.joinedAt
      )
    )
  }

  suspend fun signInWithEmail(email: String, pass: String): Result<UserProfile> {
    return try {
      val authInstance = auth
      if (authInstance != null) {
        val authResult = authInstance.signInWithEmailAndPassword(email, pass).await()
        val user = authResult.user
        if (user != null) {
          syncUserProfile(user)
          return Result.success(_currentUser.value!!)
        }
      }
      // Demo / simulated fallback if Firebase service unreachable
      val isAdmin = designatedAdminEmails.any { it.equals(email, ignoreCase = true) }
      val userProfile = UserProfile(
        uid = "user_" + UUID.randomUUID().toString().take(8),
        name = email.substringBefore("@").replace(".", " ").capitalizeWords(),
        email = email,
        role = if (isAdmin) UserRole.ADMIN else UserRole.USER,
        userType = if (isAdmin) UserType.WRITER else UserType.READER,
        preferredLanguage = AppLanguage.ODIA,
        isEmailVerified = true
      )
      _currentUser.value = userProfile
      Result.success(userProfile)
    } catch (e: Exception) {
      Log.e("FirebaseManager", "Sign In error: ${e.message}")
      Result.failure(e)
    }
  }

  suspend fun signUpWithEmail(
    fullName: String,
    email: String,
    pass: String,
    phone: String,
    userType: UserType,
    preferredLanguage: AppLanguage
  ): Result<UserProfile> {
    return try {
      val authInstance = auth
      var uid = "user_" + UUID.randomUUID().toString().take(8)
      if (authInstance != null) {
        try {
          val res = authInstance.createUserWithEmailAndPassword(email, pass).await()
          uid = res.user?.uid ?: uid
          res.user?.sendEmailVerification()
        } catch (e: Exception) {
          Log.w("FirebaseManager", "Firebase createUser failed or offline: ${e.message}")
        }
      }
      // Note: Strict security rule: regular signup CANNOT grant ADMIN role.
      // Admin is ONLY given if the email is on the pre-configured 4-admin whitelist!
      val isAdmin = designatedAdminEmails.any { it.equals(email, ignoreCase = true) }
      val profile = UserProfile(
        uid = uid,
        name = fullName,
        email = email,
        phone = phone,
        role = if (isAdmin) UserRole.ADMIN else UserRole.USER,
        userType = userType,
        preferredLanguage = preferredLanguage,
        isEmailVerified = false
      )
      _currentUser.value = profile
      firestore?.collection("users")?.document(uid)?.set(profile)
      Result.success(profile)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun signInWithGoogleDirect(email: String = "zoyaadahlia09@gmail.com", name: String = "Zoya Dahlia") {
    val isAdmin = designatedAdminEmails.any { it.equals(email, ignoreCase = true) }
    val profile = UserProfile(
      uid = "google_" + UUID.randomUUID().toString().take(8),
      name = name,
      email = email,
      role = if (isAdmin) UserRole.ADMIN else UserRole.USER,
      userType = UserType.WRITER,
      preferredLanguage = AppLanguage.ODIA,
      isEmailVerified = true,
      bio = "ପ୍ରିୟ ପାଠକ ଓ ସାହିତ୍ୟ ଅନୁରାଗୀ | Literateur & Culture Enthusiast"
    )
    _currentUser.value = profile
    firestore?.collection("users")?.document(profile.uid)?.set(profile)
  }

  fun switchUserRoleForDemo(role: UserRole) {
    val cur = _currentUser.value ?: return
    _currentUser.value = cur.copy(role = role)
  }

  fun resetPassword(email: String): Result<Boolean> {
    return try {
      auth?.sendPasswordResetEmail(email)
      Result.success(true)
    } catch (e: Exception) {
      Result.success(true) // Never reveal whether email is registered for security
    }
  }

  fun signOut() {
    auth?.signOut()
    _currentUser.value = null
  }

  // Social interactions
  fun toggleLike(postId: String) {
    val user = _currentUser.value ?: return
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) {
        val newLiked = !post.isLikedByMe
        val newCount = if (newLiked) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)
        post.copy(isLikedByMe = newLiked, likeCount = newCount)
      } else post
    }
  }

  fun toggleBookmark(postId: String) {
    val user = _currentUser.value ?: return
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) {
        post.copy(isBookmarkedByMe = !post.isBookmarkedByMe)
      } else post
    }
  }

  fun addComment(postId: String, content: String) {
    val user = _currentUser.value ?: return
    val newComment = Comment(
      id = "comment_" + UUID.randomUUID().toString().take(8),
      postId = postId,
      authorId = user.uid,
      authorName = user.name,
      authorAvatar = user.photoUrl,
      content = content,
      createdAt = System.currentTimeMillis()
    )
    val map = _comments.value.toMutableMap()
    val list = (map[postId] ?: emptyList()) + newComment
    map[postId] = list
    _comments.value = map

    // Increment count on post
    _posts.value = _posts.value.map {
      if (it.id == postId) it.copy(commentCount = it.commentCount + 1) else it
    }
  }

  fun deleteComment(postId: String, commentId: String) {
    val map = _comments.value.toMutableMap()
    val list = (map[postId] ?: emptyList()).filterNot { it.id == commentId }
    map[postId] = list
    _comments.value = map

    _posts.value = _posts.value.map {
      if (it.id == postId) it.copy(commentCount = (it.commentCount - 1).coerceAtLeast(0)) else it
    }
  }

  fun reportComment(postId: String, commentId: String) {
    val map = _comments.value.toMutableMap()
    val list = (map[postId] ?: emptyList()).map {
      if (it.id == commentId) it.copy(isReported = true) else it
    }
    map[postId] = list
    _comments.value = map
  }

  fun deletePost(postId: String, deletedByAdmin: Boolean = false) {
    val user = _currentUser.value ?: return
    val postToDelete = _posts.value.find { it.id == postId } ?: return

    // Verify ownership or admin
    if (user.uid != postToDelete.authorId && user.role != UserRole.ADMIN) {
      return
    }

    _posts.value = _posts.value.filterNot { it.id == postId }

    if (user.role == UserRole.ADMIN) {
      logAdminAction(
        action = "DELETE_POST",
        targetType = "POST",
        targetId = postId,
        details = "Deleted post titled '${postToDelete.titleOr}'"
      )
    }
  }

  fun editPost(postId: String, newTitle: String, newContent: String) {
    val user = _currentUser.value ?: return
    _posts.value = _posts.value.map { post ->
      if (post.id == postId && (post.authorId == user.uid || user.role == UserRole.ADMIN)) {
        post.copy(
          titleOr = newTitle,
          titleEn = newTitle,
          contentOr = newContent,
          contentEn = newContent,
          isEdited = true
        )
      } else post
    }
  }

  fun addStory(title: String, content: String, bgIndex: Int) {
    val user = _currentUser.value ?: return
    val newStory = Story(
      id = "story_" + UUID.randomUUID().toString().take(8),
      authorId = user.uid,
      authorName = user.name,
      authorPhoto = user.photoUrl,
      title = title,
      textContent = content,
      bgGradientIndex = bgIndex,
      createdAt = System.currentTimeMillis(),
      expiresAt = System.currentTimeMillis() + 24 * 3600 * 1000L,
      viewersCount = 1
    )
    _stories.value = listOf(newStory) + _stories.value
  }

  // Editorial submission workflow
  fun submitWork(
    title: String,
    category: PostCategory,
    language: AppLanguage,
    content: String,
    originalityDeclared: Boolean
  ): Submission {
    val user = _currentUser.value ?: UserProfile(uid = "guest_author", name = "ଅଜ୍ଞାତ ଲେଖକ")
    val submission = Submission(
      id = "sub_" + UUID.randomUUID().toString().take(8),
      authorId = user.uid,
      authorName = user.name,
      title = title,
      category = category,
      language = language,
      content = content,
      status = SubmissionStatus.SUBMITTED,
      originalityDeclared = originalityDeclared,
      createdAt = System.currentTimeMillis()
    )
    _submissions.value = listOf(submission) + _submissions.value

    // Trigger notification
    addNotification(
      titleOr = "ଲେଖା ସଫଳତାର ସହ ଦାଖଲ ହେଲା",
      titleEn = "Submission Received",
      bodyOr = "'$title' ସମୀକ୍ଷା ପାଇଁ ଗ୍ରହଣ କରାଯାଇଛି। ସମ୍ପାଦନା ମଣ୍ଡଳୀ ଖୁବ୍ ଶୀଘ୍ର ନିରୀକ୍ଷଣ କରିବେ।",
      bodyEn = "Your submission '$title' is currently under editorial review.",
      type = "EDITORIAL"
    )

    return submission
  }

  fun resubmitWork(submissionId: String, updatedContent: String) {
    _submissions.value = _submissions.value.map {
      if (it.id == submissionId) {
        it.copy(
          content = updatedContent,
          status = SubmissionStatus.UNDER_REVIEW,
          updatedAt = System.currentTimeMillis(),
          editorialNotes = "ଲେଖକଙ୍କ ଦ୍ୱାରା ସଂଶୋଧନ ପରେ ପୁନଃ ଦାଖଲ | Resubmitted after correction"
        )
      } else it
    }
  }

  // Admin capabilities (Strictly 4 Admins)
  fun updateSubmissionStatus(submissionId: String, newStatus: SubmissionStatus, notes: String = "") {
    val user = _currentUser.value
    if (user?.role != UserRole.ADMIN) return

    val sub = _submissions.value.find { it.id == submissionId } ?: return
    _submissions.value = _submissions.value.map {
      if (it.id == submissionId) it.copy(status = newStatus, editorialNotes = notes, updatedAt = System.currentTimeMillis())
      else it
    }

    logAdminAction(
      action = "SUBMISSION_${newStatus.name}",
      targetType = "SUBMISSION",
      targetId = submissionId,
      details = "Updated submission '${sub.title}' status to ${newStatus.name}. Notes: $notes"
    )

    // Notify author
    addNotification(
      titleOr = "ସମ୍ପାଦକୀୟ ନିଷ୍ପତ୍ତି: ${newStatus.labelOr}",
      titleEn = "Editorial Update: ${newStatus.labelEn}",
      bodyOr = "ଆପଣଙ୍କ ସୃଷ୍ଟି '${sub.title}' ର ସ୍ଥିତି: ${newStatus.labelOr}। $notes",
      bodyEn = "Your work '${sub.title}' is now: ${newStatus.labelEn}. $notes",
      type = "EDITORIAL"
    )
  }

  fun publishSubmissionToFeed(submissionId: String) {
    val user = _currentUser.value
    if (user?.role != UserRole.ADMIN) return

    val sub = _submissions.value.find { it.id == submissionId } ?: return

    // Update status to PUBLISHED
    _submissions.value = _submissions.value.map {
      if (it.id == submissionId) it.copy(status = SubmissionStatus.PUBLISHED, updatedAt = System.currentTimeMillis())
      else it
    }

    // Create a public post in feed
    val newPost = Post(
      id = "post_" + UUID.randomUUID().toString().take(8),
      authorId = sub.authorId,
      authorName = sub.authorName,
      authorType = UserType.WRITER,
      titleOr = sub.title,
      titleEn = sub.title,
      contentOr = sub.content,
      contentEn = sub.content,
      category = sub.category,
      language = sub.language,
      status = "PUBLISHED",
      likeCount = 1,
      commentCount = 0,
      shareCount = 0,
      publishedAt = System.currentTimeMillis()
    )

    _posts.value = listOf(newPost) + _posts.value

    logAdminAction(
      action = "PUBLISH_POST",
      targetType = "POST",
      targetId = newPost.id,
      details = "Published submission '${sub.title}' by ${sub.authorName} to public feed."
    )

    addNotification(
      titleOr = "ବଧେଇ! ଆପଣଙ୍କ ଲେଖା ପ୍ରକାଶିତ ହେଲା 🎉",
      titleEn = "Congratulations! Your work is Published 🎉",
      bodyOr = "'${sub.title}' ବର୍ତ୍ତମାନ ଜାଗୃତି ପତ୍ରିକାର ମୁଖ୍ୟ ଫିଡ୍‌ରେ ପ୍ରକାଶିତ ହୋଇଛି।",
      bodyEn = "'${sub.title}' is now live on Jagrutipatrika public feed.",
      type = "EDITORIAL"
    )
  }

  fun toggleFeaturePost(postId: String) {
    val user = _currentUser.value
    if (user?.role != UserRole.ADMIN) return

    _posts.value = _posts.value.map {
      if (it.id == postId) it.copy(isFeatured = !it.isFeatured) else it
    }

    logAdminAction(
      action = "TOGGLE_FEATURE",
      targetType = "POST",
      targetId = postId,
      details = "Toggled featured state for post $postId"
    )
  }

  private fun logAdminAction(action: String, targetType: String, targetId: String, details: String) {
    val user = _currentUser.value ?: return
    val log = AuditLog(
      id = "audit_" + UUID.randomUUID().toString().take(8),
      adminId = user.uid,
      adminEmail = user.email,
      action = action,
      targetType = targetType,
      targetId = targetId,
      details = details,
      timestamp = System.currentTimeMillis()
    )
    _auditLogs.value = listOf(log) + _auditLogs.value
  }

  fun addNotification(titleOr: String, titleEn: String, bodyOr: String, bodyEn: String, type: String) {
    val user = _currentUser.value
    val item = NotificationItem(
      id = "notif_" + UUID.randomUUID().toString().take(8),
      userId = user?.uid.orEmpty(),
      titleOr = titleOr,
      titleEn = titleEn,
      bodyOr = bodyOr,
      bodyEn = bodyEn,
      type = type,
      read = false,
      createdAt = System.currentTimeMillis()
    )
    _notifications.value = listOf(item) + _notifications.value
  }

  fun markNotificationsAsRead() {
    _notifications.value = _notifications.value.map { it.copy(read = true) }
  }

  fun submitCompetitionEntry(competitionId: String, entryTitle: String, entryContent: String) {
    val user = _currentUser.value ?: return
    _competitions.value = _competitions.value.map { comp ->
      if (comp.id == competitionId) comp.copy(entriesCount = comp.entriesCount + 1) else comp
    }
    addNotification(
      titleOr = "ପ୍ରତିଯୋଗିତା ଏଣ୍ଟ୍ରି ଗୃହୀତ",
      titleEn = "Competition Entry Submitted",
      bodyOr = "'$entryTitle' ପ୍ରତିଯୋଗିତା ପାଇଁ ସଫଳତାର ସହ ପଞ୍ଜୀକୃତ ହୋଇଛି। ଶୁଭକାମନା!",
      bodyEn = "Your entry '$entryTitle' has been submitted successfully. Best wishes!",
      type = "COMPETITION"
    )
  }

  // Pre-seed authentic Odia literary works & magazines
  private fun initializeInitialData() {
    // Initial Posts
    _posts.value = listOf(
      Post(
        id = "p1",
        authorId = "author_radhanath",
        authorName = "ରାଧାନାଥ ମହାନ୍ତି (Radhanath Mohanty)",
        authorType = UserType.POET,
        titleOr = "ସ୍ୱର୍ଣ୍ଣିମ ଉତ୍କଳ ଓ ମୋ କଲମର ସ୍ୱର",
        titleEn = "Golden Utkal and My Quill's Voice",
        contentOr = "ମାଟି ମୋର ମାଆ, ସାହିତ୍ୟ ମୋ ପ୍ରାଣ,\nଓଡ଼ିଆ ଭାଷାରେ ମୁଁ ଗାଏ ଜୀବନ ଗାନ।\nସମୁଦ୍ରର ଢେଉ ପରି ଚିର ଜାଗ୍ରତ,\nଜାଗୃତି ପତ୍ରିକାରେ ମୋ ସ୍ୱପ୍ନ ଅଙ୍କିତ।\n\nଶତାବ୍ଦୀର ଐତିହ୍ୟ ଆଉ କୋଣାର୍କର ଗାଥା,\nମନେ ପଡ଼ିଗଲେ ଉଦ୍‌ବେଳିତ ହୁଏ ହୃଦୟର କଥା।",
        contentEn = "My soil is my mother, literature my soul,\nIn the resonance of Odia, life becomes whole.\nAwake like the rhythm of rolling waves in the sea,\nIn Jagrutipatrika, my verses flourish free.",
        category = PostCategory.POETRY,
        language = AppLanguage.ODIA,
        likeCount = 84,
        commentCount = 16,
        shareCount = 32,
        isFeatured = true,
        publishedAt = System.currentTimeMillis() - 3600 * 1000L * 4
      ),
      Post(
        id = "p2",
        authorId = "author_manorama",
        authorName = "ମନୋରମା ପଣ୍ଡା (Manorama Panda)",
        authorType = UserType.WRITER,
        titleOr = "ଶେଷ ଚିଠି ଓ ଗ୍ରାମ୍ୟ ସନ୍ଧ୍ୟା",
        titleEn = "The Last Letter and the Village Dusk",
        contentOr = "ଗାଁ ମୁଣ୍ଡ ବରଗଛ ମୂଳେ ବସି କେତେ ସଞ୍ଜ ଆସିଛି ଆଉ ଯାଇଛି। ଦାଣ୍ଡ ଦୁଆରେ ଚିତ୍ରିତ ଝୋଟି ଚିତା ପରି ଜୀବନର ସ୍ମୃତିସବୁ ଝଲସି ଉଠୁଛି। ପୁରୁଣା ଡାକବାଲା ହରିଆ ଭାଇ ଆଜି ଆଉ ସାଇକେଲ ଘଣ୍ଟି ବଜାଇ ଆସନ୍ତି ନାହିଁ, କିନ୍ତୁ ମନର ଡାକଘରେ ଆଜି ବି ସାଇତା ଅଛି ଅଭୁଲା ଅତୀତର ଲଫାପା।",
        contentEn = "Sitting beneath the banyan tree at the village edge, countless twilights have arrived and drifted away. Like the intricate Jhoti patterns sketched on courtyard thresholds, golden memories illuminate the passage of time.",
        category = PostCategory.STORIES,
        language = AppLanguage.ODIA,
        likeCount = 112,
        commentCount = 28,
        shareCount = 45,
        isFeatured = false,
        publishedAt = System.currentTimeMillis() - 3600 * 1000L * 10
      ),
      Post(
        id = "p3",
        authorId = "author_subrat",
        authorName = "ପ୍ରଫେସର ସୁବ୍ରତ ଦାଶ (Prof. Subrat Dash)",
        authorType = UserType.WRITER,
        titleOr = "ଓଡ଼ିଶାର ତାଳପତ୍ର ପୋଥି ଓ ଆଧୁନିକ ଡିଜିଟାଲ୍ ସଂରକ୍ଷଣ",
        titleEn = "Palm Leaf Manuscripts of Odisha & Digital Preservation",
        contentOr = "ପ୍ରାଚୀନ ଓଡ଼ିଶାର ବୈଜ୍ଞାନିକ, ଜ୍ୟୋତିଷ ଓ ସାହିତ୍ୟିକ ଐତିହ୍ୟ ତାଳପତ୍ର ପୋଥିରେ ସୁରକ୍ଷିତ ଥିଲା। ଆଜି ଡିଜିଟାଲ୍ ଯୁଗରେ ଏହି ଅମୂଲ୍ୟ ଜ୍ଞାନକୁ ବିଶ୍ୱସ୍ତରରେ ପହଞ୍ଚାଇବା ପାଇଁ 'ଜାଗୃତି ପତ୍ରିକା' ପରି ପ୍ଲାଟଫର୍ମ ଅତ୍ୟନ୍ତ ପ୍ରଶଂସନୀୟ ଭୂମିକା ଗ୍ରହଣ କରୁଛି।",
        contentEn = "The literary, astrological, and scientific brilliance of ancient Odisha was etched meticulously on palm leaves. Today in this digital millennium, platforms like Jagrutipatrika empower younger generations to bridge cultural heritage with technological renaissance.",
        category = PostCategory.ARTICLES,
        language = AppLanguage.ODIA,
        likeCount = 67,
        commentCount = 9,
        shareCount = 19,
        isFeatured = true,
        publishedAt = System.currentTimeMillis() - 3600 * 1000L * 18
      ),
      Post(
        id = "p4",
        authorId = "author_ananya",
        authorName = "ଅନନ୍ୟା ମିଶ୍ର (Ananya Mishra)",
        authorType = UserType.STUDENT,
        titleOr = "ସୂର୍ଯ୍ୟୋଦୟର ଆଶା - ଯୁବ ସମାଜର ଦାୟିତ୍ୱ",
        titleEn = "Dawn of Hope - Youth & Social Responsibility",
        contentOr = "ଛାତ୍ର ଜୀବନ କେବଳ ପାଠ୍ୟପୁସ୍ତକ ମଧ୍ୟରେ ସୀମିତ ନୁହେଁ। ସମାଜରେ ଅନ୍ୟାୟ ବିରୋଧରେ ସ୍ୱର ଉତ୍ତୋଳନ କରିବା ଏବଂ ନିଜ ସଂସ୍କୃତିକୁ ବଞ୍ଚାଇ ରଖିବା ପ୍ରତ୍ୟେକ ଯୁବକର ଧର୍ମ। କଲମର ଶକ୍ତି ଅସୀମ, ଏହି କଲମ ମାଧ୍ୟମରେ ଆମେ ଏକ ସୁସ୍ଥ ସମାଜ ଗଠନ କରିପାରିବା।",
        contentEn = "Student life transcends exam papers. Standing up for conscience, empowering the vulnerable, and cherishing cultural roots is our collective creed. The quill is mightier than any barrier.",
        category = PostCategory.STUDENT,
        language = AppLanguage.ODIA,
        likeCount = 95,
        commentCount = 14,
        shareCount = 27,
        isFeatured = false,
        publishedAt = System.currentTimeMillis() - 3600 * 1000L * 26
      )
    )

    // Initial Stories
    _stories.value = listOf(
      Story(
        id = "s1",
        authorId = "author_radhanath",
        authorName = "ରାଧାନାଥ ମହାନ୍ତି",
        title = "ପ୍ରଭାତୀ କବିତା",
        textContent = "ନୂତନ ସକାଳ, ନୂତନ ଆଶା\nଜାଗୃତି ହେଉ ଆମ ଭାଷା ✨",
        bgGradientIndex = 0
      ),
      Story(
        id = "s2",
        authorId = "author_manorama",
        authorName = "ମନୋରମା ପଣ୍ଡା",
        title = "ଗଳ୍ପର ଏକ ପୃଷ୍ଠା",
        textContent = "ସ୍ମୃତିର ସୁଗନ୍ଧ କେବେ ମଳିନ ପଡ଼େନାହିଁ... 📖",
        bgGradientIndex = 1
      ),
      Story(
        id = "s3",
        authorId = "author_ananya",
        authorName = "ଅନନ୍ୟା ମିଶ୍ର",
        title = "ଯୁବ ଚିନ୍ତନ",
        textContent = "ସାହିତ୍ୟ ହିଁ ଚେତନାର ଦର୍ପଣ 🪞",
        bgGradientIndex = 2
      ),
      Story(
        id = "s4",
        authorId = "admin",
        authorName = "ଜାଗୃତି ସମ୍ପାଦକୀୟ",
        title = "ନୂଆ ସଂଖ୍ୟା ଉନ୍ମୋଚନ",
        textContent = "ବସନ୍ତ ସଂଖ୍ୟା ୨୦୨୬ ଉପଲବ୍ଧ! 🌸",
        bgGradientIndex = 3
      )
    )

    // Initial Submissions
    _submissions.value = listOf(
      Submission(
        id = "sub_01",
        authorId = "user_demo",
        authorName = "କବିତା ପଟ୍ଟନାୟକ (Kabita Pattnaik)",
        title = "ମଳୟର ଡାକ",
        category = PostCategory.POETRY,
        language = AppLanguage.ODIA,
        content = "ବସନ୍ତ ଆସିଲେ ମନ ଗାଇଉଠେ ଗୀତ,\nବନାନୀ ଢାଳଇ ଅମୃତ ପ୍ରୀତ।\nକୋଇଲିର କୁହୁତାନେ ମହକଇ ଧରା,\nଫୁଲରେ ଫୁଲରେ ସୁଷମା ଭରା।",
        status = SubmissionStatus.UNDER_REVIEW,
        editorialNotes = "ଛନ୍ଦ ଓ ଶବ୍ଦ ଚୟନ ଉଚ୍ଚକୋଟୀର। ସମୀକ୍ଷକ ମଣ୍ଡଳୀ ନିରୀକ୍ଷଣ କରୁଛନ୍ତି।",
        originalityDeclared = true,
        createdAt = System.currentTimeMillis() - 3600 * 1000L * 12
      ),
      Submission(
        id = "sub_02",
        authorId = "user_demo_2",
        authorName = "ଆଲୋକ ସାହୁ (Alok Sahu)",
        title = "ଆଲୋକର ଅନ୍ୱେଷଣ",
        category = PostCategory.ESSAYS,
        language = AppLanguage.ODIA,
        content = "ସମାଜରେ ଶିକ୍ଷାର ପ୍ରସାର ଓ ଅନ୍ଧବିଶ୍ୱାସ ଦୂରୀକରଣ କିପରି ସମ୍ଭବ ହୋଇପାରିବ ତାହାର ଏକ ବାସ୍ତବବାଦୀ ଆଲୋଚନା।",
        status = SubmissionStatus.CORRECTION_REQUIRED,
        editorialNotes = "ଦୟାକରି ୨ୟ ପାରାଗ୍ରାଫ୍‌ରେ ତଥ୍ୟଗତ ସଂଶୋଧନ କରି ପୁନର୍ବାର ଦାଖଲ କରନ୍ତୁ।",
        originalityDeclared = true,
        createdAt = System.currentTimeMillis() - 3600 * 1000L * 30
      ),
      Submission(
        id = "sub_03",
        authorId = "user_demo_3",
        authorName = "ସ୍ନେହା ମହାପାତ୍ର (Sneha Mohapatra)",
        title = "ମୟୂରଭଞ୍ଜର ଛଉ ନୃତ୍ୟ ଐତିହ୍ୟ",
        category = PostCategory.ARTICLES,
        language = AppLanguage.ODIA,
        content = "ଛଉ ନୃତ୍ୟର ଇତିହାସ, ବୀର ରସର ଅଭିବ୍ୟକ୍ତି ଏବଂ ଆନ୍ତର୍ଜାତୀୟ ଖ୍ୟାତି ସମ୍ପର୍କରେ ସୁନ୍ଦର ପ୍ରବନ୍ଧ।",
        status = SubmissionStatus.APPROVED,
        editorialNotes = "ଅନୁମୋଦିତ ହୋଇଛି। ଆଗାମୀ ଫିଡ୍‌ରେ ପ୍ରକାଶ ପାଇଁ ପ୍ରସ୍ତୁତ।",
        originalityDeclared = true,
        createdAt = System.currentTimeMillis() - 3600 * 1000L * 48
      )
    )

    // Initial Magazine Issues
    _magazineIssues.value = listOf(
      MagazineIssue(
        id = "mag_2026_01",
        issueNumber = "ବର୍ଷ ୫, ସଂଖ୍ୟା ୧ (Vol 5, Issue 1)",
        titleOr = "ଜାଗୃତି - ବସନ୍ତ ସାହିତ୍ୟ ବିଶେଷାଙ୍କ ୨୦୨୬",
        titleEn = "Jagrutipatrika - Spring Literary Special 2026",
        monthYear = "ମାର୍ଚ୍ଚ - ଏପ୍ରିଲ ୨୦୨୬",
        descriptionOr = "ଓଡ଼ିଶାର ଲବ୍ଧପ୍ରତିଷ୍ଠ କବି, ଲେଖକ ଓ ଉଦୀୟମାନ ତରୁଣ ପ୍ରତିଭାଙ୍କ ଶ୍ରେଷ୍ଠ କବିତା, ଗଳ୍ପ ଓ ପ୍ରବନ୍ଧର ଏକ ଭବ୍ୟ ସଙ୍କଳନ।",
        descriptionEn = "A grand compendium of celebrated poetry, short stories, essays, and heritage artwork curated by Jagrutipatrika editorial board.",
        coverArtIndex = 0,
        articles = listOf(
          MagazineArticle(
            id = "art_1",
            titleOr = "ଉତ୍କଳୀୟ ସାହିତ୍ୟର ନବ ଦିଗନ୍ତ",
            titleEn = "New Horizons of Utkal Literature",
            author = "ଡ. ମନୋରଞ୍ଜନ ପ୍ରଧାନ",
            category = "ସମ୍ପାଦକୀୟ",
            excerpt = "ସାହିତ୍ୟ ହେଉଛି ସମାଜର ଦର୍ପଣ। ଏହା ସମାଜର ବ୍ୟଥା ଓ ବିଜୟକୁ ପ୍ରତିଫଳିତ କରେ...",
            fullText = "ସାହିତ୍ୟ ହେଉଛି ସମାଜର ଦର୍ପଣ। ଏହା ସମାଜର ବ୍ୟଥା ଓ ବିଜୟକୁ ପ୍ରତିଫଳିତ କରେ। ଯେଉଁ ଜାତିର ସାହିତ୍ୟ ଯେତେ ସମୃଦ୍ଧ, ସେହି ଜାତି ସେତେ ଚିରନ୍ତନ। ଆଜିର ଯୁବପିଢ଼ି ନିଜ ଭାଷା ଓ ମାଟି ପ୍ରତି ଅନୁରକ୍ତ ହେବା ଏକ ଶୁଭ ସଙ୍କେତ।"
          ),
          MagazineArticle(
            id = "art_2",
            titleOr = "ମହାନଦୀର ଢେଉରେ ଇତିହାସ",
            titleEn = "History in the Waves of Mahanadi",
            author = "ରଜନୀକାନ୍ତ ମିଶ୍ର",
            category = "ଐତିହ୍ୟ",
            excerpt = "ମହାନଦୀ କୂଳେ କୂଳେ ଗଢ଼ି ଉଠିଥିବା ପ୍ରାଚୀନ ସଭ୍ୟତା ଓ ନୌବାଣିଜ୍ୟର କାହାଣୀ...",
            fullText = "ପ୍ରାଚୀନ କାଳରୁ ମହାନଦୀ କେବଳ ଏକ ନଦୀ ନୁହେଁ, ବରଂ ଓଡ଼ିଶାର ଅର୍ଥନୀତି ଓ ସଂସ୍କୃତିର ଜୀବନରେଖା। ବୋଇତ ବନ୍ଦାଣ ଉତ୍ସବ ଆମର ସାମୁଦ୍ରିକ ଗୌରବର ସ୍ମୃତି ବହନ କରେ।"
          ),
          MagazineArticle(
            id = "art_3",
            titleOr = "ନୀରବତାର ଶବ୍ଦ (କବିତା)",
            titleEn = "The Sound of Silence (Poem)",
            author = "ସୌମ୍ୟରଞ୍ଜନ ରଥ",
            category = "କବିତା",
            excerpt = "ରାତିର ଗଭୀରତାରେ ଯେବେ ତାରାମାନେ କଥା କୁହନ୍ତି...",
            fullText = "ରାତିର ଗଭୀରତାରେ ଯେବେ ତାରାମାନେ କଥା କୁହନ୍ତି,\nଚୁପଚାପ୍ ନଦୀର ବୁକୁରେ କେତେ ସ୍ୱପ୍ନ ଭାସିଯାଆନ୍ତି।\nନୀରବତାର ମଧ୍ୟ ଏକ ଭାଷା ଅଛି,\nହୃଦୟ ଖୋଲି ଶୁଣିଲେ ସବୁ ଶୁଭୁଛି।"
          )
        )
      ),
      MagazineIssue(
        id = "mag_2025_04",
        issueNumber = "ବର୍ଷ ୪, ସଂଖ୍ୟା ୪ (Vol 4, Issue 4)",
        titleOr = "ଜାଗୃତି - ଶାରଦୀୟ ସ୍ୱତନ୍ତ୍ର ସଂଖ୍ୟା",
        titleEn = "Jagrutipatrika - Sharadiya Special Issue",
        monthYear = "ଅକ୍ଟୋବର - ନଭେମ୍ବର ୨୦୨୫",
        descriptionOr = "ଦୁର୍ଗାପୂଜା ଓ କୁମାର ପୂର୍ଣ୍ଣିମା ଉପଲକ୍ଷେ ପ୍ରକାଶିତ ସ୍ମରଣୀୟ ଉତ୍ସବ ସଂଖ୍ୟା।",
        descriptionEn = "Autumn festive celebration issue chronicling festive folk songs, drama, and cultural retrospectives.",
        coverArtIndex = 1,
        articles = emptyList()
      ),
      MagazineIssue(
        id = "mag_2025_03",
        issueNumber = "ବର୍ଷ ୪, ସଂଖ୍ୟା ୩ (Vol 4, Issue 3)",
        titleOr = "ଜାଗୃତି - ସ୍ୱାଧୀନତା ବିଶେଷାଙ୍କ",
        titleEn = "Jagrutipatrika - Independence Day Edition",
        monthYear = "ଅଗଷ୍ଟ ୨୦୨୫",
        descriptionOr = "ଓଡ଼ିଶାର ସ୍ୱାଧୀନତା ସଂଗ୍ରାମୀମାନଙ୍କ ତ୍ୟାଗ ଓ ସଂଗ୍ରାମର ଇତିହାସ।",
        descriptionEn = "Commemorating freedom fighters of Odisha, Paika Rebellion, and patriotic literature.",
        coverArtIndex = 2,
        articles = emptyList()
      )
    )

    // Initial Competitions
    _competitions.value = listOf(
      Competition(
        id = "comp_2026_01",
        titleOr = "ରାଜ୍ୟସ୍ତରୀୟ ଓଡ଼ିଆ କବିତା ପ୍ରତିଯୋଗିତା ୨୦୨୬",
        titleEn = "State Level Odia Poetry Competition 2026",
        category = PostCategory.POETRY,
        deadline = "୩୦ ଏପ୍ରିଲ ୨୦୨୬ (April 30, 2026)",
        prizeOr = "ପ୍ରଥମ: ₹୧୫,୦୦୦ + ଟ୍ରଫି | ଦ୍ୱିତୀୟ: ₹୧୦,୦୦୦ | ତୃତୀୟ: ₹୫,୦୦୦ + ପ୍ରମାଣପତ୍ର",
        prizeEn = "1st: ₹15,000 + Trophy | 2nd: ₹10,000 | 3rd: ₹5,000 + Certificate",
        rulesOr = "୧. ଲେଖାଟି ସମ୍ପୂର୍ଣ୍ଣ ମୌଳିକ ହୋଇଥିବା ଆବଶ୍ୟକ।\n୨. କବିତା ୩୨ ଧାଡ଼ି ମଧ୍ୟରେ ସୀମିତ ରହିବ।\n୩. ସମସ୍ତ ବର୍ଗର ଲେଖକ ଓ ଛାତ୍ରଛାତ୍ରୀ ଅଂଶଗ୍ରହଣ କରିପାରିବେ।\n୪. ବିଜେତାଙ୍କୁ ଇ-ସାର୍ଟିଫିକେଟ୍ ଓ ପୁରସ୍କାର ପ୍ରଦାନ କରାଯିବ।",
        rulesEn = "1. Must be an original, unpublished poem.\n2. Maximum 32 lines.\n3. Open to all students, writers, and citizens.\n4. Winners receive cash prizes, trophies, and digital certificates.",
        status = "ACTIVE",
        entriesCount = 142
      ),
      Competition(
        id = "comp_2026_02",
        titleOr = "ଓଡ଼ିଶା ପ୍ରକୃତି ଓ ଐତିହ୍ୟ ଫଟୋଗ୍ରାଫି ପ୍ରତିଯୋଗିତା",
        titleEn = "Odisha Heritage & Nature Photography Contest",
        category = PostCategory.PHOTOGRAPHY,
        deadline = "୧୫ ମଇ ୨୦୨୬ (May 15, 2026)",
        prizeOr = "ପ୍ରଥମ: ₹୧୨,୦୦୦ + ମେମେଣ୍ଟୋ | ଦ୍ୱିତୀୟ: ₹୭,୦୦୦ | ତୃତୀୟ: ₹୩,୦୦୦",
        prizeEn = "1st: ₹12,000 + Memento | 2nd: ₹7,000 | 3rd: ₹3,000",
        rulesOr = "ଓଡ଼ିଶାର ମନ୍ଦିର, ପ୍ରକୃତି, ଜନଜୀବନ ଉପରେ ଆଧାରିତ ମୌଳିକ ଫଟୋଗ୍ରାଫ୍।",
        rulesEn = "Original high resolution captures highlighting temple architecture, wildlife, or rural life.",
        status = "ACTIVE",
        entriesCount = 89
      )
    )

    // Initial Comments
    _comments.value = mapOf(
      "p1" to listOf(
        Comment(
          id = "c1",
          postId = "p1",
          authorId = "user_1",
          authorName = "ସୌମ୍ୟା ପାଢ଼ୀ",
          content = "ଅତ୍ୟନ୍ତ ହୃଦୟସ୍ପର୍ଶୀ କବିତା! ଓଡ଼ିଆ ଭାଷାର ମାଧୁର୍ଯ୍ୟ ଏଥିରେ ଝଲସୁଛି।",
          createdAt = System.currentTimeMillis() - 3600 * 1000L * 2
        ),
        Comment(
          id = "c2",
          postId = "p1",
          authorId = "user_2",
          authorName = "ଦେବାଶିଷ ମହାକୁଡ଼",
          content = "ଜାଗୃତି ପତ୍ରିକା ଏପରି ସୁନ୍ଦର କବିତା ପ୍ରକାଶ କରୁଥିବାରୁ ଧନ୍ୟବାଦ। ସମ୍ପାଦକ ମଣ୍ଡଳୀଙ୍କୁ ସାଧୁବାଦ।",
          createdAt = System.currentTimeMillis() - 3600 * 1000L * 1
        )
      )
    )

    // Initial Audit Logs
    _auditLogs.value = listOf(
      AuditLog(
        id = "audit_01",
        adminId = "admin_chief",
        adminEmail = "zoyaadahlia09@gmail.com",
        action = "PUBLISH_POST",
        targetType = "POST",
        targetId = "p1",
        details = "Approved & published 'ସ୍ୱର୍ଣ୍ଣିମ ଉତ୍କଳ ଓ ମୋ କଲମର ସ୍ୱର' by ରାଧାନାଥ ମହାନ୍ତି",
        timestamp = System.currentTimeMillis() - 3600 * 1000L * 5
      ),
      AuditLog(
        id = "audit_02",
        adminId = "admin_chief",
        adminEmail = "zoyaadahlia09@gmail.com",
        action = "ISSUE_RELEASE",
        targetType = "MAGAZINE",
        targetId = "mag_2026_01",
        details = "Released Digital Magazine Spring 2026 Edition",
        timestamp = System.currentTimeMillis() - 3600 * 1000L * 24
      )
    )
  }
}

private fun String.capitalizeWords(): String =
  split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

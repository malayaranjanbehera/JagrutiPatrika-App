package com.example.model

enum class UserRole {
  USER,
  ADMIN,
  AUTHOR,
  READER
}

enum class UserType(val labelEn: String, val labelOr: String) {
  READER("Reader", "ପାଠକ"),
  WRITER("Writer", "ଲେଖକ"),
  POET("Poet", "କବି"),
  STUDENT("Student", "ଛାତ୍ର / ଛାତ୍ରୀ"),
  ARTIST("Artist", "ଚିତ୍ରଶିଳ୍ପୀ"),
  PHOTOGRAPHER("Photographer", "ଫଟୋଗ୍ରାଫର")
}

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
  ODIA("or", "Odia", "ଓଡ଼ିଆ"),
  ENGLISH("en", "English", "English")
}

enum class PostCategory(val labelEn: String, val labelOr: String) {
  ALL("All", "ସବୁ"),
  POETRY("Poetry", "କବିତା"),
  STORIES("Stories", "ଗଳ୍ପ"),
  ARTICLES("Articles", "ପ୍ରବନ୍ଧ"),
  ESSAYS("Essays", "ରଚନା"),
  CHILDREN("Children's", "ଶିଶୁ ସାହିତ୍ୟ"),
  PHOTOGRAPHY("Photography", "ଫଟୋଗ୍ରାଫି"),
  ARTWORK("Artwork", "ଚିତ୍ରକଳା"),
  STUDENT("Student", "ଛାତ୍ର ସୃଷ୍ଟି"),
  ANNOUNCEMENT("Announcement", "ବାର୍ତ୍ତା")
}

enum class SubmissionStatus(val labelEn: String, val labelOr: String) {
  SUBMITTED("Submitted", "ଦାଖଲ ହୋଇଛି"),
  UNDER_REVIEW("Under Review", "ସମୀକ୍ଷା ଚାଲିଛି"),
  APPROVED("Approved", "ଅନୁମୋଦିତ"),
  CORRECTION_REQUIRED("Correction Required", "ସଂଶୋଧନ ଆବଶ୍ୟକ"),
  REJECTED("Rejected", "ଅଗ୍ରାହ୍ୟ"),
  PUBLISHED("Published", "ପ୍ରକାଶିତ")
}

data class UserProfile(
  val uid: String = "",
  val name: String = "",
  val email: String = "",
  val phone: String = "",
  val role: UserRole = UserRole.USER,
  val userType: UserType = UserType.READER,
  val preferredLanguage: AppLanguage = AppLanguage.ODIA,
  val bio: String = "",
  val photoUrl: String = "",
  val isEmailVerified: Boolean = false,
  val totalSubmissions: Int = 0,
  val totalPublished: Int = 0,
  val joinedAt: Long = System.currentTimeMillis(),
  val followersCount: Int = 124,
  val followingCount: Int = 42
) {
  val displayName: String get() = name.ifBlank { "ଜାଗୃତି ସାହିତ୍ୟିକ" }
}

data class Post(
  val id: String = "",
  val authorId: String = "",
  val authorName: String = "",
  val authorPhoto: String = "",
  val authorType: UserType = UserType.WRITER,
  val titleEn: String = "",
  val titleOr: String = "",
  val contentEn: String = "",
  val contentOr: String = "",
  val category: PostCategory = PostCategory.POETRY,
  val language: AppLanguage = AppLanguage.ODIA,
  val mediaUrl: String? = null,
  val mediaType: String = "TEXT", // TEXT, IMAGE, ART, DOCUMENT
  val status: String = "PUBLISHED",
  val likeCount: Int = 0,
  val commentCount: Int = 0,
  val shareCount: Int = 0,
  val isFeatured: Boolean = false,
  val publishedAt: Long = System.currentTimeMillis(),
  val isLikedByMe: Boolean = false,
  val isBookmarkedByMe: Boolean = false,
  val isEdited: Boolean = false
)

data class Story(
  val id: String = "",
  val authorId: String = "",
  val authorName: String = "",
  val authorPhoto: String = "",
  val title: String = "",
  val textContent: String = "",
  val bgGradientIndex: Int = 0,
  val mediaUrl: String? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val expiresAt: Long = System.currentTimeMillis() + 24 * 3600 * 1000L,
  val viewersCount: Int = 0
)

data class Comment(
  val id: String = "",
  val postId: String = "",
  val authorId: String = "",
  val authorName: String = "",
  val authorAvatar: String = "",
  val content: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val isReported: Boolean = false
)

data class Submission(
  val id: String = "",
  val authorId: String = "",
  val authorName: String = "",
  val title: String = "",
  val category: PostCategory = PostCategory.POETRY,
  val language: AppLanguage = AppLanguage.ODIA,
  val content: String = "",
  val mediaUrl: String? = null,
  val status: SubmissionStatus = SubmissionStatus.SUBMITTED,
  val editorialNotes: String = "",
  val originalityDeclared: Boolean = true,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  val submittedAt: String get() = "୨ ଦିନ ପୂର୍ବେ"
}

data class MagazineArticle(
  val id: String = "",
  val titleOr: String = "",
  val titleEn: String = "",
  val author: String = "",
  val category: String = "",
  val excerpt: String = "",
  val fullText: String = ""
)

data class MagazineIssue(
  val id: String = "",
  val issueNumber: String = "",
  val titleOr: String = "",
  val titleEn: String = "",
  val monthYear: String = "",
  val descriptionOr: String = "",
  val descriptionEn: String = "",
  val coverArtIndex: Int = 0,
  val articles: List<MagazineArticle> = emptyList(),
  val publishedAt: Long = System.currentTimeMillis(),
  val downloadSizeMb: String = "12.4 MB"
)

data class Competition(
  val id: String = "",
  val titleOr: String = "",
  val titleEn: String = "",
  val category: PostCategory = PostCategory.POETRY,
  val deadline: String = "",
  val prizeEn: String = "",
  val prizeOr: String = "",
  val rulesOr: String = "",
  val rulesEn: String = "",
  val status: String = "ACTIVE", // ACTIVE, COMPLETED
  val entriesCount: Int = 0,
  val winners: List<String> = emptyList()
)

data class NotificationItem(
  val id: String = "",
  val userId: String = "",
  val titleOr: String = "",
  val titleEn: String = "",
  val bodyOr: String = "",
  val bodyEn: String = "",
  val type: String = "EDITORIAL", // EDITORIAL, SOCIAL, MAGAZINE, COMPETITION
  val read: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
) {
  val isRead: Boolean get() = read
  val title: String get() = titleOr.ifBlank { titleEn }
  val body: String get() = bodyOr.ifBlank { bodyEn }
  val timestamp: String get() = "୧ ଘଣ୍ଟା ପୂର୍ବେ"
}

data class AuditLog(
  val id: String = "",
  val adminId: String = "",
  val adminEmail: String = "",
  val action: String = "",
  val targetType: String = "",
  val targetId: String = "",
  val details: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

package com.example.data

import com.example.model.AppLanguage

object AppStrings {
  fun appName(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଜାଗୃତି ପତ୍ରିକା" else "Jagrutipatrika"
  fun brandOdia() = "ଜାଗୃତି"
  fun tagline(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସଚେତନ ସମାଜର ସ୍ୱର" else "Voice of a Conscious Society"

  // Bottom Nav
  fun navHome(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମୁଖ୍ୟ ପୃଷ୍ଠା" else "Home"
  fun homeTab(lang: AppLanguage) = navHome(lang)
  fun navMagazine(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପତ୍ରିକା" else "Magazine"
  fun magazineTab(lang: AppLanguage) = navMagazine(lang)
  fun navCreate(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସୃଷ୍ଟି" else "Create"
  fun navCompetitions(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପ୍ରତିଯୋଗିତା" else "Contests"
  fun competitionsTab(lang: AppLanguage) = navCompetitions(lang)
  fun navProfile(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପ୍ରୋଫାଇଲ୍" else "Profile"
  fun profileTab(lang: AppLanguage) = navProfile(lang)
  fun signOut(lang: AppLanguage) = logout(lang)
  fun submitButton(lang: AppLanguage) = submitForReview(lang)
  fun adminPanelTitle(lang: AppLanguage) = adminPanel(lang)
  fun notificationsTitle(lang: AppLanguage) = notifications(lang)
  fun settingsTitle(lang: AppLanguage) = settings(lang)
  fun correctionRequired(lang: AppLanguage) = correctionReq(lang)
  fun bookmarked(lang: AppLanguage) = bookmarks(lang)
  fun myPublishedWorks(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମୋର ପ୍ରକାଶିତ ରଚନା" else "My Published Works"

  // Auth
  fun signIn(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପ୍ରବେଶ କରନ୍ତୁ" else "Sign In"
  fun signUp(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପଞ୍ଜୀକରଣ କରନ୍ତୁ" else "Sign Up"
  fun continueWithGoogle(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଗୁଗୁଲ୍ ସହ ଆଗକୁ ବଢ଼ନ୍ତୁ" else "Continue with Google"
  fun email(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଇମେଲ୍ ଠିକଣା" else "Email Address"
  fun password(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପାସୱାର୍ଡ" else "Password"
  fun confirmPassword(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପାସୱାର୍ଡ ନିଶ୍ଚିତ କରନ୍ତୁ" else "Confirm Password"
  fun fullName(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପୂରା ନାମ" else "Full Name"
  fun phoneNumber(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଫୋନ୍ ନମ୍ବର (ଐଚ୍ଛିକ)" else "Phone Number (Optional)"
  fun userTypeLabel(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଆପଣଙ୍କ ପରିଚୟ ବାଛନ୍ତୁ" else "Select Your Profile Type"
  fun preferredLangLabel(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପସନ୍ଦର ଭାଷା" else "Preferred Language"
  fun forgotPassword(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପାସୱାର୍ଡ ଭୁଲିଗଲେ କି?" else "Forgot Password?"
  fun resetPassword(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପାସୱାର୍ଡ ରିସେଟ୍ ଲିଙ୍କ୍ ପଠାନ୍ତୁ" else "Send Reset Link"
  fun termsAcceptance(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମୁଁ ନିୟମ ଓ ଗୋପନୀୟତା ନୀତି ସହିତ ସହମତ" else "I accept the Terms & Privacy Policy"
  fun dontHaveAccount(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଖାତା ନାହିଁ କି? ପଞ୍ଜୀକରଣ କରନ୍ତୁ" else "Don't have an account? Sign Up"
  fun alreadyHaveAccount(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପୂର୍ବରୁ ଖାତା ଅଛି କି? ପ୍ରବେଶ କରନ୍ତୁ" else "Already have an account? Sign In"
  fun logout(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପ୍ରସ୍ଥାନ କରନ୍ତୁ" else "Log Out"

  // Feed & Social
  fun stories(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଗଳ୍ପ କାହାଣୀ (Stories)" else "Stories"
  fun addStory(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଷ୍ଟୋରି ଯୋଡ଼ନ୍ତୁ" else "Add Story"
  fun refreshing(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଜାଗୃତି ରିଫ୍ରେଶ୍ ହେଉଛି..." else "Refreshing Jagrutipatrika..."
  fun searchPlaceholder(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଲେଖା, କବିତା, ଲେଖକ ଖୋଜନ୍ତୁ..." else "Search articles, poems, authors..."
  fun like(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପସନ୍ଦ" else "Like"
  fun comment(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମତାମତ" else "Comment"
  fun share(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସେୟାର୍" else "Share"
  fun bookmark(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସାଇତି ରଖନ୍ତୁ" else "Bookmark"
  fun commentsTitle(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପାଠକଙ୍କ ମତାମତ" else "Reader Comments"
  fun writeComment(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଆପଣଙ୍କ ମତାମତ ଲେଖନ୍ତୁ..." else "Write a comment..."
  fun postComment(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପୋଷ୍ଟ୍ କରନ୍ତୁ" else "Post"
  fun editPost(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସଂଶୋଧନ କରନ୍ତୁ" else "Edit Post"
  fun deletePost(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଡିଲିଟ୍ କରନ୍ତୁ" else "Delete Post"
  fun readMore(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଅଧିକ ପଢ଼ନ୍ତୁ..." else "Read more..."
  fun readLess(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "କମ୍ ଦେଖନ୍ତୁ" else "Show less"

  // Submissions & Editorial
  fun authorDashboard(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଲେଖକ ଡ୍ୟାସବୋର୍ଡ" else "Author Dashboard"
  fun submitWork(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ନୂଆ ଲେଖା ଦାଖଲ କରନ୍ତୁ" else "Submit Work"
  fun originalityDeclaration(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମୁଁ ଘୋଷଣା କରୁଛି ଯେ ଏହା ମୋର ନିଜସ୍ୱ ମୌଳିକ ସୃଷ୍ଟି ଅଟେ ଏବଂ କୌଣସି ନକଲ ହୋଇନାହିଁ।" else "I declare that this is my original creative work and not plagiarized."
  fun titleField(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଶୀର୍ଷକ (Title)" else "Title"
  fun contentField(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଲେଖା ବା କବିତାର ବିଷୟବସ୍ତୁ" else "Content / Verses"
  fun categorySelect(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ବିଭାଗ ବାଛନ୍ତୁ" else "Select Category"
  fun submitForReview(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସମୀକ୍ଷା ପାଇଁ ଦାଖଲ କରନ୍ତୁ" else "Submit for Review"
  fun mySubmissions(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମୋର ଦାଖଲ ସୃଷ୍ଟିଗୁଡ଼ିକ" else "My Submissions"
  fun resubmit(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପୁନଃ ଦାଖଲ କରନ୍ତୁ" else "Resubmit"

  // Counters
  fun totalSubmissions(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମୋଟ ଦାଖଲ" else "Total Submitted"
  fun underReview(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସମୀକ୍ଷାଧୀନ" else "Under Review"
  fun correctionReq(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସଂଶୋଧନ ଆବଶ୍ୟକ" else "Correction Req."
  fun approved(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଅନୁମୋଦିତ" else "Approved"
  fun published(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପ୍ରକାଶିତ" else "Published"
  fun rejected(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଅଗ୍ରାହ୍ୟ" else "Rejected"

  // Text to Image
  fun textToImageTitle(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "କବିତା / ଉକ୍ତି ଫଟୋ ନିର୍ମାଣ (Text to Image)" else "Text to Image Creator"
  fun authorNameWatermark(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଲେଖକଙ୍କ ନାମ" else "Author Name"
  fun backgroundTheme(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପୃଷ୍ଠଭୂମି ରଙ୍ଗ (Theme)" else "Background Theme"
  fun fontSizeLabel(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଅକ୍ଷର ଆକାର" else "Font Size"
  fun saveAndShare(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସାଇତନ୍ତୁ ଓ ସେୟାର୍ କରନ୍ତୁ" else "Save & Share"

  // Magazine
  fun digitalMagazine(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଇ-ପତ୍ରିକା (Digital Magazine)" else "Digital Magazine"
  fun currentIssue(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଚଳିତ ସଂଖ୍ୟା" else "Current Issue"
  fun readArticles(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଲେଖାଗୁଡ଼ିକ ପଢ଼ନ୍ତୁ" else "Read Articles"
  fun downloadPdf(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଇ-ମାଗାଜିନ୍ ଡାଉନଲୋଡ୍ କରନ୍ତୁ" else "Download Magazine"
  fun pastIssues(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପୂର୍ବବର୍ତ୍ତୀ ସଂଖ୍ୟାଗୁଡ଼ିକ" else "Previous Issues"

  // Competitions
  fun competitionsTitle(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସାହିତ୍ୟ ଓ ଚିତ୍ରକଳା ପ୍ରତିଯୋଗିତା" else "Literature & Art Competitions"
  fun deadline(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଶେଷ ତାରିଖ" else "Deadline"
  fun prize(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପୁରସ୍କାର" else "Prize"
  fun registerEntry(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପ୍ରତିଯୋଗିତାରେ ଭାଗ ନିଅନ୍ତୁ" else "Submit Entry"
  fun viewWinners(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ବିଜେତା ତାଲିକା ଓ ସାର୍ଟିଫିକେଟ୍" else "Winners & Certificates"

  // Admin
  fun adminPanel(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପ୍ରଶାସକ ପ୍ୟାନେଲ୍ (Admin)" else "Admin Panel"
  fun adminLimitNotice(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସର୍ବାଧିକ ୪ ଜଣ ପ୍ରଶାସକ ଅନୁମୋଦିତ (Strict 4 Admins Cap)" else "Strict Limit: Exactly 4 Admin Accounts Maximum"
  fun reviewSubmissions(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଦାଖଲ ଲେଖାଗୁଡ଼ିକର ସମୀକ୍ଷା" else "Review Submissions"
  fun publishToFeed(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମୁଖ୍ୟ ଫିଡ୍‌ରେ ପ୍ରକାଶ କରନ୍ତୁ" else "Publish to Public Feed"
  fun requestCorrection(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସଂଶୋଧନ ଅନୁରୋଧ" else "Request Correction"
  fun approve(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଅନୁମୋଦନ କରନ୍ତୁ" else "Approve"
  fun reject(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଅଗ୍ରାହ୍ୟ କରନ୍ତୁ" else "Reject"
  fun auditLogs(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ଅଡିଟ୍ ଲଗ୍ (Audit Logs)" else "Audit Logs"
  fun moderateComments(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ମତାମତ ପରିଚାଳନା" else "Moderate Comments"
  fun managePosts(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ପୋଷ୍ଟ୍ ପରିଚାଳନା" else "Manage Posts"

  // Common
  fun cancel(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ବାତିଲ୍" else "Cancel"
  fun close(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ବନ୍ଦ କରନ୍ତୁ" else "Close"
  fun save(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସାଇତନ୍ତୁ" else "Save"
  fun settings(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସେଟିଂସ୍" else "Settings"
  fun notifications(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ବାର୍ତ୍ତାଗୁଡ଼ିକ" else "Notifications"
  fun bookmarks(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "ସାଇତି ରଖାଯାଇଥିବା ଲେଖା" else "Bookmarks"
  fun languageToggle(lang: AppLanguage) = if (lang == AppLanguage.ODIA) "English" else "ଓଡ଼ିଆ"
}

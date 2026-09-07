package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppStrings
import com.example.data.FirebaseManager
import com.example.model.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TextToImageState
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Jagrutipatrika", appName)
  }

  @Test
  fun `verify Odia and English localization in AppStrings`() {
    // Brand and Titles
    assertEquals("ଜାଗୃତି ପତ୍ରିକା", AppStrings.appName(AppLanguage.ODIA))
    assertEquals("Jagrutipatrika", AppStrings.appName(AppLanguage.ENGLISH))
    assertEquals("ଜାଗୃତି", AppStrings.brandOdia())
    assertEquals("ସଚେତନ ସମାଜର ସ୍ୱର", AppStrings.tagline(AppLanguage.ODIA))
    assertEquals("Voice of a Conscious Society", AppStrings.tagline(AppLanguage.ENGLISH))

    // Navigation
    assertEquals("ମୁଖ୍ୟ ପୃଷ୍ଠା", AppStrings.homeTab(AppLanguage.ODIA))
    assertEquals("Home", AppStrings.homeTab(AppLanguage.ENGLISH))
    assertEquals("ପତ୍ରିକା", AppStrings.magazineTab(AppLanguage.ODIA))
    assertEquals("Magazine", AppStrings.magazineTab(AppLanguage.ENGLISH))
    assertEquals("ପ୍ରତିଯୋଗିତା", AppStrings.competitionsTab(AppLanguage.ODIA))
    assertEquals("Contests", AppStrings.competitionsTab(AppLanguage.ENGLISH))
    assertEquals("ପ୍ରୋଫାଇଲ୍", AppStrings.profileTab(AppLanguage.ODIA))
    assertEquals("Profile", AppStrings.profileTab(AppLanguage.ENGLISH))
  }

  @Test
  fun `verify PostCategory labels in both languages`() {
    val categories = PostCategory.values()
    assertTrue(categories.isNotEmpty())

    val poetry = PostCategory.POETRY
    assertEquals("କବିତା", poetry.labelOr)
    assertEquals("Poetry", poetry.labelEn)

    val stories = PostCategory.STORIES
    assertEquals("ଗଳ୍ପ", stories.labelOr)
    assertEquals("Stories", stories.labelEn)

    val articles = PostCategory.ARTICLES
    assertEquals("ପ୍ରବନ୍ଧ", articles.labelOr)
    assertEquals("Articles", articles.labelEn)

    val essays = PostCategory.ESSAYS
    assertEquals("ରଚନା", essays.labelOr)
    assertEquals("Essays", essays.labelEn)
  }

  @Test
  fun `verify Admin whitelist security cap of 4 administrators`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val firebaseManager = FirebaseManager.getInstance(context)

    assertEquals(4, firebaseManager.designatedAdminEmails.size)
    assertTrue(firebaseManager.designatedAdminEmails.contains("zoyaadahlia09@gmail.com"))
    assertTrue(firebaseManager.designatedAdminEmails.contains("chief.editor@jagrutipatrika.org"))
  }

  @Test
  fun `verify TextToImageState updates and dimensions`() {
    val defaultState = TextToImageState()
    assertEquals(0, defaultState.bgGradientIndex)
    assertFalse(defaultState.isStoryAspect)
    assertEquals(18f, defaultState.fontSizeSp)

    val updatedState = defaultState.copy(
      title = "ନୂତନ ପ୍ରଭାତ",
      verses = "ଆଲୋକର ବର୍ତ୍ତିକା ଜାଳି ଚାଲିବା ଆମର ଧର୍ମ।",
      author = "ସୁଶ୍ରୀ ଦାସ",
      bgGradientIndex = 2,
      isStoryAspect = true,
      fontSizeSp = 22f
    )

    assertEquals("ନୂତନ ପ୍ରଭାତ", updatedState.title)
    assertEquals("ସୁଶ୍ରୀ ଦାସ", updatedState.author)
    assertEquals(2, updatedState.bgGradientIndex)
    assertTrue(updatedState.isStoryAspect)
    assertEquals(22f, updatedState.fontSizeSp)
  }

  @Test
  fun `verify Screen navigation enum transitions`() {
    val allScreens = Screen.values()
    assertTrue(allScreens.contains(Screen.SPLASH))
    assertTrue(allScreens.contains(Screen.HOME))
    assertTrue(allScreens.contains(Screen.MAGAZINE))
    assertTrue(allScreens.contains(Screen.COMPETITIONS))
    assertTrue(allScreens.contains(Screen.PROFILE))
    assertTrue(allScreens.contains(Screen.ADMIN))
    assertTrue(allScreens.contains(Screen.SUBMIT_WORK))
    assertTrue(allScreens.contains(Screen.TEXT_TO_IMAGE))
  }
}

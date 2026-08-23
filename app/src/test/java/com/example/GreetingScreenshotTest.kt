package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.UserProfile
import com.example.model.UserWallet
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        MainMenuScreen(
          userProfile = UserProfile(
            uid = "test_user",
            displayName = "Test Player",
            email = "test@example.com",
            photoUrl = null,
            isGuest = true,
            wallets = UserWallet(gameCoins = 500, cashCoins = 100)
          ),
          onNavigateToPlay = {},
          onNavigateToLevels = {},
          onNavigateToChallenge = {},
          onNavigateToRandomPlay = {},
          onNavigateToProfile = {},
          onNavigateToMembership = {},
          onNavigateToReferral = {},
          onNavigateToSettings = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}



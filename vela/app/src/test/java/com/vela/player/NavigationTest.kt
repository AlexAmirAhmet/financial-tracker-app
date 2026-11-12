package com.vela.player

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NavigationTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
    @Test fun tilesOpenScreens() {
        rule.waitUntilAtLeastOneExists(androidx.compose.ui.test.hasText("Поток"), 10_000)
        rule.onNodeWithText("Поток").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("поток").assertExists()
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()
        rule.onNodeWithText("Настройки").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("настройки").assertExists()
    }
}

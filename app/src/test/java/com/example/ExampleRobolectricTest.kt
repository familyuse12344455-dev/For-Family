package com.example

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.BookingStatus
import com.example.ui.components.AutomotiveSectionHeader
import com.example.ui.components.BookingStatusBadge
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.components.TorqfixIgnitionButton
import com.example.ui.components.TorqfixOutlinedButton
import com.example.ui.theme.TorqfixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TORQFIX", appName)
    }

    @Test
    fun `test TorqfixGoldButton click interaction`() {
        var clicked = false
        composeTestRule.setContent {
            TorqfixTheme {
                TorqfixGoldButton(
                    text = "Confirm Booking",
                    icon = Icons.Default.Build,
                    onClick = { clicked = true }
                )
            }
        }

        // Gold button displays uppercase text
        val buttonNode = composeTestRule.onNodeWithText("CONFIRM BOOKING")
        buttonNode.assertIsDisplayed()
        buttonNode.performClick()
        assertTrue("Gold button should execute click callback", clicked)
    }

    @Test
    fun `test TorqfixOutlinedButton click interaction`() {
        var clicked = false
        composeTestRule.setContent {
            TorqfixTheme {
                TorqfixOutlinedButton(
                    text = "Back to Garage",
                    onClick = { clicked = true }
                )
            }
        }

        val buttonNode = composeTestRule.onNodeWithText("Back to Garage")
        buttonNode.assertIsDisplayed()
        buttonNode.performClick()
        assertTrue("Outlined button should execute click callback", clicked)
    }

    @Test
    fun `test TorqfixIgnitionButton rendering and click`() {
        var ignitionTriggered = false
        composeTestRule.setContent {
            TorqfixTheme {
                TorqfixIgnitionButton(
                    title = "START ENGINE",
                    subtitle = "PUSH TO DIAGNOSE",
                    onClick = { ignitionTriggered = true }
                )
            }
        }

        composeTestRule.onNodeWithText("START ENGINE").assertIsDisplayed()
        composeTestRule.onNodeWithText("PUSH TO DIAGNOSE").assertIsDisplayed()
        composeTestRule.onNodeWithText("ENGAGE").assertIsDisplayed()

        composeTestRule.onNodeWithText("START ENGINE").performClick()
        assertTrue("Ignition button should trigger action on click", ignitionTriggered)
    }

    @Test
    fun `test AutomotiveSectionHeader rendering`() {
        var badgeClicked = false
        composeTestRule.setContent {
            TorqfixTheme {
                AutomotiveSectionHeader(
                    title = "Telemetry Lab",
                    subtitle = "Live sensors connected",
                    badge = "ACTIVE",
                    onBadgeClick = { badgeClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("TELEMETRY LAB").assertIsDisplayed()
        composeTestRule.onNodeWithText("Live sensors connected").assertIsDisplayed()
        val badge = composeTestRule.onNodeWithText("ACTIVE")
        badge.assertIsDisplayed()
        badge.performClick()
        assertTrue("Header badge should be clickable", badgeClicked)
    }

    @Test
    fun `test BookingStatusBadge displays status label`() {
        composeTestRule.setContent {
            TorqfixTheme {
                BookingStatusBadge(status = BookingStatus.REPAIRING)
            }
        }

        composeTestRule.onNodeWithText("Repairing").assertIsDisplayed()
    }
}

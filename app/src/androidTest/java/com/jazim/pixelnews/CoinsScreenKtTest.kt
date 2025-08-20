package com.jazim.pixelnews

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jazim.pixelnews.presentation.coins.CoinViewModel
import com.jazim.pixelnews.presentation.coins.CoinsScreen
import com.jazim.pixelnews.presentation.state.AllCoinsState
import com.jazim.pixelnews.presentation.state.CoinDetailState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CoinsScreenKtTest {

    @MockK
    private lateinit var coinViewModel: CoinViewModel

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.jazim.pixelnews", appContext.packageName)
    }

    @Test
    fun coinsScreenShowsMainScreen() {
        every { coinViewModel.allCoinsState } returns MutableStateFlow(AllCoinsState())
        every { coinViewModel.coinDetailState } returns MutableStateFlow(CoinDetailState())
        every { coinViewModel.filteredCoins } returns mutableStateOf(emptyList())
        every { coinViewModel.searchQuery } returns mutableStateOf("")

        composeTestRule.setContent {
            CoinsScreen(coinViewModel)
        }

        composeTestRule.onNodeWithTag("MainScreen").assertIsDisplayed()
    }


    // to confirm this testing format works, and isn't giving false positives
    @Test
    fun coinsScreenDoesntShowRandomTestTagScreen() {
        every { coinViewModel.allCoinsState } returns MutableStateFlow(AllCoinsState())
        every { coinViewModel.coinDetailState } returns MutableStateFlow(CoinDetailState())
        every { coinViewModel.filteredCoins } returns mutableStateOf(emptyList())
        every { coinViewModel.searchQuery } returns mutableStateOf("")

        composeTestRule.setContent {
            CoinsScreen(coinViewModel)
        }

        composeTestRule.onNodeWithTag("foobar").assertIsNotDisplayed()
    }

    @Test
    fun coinsScreenShowsLoadingIndicatorWhenLoading() {
        every { coinViewModel.allCoinsState } returns MutableStateFlow(AllCoinsState(loading = true))
        every { coinViewModel.coinDetailState } returns MutableStateFlow(CoinDetailState())
        every { coinViewModel.filteredCoins } returns mutableStateOf(emptyList())
        every { coinViewModel.searchQuery } returns mutableStateOf("")

        composeTestRule.setContent {
            CoinsScreen(coinViewModel)
        }

        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun coinsScreenShowsErrorMessageWhenErrorOccurs() {
        val errorMessage = "An error occurred"
        every { coinViewModel.allCoinsState } returns MutableStateFlow(AllCoinsState(loading = false, error = errorMessage))
        every { coinViewModel.coinDetailState } returns MutableStateFlow(CoinDetailState())
        every { coinViewModel.filteredCoins } returns mutableStateOf(emptyList())
        every { coinViewModel.searchQuery } returns mutableStateOf("")

        composeTestRule.setContent {
            CoinsScreen(coinViewModel)
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }


}
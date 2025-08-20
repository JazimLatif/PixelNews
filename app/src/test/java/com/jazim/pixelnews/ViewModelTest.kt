package com.jazim.pixelnews

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jazim.pixelnews.domain.model.ShortCoin
import com.jazim.pixelnews.domain.repository.CoinRepository
import com.jazim.pixelnews.presentation.coins.CoinViewModel
import com.jazim.pixelnews.presentation.state.ShortCoinState
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@ExperimentalCoroutinesApi
class CoinViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var coinRepository: CoinRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coinRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun whenGetCoinsSucceedsReturnsSuccess() = runTest {

        val mockResponse = listOf(ShortCoin("1","foobar","baz"), ShortCoin("2","barfoo","bazeggs"))
        coEvery { coinRepository.getCoins() } returns Result.success(mockResponse)

        val coinViewModel = CoinViewModel(coinRepository, testDispatcher)

        advanceUntilIdle()

        assertEquals(false, coinViewModel.allCoinsState.value.loading)
        assertNull(coinViewModel.allCoinsState.value.error)


        val expectedState = listOf(ShortCoinState("1","foobar","baz"), ShortCoinState("2","barfoo","bazeggs"))
        assertEquals(expectedState, coinViewModel.allCoinsState.value.coins)
        coVerify { coinRepository.getCoins() }
    }

    @Test
    fun whenGetCoinsFailsReturnsFailure() = runTest {

        val errorMessage = "Failed to load coins"
        coEvery { coinRepository.getCoins() } returns Result.failure(Exception(errorMessage))

        val coinViewModel = CoinViewModel(coinRepository, testDispatcher)
        advanceUntilIdle()

        assertEquals(false, coinViewModel.allCoinsState.value.loading)
        assertEquals(errorMessage, coinViewModel.allCoinsState.value.error)
        assertEquals(emptyList<ShortCoinState>(), coinViewModel.allCoinsState.value.coins)
    }


    @Test fun coinsAreSortedAlphabetically() = runTest {

        val mockResponse = listOf( ShortCoin("1", "beta", "b"),
            ShortCoin("2", " alpha", "a"), // with a space at the start
            ShortCoin("3", "delta", "d"),
            ShortCoin("4", "gamma", "g"),
            ShortCoin("5", "epsilon", "e"),
            ShortCoin("6", "zeta", "z"),
            ShortCoin("7", "\$symbol", "!"),
            ShortCoin("8", "eta", "h"),
            ShortCoin("9", "iota", "i"),
            ShortCoin("10", "kappa", "k"),
            ShortCoin("11", "1hash", "1"),
            ShortCoin("12", " omega", "o"), // another leading space
            ShortCoin("13", "@at", "@"),
            ShortCoin("14", "theta", "t"),
            ShortCoin("15", "APPLE", "A"),
            ShortCoin("17", "apple", "a"),
            ShortCoin("16", "GARY", "G")
        )
        coEvery { coinRepository.getCoins() } returns Result.success(mockResponse)

        val coinViewModel = CoinViewModel(coinRepository, testDispatcher)
        advanceUntilIdle()

        assertEquals(
            listOf(
                ShortCoinState("2", " alpha", "a"),
                ShortCoinState("12", " omega", "o"),
                ShortCoinState("7", "\$symbol", "!"),
                ShortCoinState("11", "1hash", "1"),
                ShortCoinState("13", "@at", "@"),
                ShortCoinState("15", "APPLE", "A"),
                ShortCoinState("16", "GARY", "G"),
                ShortCoinState("17", "apple", "a"),
                ShortCoinState("1", "beta", "b"),
                ShortCoinState("3", "delta", "d"),
                ShortCoinState("5", "epsilon", "e"),
                ShortCoinState("8", "eta", "h"),
                ShortCoinState("4", "gamma", "g"),
                ShortCoinState("9", "iota", "i"),
                ShortCoinState("10", "kappa", "k"),
                ShortCoinState("14", "theta", "t"),
                ShortCoinState("6", "zeta", "z")
            ),
            coinViewModel.getCoinsAlphabetically()
        )
    }
}

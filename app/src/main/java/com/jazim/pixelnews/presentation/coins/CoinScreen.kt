package com.jazim.pixelnews.presentation.coins

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.jazim.pixelnews.presentation.CoinViewModel
import com.jazim.pixelnews.presentation.components.CoinListItemView
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinsScreen(
    coinViewModel: CoinViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    val allCoinsState = coinViewModel.allCoinsState.value
    val coinDetailState = coinViewModel.coinDetailState.value

    val bottomSheetState = rememberModalBottomSheetState()

    var selectedCoinId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedCoinName by rememberSaveable { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    var isRefreshing by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                coinViewModel.getAllCoins()
            }
        ) {
            LaunchedEffect(allCoinsState.loading) {
                if (!allCoinsState.loading) {
                    isRefreshing = false
                }
            }

            when {
                // This was done because I wanted the CircularProgressIndicator when the app first loads,
                // but on refreshes I wanted to only see the PullDownToRefreshIndicator, and I was seeing both overlapped which wasn't nice
                allCoinsState.loading && !isRefreshing -> {
                    CircularProgressIndicator(Modifier.testTag("LoadingIndicator"))
                }

                allCoinsState.error != null -> {
                    isRefreshing = false

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(modifier = Modifier.width(300.dp), text = allCoinsState.error.toString())
                        Button( { coinViewModel.getAllCoins() } ) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    val sortedCoins = coinViewModel.getCoinsAlphabetically()
                    Box(Modifier.fillMaxSize()) {
                        LazyColumn(state = listState) {
                            items(allCoinsState.coins) { coin ->
                                CoinListItemView(
                                    modifier = Modifier,
                                    symbol = coin.symbol,
                                    name = coin.name,
                                    onClick = {
                                        selectedCoinId = null
                                        val clickedCoin =
                                            allCoinsState.coins.find { it.id == coin.id }
                                        selectedCoinId = clickedCoin?.id
                                        selectedCoinName = clickedCoin?.name
                                        selectedCoinId?.let { id -> coinViewModel.getCoin(id) }
                                        coroutineScope.launch { bottomSheetState.show() }
                                    }
                                )
                            }
                        }
                        val showButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 10} }

                        AnimatedVisibility(
                            visible = showButton,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                            ) {
                                Text("Scroll to top")
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedCoinId != null) {
        BottomSheet(
            onDismissed = { selectedCoinId = null },
            selectedCoinName = selectedCoinName,
            coinDetailState = coinDetailState,
            bottomSheetState = bottomSheetState,
        )
    }
}


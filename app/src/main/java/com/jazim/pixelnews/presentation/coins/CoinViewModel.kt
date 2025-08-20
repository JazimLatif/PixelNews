package com.jazim.pixelnews.presentation.coins

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jazim.pixelnews.di.IoDispatcher
import com.jazim.pixelnews.domain.repository.CoinRepository
import com.jazim.pixelnews.presentation.state.AllCoinsState
import com.jazim.pixelnews.presentation.state.CoinDetailState
import com.jazim.pixelnews.presentation.state.ShortCoinState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _allCoinsState = MutableStateFlow(AllCoinsState())
    val allCoinsState = _allCoinsState.asStateFlow()

    private val _coinDetailState = MutableStateFlow(CoinDetailState())
    val coinDetailState = _coinDetailState.asStateFlow()

    private val _searchQuery = mutableStateOf("")
    val searchQuery: MutableState<String> = _searchQuery

    init {
        getAllCoins()
    }

    fun getAllCoins() {
        // Started logging this because I was worried I hit the rate limit of the API since I was
        // refreshing using the pullToRefresh and started getting 402 errors which made me think I
        // had called the api more than the free limit (20,000 per month) due to an infinite loop
        // turns out this happens if you call it more than once per second for a while also
        // (which I learned after contacting coinpaprika support)
        println("getAllCoins called at ${System.currentTimeMillis()}")

        // I initially thought this line wasn't needed because loading is true by default in AllCoinsState()
        // However, when called again from pull down to refresh, this line is needed to set loading to true
        _allCoinsState.value = AllCoinsState(loading = true, error = null)


        viewModelScope.launch {
            // for testability, not using Dispatchers.IO
            val result = withContext(ioDispatcher) {
                coinRepository.getCoins()
            }

            result.fold(
                onSuccess = { coinsInfo ->
                    _allCoinsState.value = AllCoinsState(
                        loading = false,
                        coins = coinsInfo.map { coin ->
                            ShortCoinState(
                                id = coin.id,
                                symbol = coin.symbol,
                                name = coin.name
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _allCoinsState.value = AllCoinsState(
                        error = error.message, loading = false,
                    )
                }
            )
        }
    }

    fun getCoin(id: String) {
        _coinDetailState.value = CoinDetailState(loading = true, error = null)
        viewModelScope.launch {

            val result = withContext(ioDispatcher) {
                coinRepository.getCoinById(id)
            }

            result.fold(
                onSuccess = { coinInfo ->
                    _coinDetailState.value = CoinDetailState(
                        loading = false,
                        name = coinInfo.name,
                        logo = coinInfo.logo,
                        description = coinInfo.description,
                        links = coinInfo.links
                    )
                },
                onFailure = { error ->
                    _coinDetailState.value = CoinDetailState(
                        error = error.message, loading = false,
                    )
                }
            )
        }
    }

    // If there were a lot of these "helper" functions they could be moved into a class and injected, since there aren't many I chose to leave them here
    fun getCoinsAlphabetically(): List<ShortCoinState>{
        return _allCoinsState.value.coins.map { coin ->
            ShortCoinState(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol
            )
        }.sortedBy { it.name }
    }


    val filteredCoins: State<List<ShortCoinState>> = derivedStateOf {
        val query = searchQuery.value
        if (query.isEmpty()) {
            getCoinsAlphabetically()
        } else {
            allCoinsState.value.coins.filter { it.name.startsWith(query) }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

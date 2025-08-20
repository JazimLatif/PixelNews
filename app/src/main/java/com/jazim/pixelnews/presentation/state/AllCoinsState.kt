package com.jazim.pixelnews.presentation.state

data class AllCoinsState(
    val loading: Boolean = true,
    val error: String? = null,
    val coins: List<ShortCoinState> = emptyList()
)

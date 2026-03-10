package com.rohan.fablefit.agent

import androidx.compose.runtime.Composable

sealed class AgentRoutes(
    val title: String,
    val route: String,
    val ui: @Composable () -> Unit
) {

    object ImageSearch : AgentRoutes(
        title = "Image Search",
        route = "image_search",
        ui = { /*ImageSearchScreen()*/ }
    )
    object ChatBot: AgentRoutes(
        "Chat",
        "chat",
        ui={},
    )
}
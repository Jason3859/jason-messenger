package dev.jason.app.compose.messenger_app.auth_ui.controller

import dev.jason.app.compose.messenger_app.auth_ui.route.AuthRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal object NavigationController {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private val _destination = MutableSharedFlow<Pair<AuthRoute, Boolean>>()
    val destination = _destination.asSharedFlow()

    private val backStack = mutableListOf<AuthRoute>()

    fun navigate(route: AuthRoute, popBackStack: Boolean = false) {
        coroutineScope.launch {
            _destination.emit(route to popBackStack)
        }
    }

    init {
        coroutineScope.launch {
            _destination.collect { (route, _) ->
                backStack.add(route)
            }
        }
    }
}
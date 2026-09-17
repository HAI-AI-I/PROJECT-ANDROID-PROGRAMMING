package com.group_7.library_management.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class AppSnackbarEvent(
    val message: String,
    val duration: SnackbarDuration = SnackbarDuration.Short
)

@Singleton
class AppSnackbarController @Inject constructor() {
    private val _events = MutableSharedFlow<AppSnackbarEvent>(extraBufferCapacity = 10)
    val events = _events.asSharedFlow()

    fun show(message: String,duration: SnackbarDuration = SnackbarDuration.Short) {
        _events.tryEmit(AppSnackbarEvent(message, duration))
    }
}

@Composable
fun AppSnackbarHost(controller: AppSnackbarController) {
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(controller) {
        controller.events.collect { event ->
            hostState.showSnackbar(
                message = event.message,
                withDismissAction = true,
                duration = event.duration
            )
        }
    }

    SnackbarHost(hostState = hostState)
}

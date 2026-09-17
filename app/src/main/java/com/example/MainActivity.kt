package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.TorqfixDatabase
import com.example.data.repository.TorqfixRepository
import com.example.ui.screens.MainScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.theme.TorqfixTheme
import com.example.ui.viewmodel.TorqfixViewModel
import com.example.ui.viewmodel.TorqfixViewModelFactory

enum class AppDestination {
    SPLASH,
    AUTH,
    MAIN
}

class MainActivity : ComponentActivity() {

    private val viewModel: TorqfixViewModel by viewModels {
        com.example.data.remote.supabase.SupabaseSessionManager.init(applicationContext)
        val database = TorqfixDatabase.getInstance(applicationContext)
        val repository = TorqfixRepository(database.torqfixDao())
        TorqfixViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.data.remote.supabase.SupabaseSessionManager.init(applicationContext)
        enableEdgeToEdge()

        setContent {
            TorqfixTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TorqNavyBackground
                ) {
                    TorqfixAppHost(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TorqfixAppHost(viewModel: TorqfixViewModel) {
    val currentCustomer by viewModel.currentCustomer.collectAsState()
    var appDestination by remember { mutableStateOf(AppDestination.SPLASH) }

    AnimatedContent(
        targetState = appDestination,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "appNavigation"
    ) { destination ->
        when (destination) {
            AppDestination.SPLASH -> {
                SplashScreen(
                    onTimeout = {
                        // After splash, if authenticated in SupabaseSessionManager, open MainScreen; otherwise AuthScreen
                        if (viewModel.isUserAuthenticated()) {
                            appDestination = AppDestination.MAIN
                        } else {
                            appDestination = AppDestination.AUTH
                        }
                    }
                )
            }
            AppDestination.AUTH -> {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        appDestination = AppDestination.MAIN
                    }
                )
            }
            AppDestination.MAIN -> {
                MainScreen(
                    viewModel = viewModel,
                    onLogout = {
                        appDestination = AppDestination.AUTH
                    }
                )
            }
        }
    }
}

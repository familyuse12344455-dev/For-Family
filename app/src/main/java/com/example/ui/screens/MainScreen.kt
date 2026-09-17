package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.components.NavTab
import com.example.ui.components.TorqfixBottomNav
import com.example.ui.screens.book.BookRepairScreen
import com.example.ui.screens.garage.VehiclesGarageScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.tracking.TrackingScreen
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.viewmodel.TorqfixViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: TorqfixViewModel,
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    val activeBooking by viewModel.activeBooking.collectAsState()
    val unreadCount by viewModel.unreadNotifCount.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = TorqNavyBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            TorqfixBottomNav(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it },
                hasActiveBooking = activeBooking != null,
                unreadNotifications = unreadCount
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onBookRepairClick = { currentTab = NavTab.BOOKINGS },
                        onTrackBookingClick = { _ -> currentTab = NavTab.TRACK },
                        onViewHistoryClick = { currentTab = NavTab.VEHICLES },
                        onNotificationsClick = { currentTab = NavTab.PROFILE },
                        onManageVehiclesClick = { currentTab = NavTab.VEHICLES },
                        onSupportClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Connecting to 24/7 Concierge Hotline: 0800-TORQFIX")
                            }
                        }
                    )
                }
                NavTab.BOOKINGS -> {
                    BookRepairScreen(
                        viewModel = viewModel,
                        onBookingCreated = { _ ->
                            currentTab = NavTab.TRACK
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Booking submitted! Driver dispatch initiated.")
                            }
                        },
                        onCancel = { currentTab = NavTab.HOME }
                    )
                }
                NavTab.TRACK -> {
                    TrackingScreen(
                        viewModel = viewModel,
                        onBack = { currentTab = NavTab.HOME },
                        onBookNewClick = { currentTab = NavTab.BOOKINGS }
                    )
                }
                NavTab.VEHICLES -> {
                    VehiclesGarageScreen(
                        viewModel = viewModel,
                        onBookServiceForVehicle = { vehicle ->
                            viewModel.selectVehicle(vehicle)
                            currentTab = NavTab.BOOKINGS
                        }
                    )
                }
                NavTab.PROFILE -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onLogoutSuccess = onLogout
                    )
                }
            }
        }
    }
}

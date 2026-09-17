package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldDark
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.theme.TorqNavyCard
import com.example.ui.theme.TorqNavyCardBorder
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextDark
import com.example.ui.theme.TorqTextPrimary

import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.outlined.DirectionsCar

enum class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    BOOKINGS("Bookings", Icons.Filled.Build, Icons.Outlined.Build),
    TRACK("Track", Icons.Filled.LocationOn, Icons.Outlined.LocationOn),
    VEHICLES("Vehicles", Icons.Filled.DirectionsCar, Icons.Outlined.DirectionsCar),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person);

    companion object {
        // Backwards compatibility aliases for existing references if any
        val BOOK = BOOKINGS
        val TRACKING = TRACK
        val HISTORY = VEHICLES
    }
}

@Composable
fun TorqfixBottomNav(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    hasActiveBooking: Boolean = false,
    unreadNotifications: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = TorqNavyCard,
        shadowElevation = 12.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, TorqNavyCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val visibleTabs = listOf(NavTab.HOME, NavTab.BOOKINGS, NavTab.TRACK, NavTab.VEHICLES, NavTab.PROFILE)
            visibleTabs.forEach { tab ->
                val isSelected = tab == selectedTab

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 6.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (tab == NavTab.TRACK && hasActiveBooking) {
                                Badge(
                                    containerColor = TorqGold,
                                    modifier = Modifier.size(7.dp)
                                )
                            } else if (tab == NavTab.PROFILE && unreadNotifications > 0) {
                                Badge(
                                    containerColor = TorqGold,
                                    contentColor = TorqTextDark
                                ) {
                                    Text(
                                        text = "$unreadNotifications",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            tint = if (isSelected) TorqGold else TorqSlateMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = tab.title,
                        color = if (isSelected) TorqGold else TorqSlateMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

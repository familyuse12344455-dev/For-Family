package com.example.ui.screens.tracking

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.util.UiFormatters
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookingEntity
import com.example.data.models.BookingStatus
import com.example.ui.components.BookingStatusBadge
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldCard
import com.example.ui.components.StatusProgressTimeline
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.interactive.WorkshopBayCamSheet
import com.example.ui.components.TorqfixOutlinedButton
import com.example.ui.components.TrackingMapCanvas
import com.example.ui.theme.TorqAmber
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldBorder
import com.example.ui.theme.TorqGoldLight
import com.example.ui.theme.TorqGoldSurface
import com.example.ui.theme.TorqGreen
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.theme.TorqNavyCard
import com.example.ui.theme.TorqNavyCardBorder
import com.example.ui.theme.TorqNavyCardElevated
import com.example.ui.theme.TorqNavySurface
import com.example.ui.theme.TorqSky
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextDark
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary
import com.example.ui.viewmodel.TorqfixViewModel

@Composable
fun TrackingScreen(
    viewModel: TorqfixViewModel,
    bookingIdFilter: String? = null,
    onBack: () -> Unit,
    onBookNewClick: () -> Unit
) {
    val activeBooking by viewModel.activeBooking.collectAsState()
    val repairPhotos by viewModel.repairPhotos.collectAsState()
    var showBayCamSheet by remember { mutableStateOf(false) }

    val booking = activeBooking
    val scrollState = rememberScrollState()

    LaunchedEffect(booking?.bookingId) {
        viewModel.loadRepairPhotos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TorqNavyBackground)
            .statusBarsPadding()
    ) {
        // === Top Bar ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TorqGold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Live Vehicle Tracking",
                    color = TorqTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (booking != null) "Booking #${UiFormatters.formatDisplayBookingNumber(booking.bookingId)}" else "Automotive Concierge",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (booking != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TorqNavyCard,
                    border = BorderStroke(1.dp, TorqNavyCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(TorqGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "REAL-TIME SYNC",
                            color = TorqGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        AnimatedContent(
            targetState = booking,
            transitionSpec = {
                fadeIn(tween(400, easing = FastOutSlowInEasing))
                    .togetherWith(fadeOut(tween(300, easing = FastOutSlowInEasing)))
            },
            label = "trackingScreenState"
        ) { currentBooking ->
            if (currentBooking == null) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TorqNavyCard,
                        border = BorderStroke(1.5.dp, TorqGoldBorder),
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = TorqGold,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "No Active Service In Progress",
                        color = TorqTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Book a white-glove repair or maintenance to view real-time driver telemetry, workshop diagnostic cameras and delivery tracking.",
                        color = TorqTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    TorqfixGoldButton(
                        text = "Book a Repair Now",
                        onClick = onBookNewClick,
                        icon = Icons.Default.Build
                    )
                }
            } else {
                val currentStatus = BookingStatus.fromString(currentBooking.status)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .padding(bottom = 80.dp)
                ) {
                    // === Interactive Automotive GPS Map Canvas with Floating Badge ===
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        TrackingMapCanvas(
                            status = currentStatus,
                            driverName = currentBooking.driverName,
                            etaMinutes = currentBooking.driverEtaMinutes
                        )

                        // Floating Distance & ETA pill over map
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(14.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = TorqNavyCard.copy(alpha = 0.92f),
                            border = BorderStroke(1.dp, TorqGoldBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = TorqGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentBooking.driverEtaMinutes != null) "${currentBooking.driverEtaMinutes} MINS • 3.2 KM" else "LIVE GPS",
                                    color = TorqGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // === Floating Live Status & Driver Info Card ===
                    LuxuryGoldCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedContent(
                                    targetState = currentStatus,
                                    transitionSpec = {
                                        (slideInVertically(tween(350, easing = FastOutSlowInEasing)) { it / 3 } + fadeIn(tween(350)))
                                            .togetherWith(slideOutVertically(tween(250, easing = FastOutSlowInEasing)) { -it / 3 } + fadeOut(tween(200)))
                                    },
                                    label = "statusHeadline",
                                    modifier = Modifier.weight(1f)
                                ) { status ->
                                    Column {
                                        Text(
                                            text = if (currentBooking.driverEtaMinutes != null && (status == BookingStatus.DRIVER_ASSIGNED || status == BookingStatus.RETURNING)) {
                                                "Your vehicle is ~${currentBooking.driverEtaMinutes} min away"
                                            } else {
                                                status.displayName
                                            },
                                            color = TorqTextPrimary,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${currentBooking.vehicleName} • ${currentBooking.vehiclePlate}",
                                            color = TorqTextSecondary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                BookingStatusBadge(status = currentStatus)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Driver Details
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TorqNavySurface,
                                border = BorderStroke(1.dp, TorqNavyCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(TorqNavyCardElevated)
                                            .border(1.dp, TorqGold, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = TorqGold,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = currentBooking.driverName ?: "Muhammad A. (Assigned)",
                                            color = TorqTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = currentBooking.driverVehicle ?: "Flatbed Carrier • 4.9 ★ Rating",
                                            color = TorqTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = CircleShape,
                                            color = TorqNavyCard,
                                            border = BorderStroke(1.dp, TorqGoldBorder),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Call,
                                                    contentDescription = "Call Driver",
                                                    tint = TorqGold,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = CircleShape,
                                            color = TorqNavyCard,
                                            border = BorderStroke(1.dp, TorqSlateBorder),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Chat,
                                                    contentDescription = "Chat",
                                                    tint = TorqTextPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // === Workshop Connection Card (when vehicle reached/repairing/testing) ===
                    AnimatedVisibility(
                        visible = currentStatus.stepIndex >= BookingStatus.WORKSHOP_RECEIVED.stepIndex,
                        enter = fadeIn(tween(400)) + expandVertically(tween(450, easing = FastOutSlowInEasing)),
                        exit = fadeOut(tween(250)) + shrinkVertically(tween(300))
                    ) {
                        Column {
                            LuxuryCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = TorqNavyCard,
                                borderColor = TorqNavyCardBorder
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Authorized TORQFIX Workshop Hub",
                                            color = TorqTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = TorqNavySurface,
                                            border = BorderStroke(1.dp, TorqSlateBorder)
                                        ) {
                                            Text(
                                                text = "BAY #04",
                                                color = TorqSky,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(TorqNavySurface)
                                                .border(1.dp, TorqSky, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Store,
                                                contentDescription = null,
                                                tint = TorqSky,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = currentBooking.workshopName ?: "TORQFIX Elite Hub — DHA Phase 6, Lahore",
                                                color = TorqTextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Lead Tech: ${currentBooking.workshopMechanic ?: "Ustad Jamil Akhtar"}",
                                                color = TorqGold,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = currentBooking.workshopAddress ?: "Sector H, Commercial Area, DHA Phase 6",
                                                color = TorqSlateGrey,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    TorqfixGoldButton(
                                        text = "Live Bay Cam & Diagnostic Photos",
                                        onClick = { showBayCamSheet = true },
                                        icon = Icons.Default.Videocam,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    StatusProgressTimeline(
                        currentStatus = currentStatus,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showBayCamSheet && booking != null) {
            WorkshopBayCamSheet(
                onDismiss = { showBayCamSheet = false },
                vehicleName = booking.vehicleName,
                workshopName = booking.workshopName ?: "TORQFIX Master Hub (DHA Phase 5, Lahore)",
                repairPhotos = repairPhotos
            )
        }
    }
}

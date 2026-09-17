package com.example.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.BookingStatus
import com.example.ui.components.BookingStatusBadge
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.components.TorqfixOutlinedButton
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldBorder
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
import com.example.ui.theme.TorqTextMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary
import com.example.ui.util.UiFormatters
import com.example.ui.viewmodel.TorqfixViewModel

/**
 * TORQFIX CUSTOMER HOME SCREEN (2026 Redesign)
 *
 * Designed around three core customer answers:
 * 1. BOOK A REPAIR
 * 2. WHERE IS MY CAR?
 * 3. WHAT IS HAPPENING TO MY CAR?
 *
 * Structure:
 * A. Minimal Top Header (Location, Greeting, Notifications, Profile)
 * B. Cinematic Hero Section ("Your car deserves better.", CTA: BOOK A REPAIR, TRACK MY VEHICLE)
 * C. Active Repair (Single prominent card with live progress timeline & driver/workshop info)
 * D. Quick Actions (Book Repair, Track Vehicle, My Vehicles, Service History)
 * E. Services (Clean horizontal carousel of genuine major automotive services)
 * F. Trust & Premium Pillars (Certified Master Technicians, Flatbed Concierge, Digital Warranty)
 */
@Composable
fun HomeScreen(
    viewModel: TorqfixViewModel,
    onBookRepairClick: () -> Unit,
    onTrackBookingClick: (bookingId: String) -> Unit,
    onViewHistoryClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSupportClick: () -> Unit,
    onManageVehiclesClick: () -> Unit = onViewHistoryClick
) {
    val customer by viewModel.currentCustomer.collectAsState()
    val activeBooking by viewModel.activeBooking.collectAsState()
    val unreadCount by viewModel.unreadNotifCount.collectAsState()

    val scrollState = rememberScrollState()

    val customerFirstName = customer?.name?.trim()?.split(" ")?.firstOrNull() ?: "Hamza"
    val customerCity = customer?.city ?: "Lahore"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TorqNavyBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp)
    ) {
        // =====================================================================
        // SECTION A: TOP HEADER (Clean, modern, minimal)
        // =====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TorqGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$customerCity, Pakistan",
                        color = TorqTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Good afternoon, $customerFirstName",
                    color = TorqTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Notification Icon
                Surface(
                    shape = CircleShape,
                    color = TorqNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TorqNavyCardBorder),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = TorqGold,
                                        contentColor = TorqTextDark
                                    ) {
                                        Text(
                                            text = "$unreadCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = TorqTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Profile Avatar Shortcut
                Surface(
                    shape = CircleShape,
                    color = TorqNavyCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TorqGoldBorder),
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onNotificationsClick() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = customerFirstName.take(1).uppercase(),
                            color = TorqGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // =====================================================================
        // SECTION B: CINEMATIC HERO SECTION
        // =====================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, TorqNavyCardBorder, RoundedCornerShape(22.dp))
                .background(TorqNavyCard)
        ) {
            // High-Resolution Cinematic Vehicle Artwork
            Image(
                painter = painterResource(id = R.drawable.torqfix_luxury_car_1789152555611),
                contentDescription = "Torqfix Luxury Vehicle Care",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            // Subtle Dark Gradient Overlay (Luxury Lighting)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x2207111F),
                                Color(0x9907111F),
                                TorqNavyBackground
                            )
                        )
                    )
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .align(Alignment.BottomStart)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TorqGoldSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TorqGoldBorder)
                ) {
                    Text(
                        text = "ON-DEMAND AUTOMOTIVE CARE",
                        color = TorqGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your car deserves better.",
                    color = TorqTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Premium repair, pickup & delivery — all handled by Torqfix.",
                    color = TorqTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Primary CTA & Secondary Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TorqfixGoldButton(
                        text = "BOOK A REPAIR",
                        onClick = onBookRepairClick,
                        modifier = Modifier.weight(1.3f)
                    )

                    TorqfixOutlinedButton(
                        text = "TRACK VEHICLE",
                        onClick = {
                            if (activeBooking != null) {
                                onTrackBookingClick(activeBooking!!.bookingId)
                            } else {
                                onTrackBookingClick("LIVE")
                            }
                        },
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // =====================================================================
        // SECTION C: ACTIVE REPAIR (Single Prominent Card)
        // =====================================================================
        if (activeBooking != null) {
            val booking = activeBooking!!
            val status = BookingStatus.fromString(booking.status)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Repair",
                        color = TorqTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Booking #${UiFormatters.formatDisplayBookingNumber(booking.bookingId)}",
                        color = TorqGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LuxuryGoldCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onTrackBookingClick(booking.bookingId) }
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Vehicle Header & Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "YOUR VEHICLE",
                                    color = TorqGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = booking.vehicleName,
                                    color = TorqTextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${booking.service} • ${booking.vehiclePlate}",
                                    color = TorqTextSecondary,
                                    fontSize = 13.sp
                                )
                            }

                            BookingStatusBadge(status = status)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Clean 5-Stage Repair Progress Bar
                        RepairProgressPills(currentStatus = status)

                        Spacer(modifier = Modifier.height(18.dp))

                        // Details: Workshop, Driver, Estimated Arrival
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TorqNavySurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, TorqNavyCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (booking.driverName != null) Icons.Default.LocalShipping else Icons.Default.Store,
                                        contentDescription = null,
                                        tint = TorqGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = booking.driverName?.let { "Driver: $it" } ?: (booking.workshopName ?: "TORQFIX Elite Bay"),
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = status.description,
                                            color = TorqTextSecondary,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (booking.driverEtaMinutes != null) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = TorqGoldSurface,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, TorqGoldBorder)
                                    ) {
                                        Text(
                                            text = "~${booking.driverEtaMinutes} MINS",
                                            color = TorqGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Main Card Action
                        TorqfixGoldButton(
                            text = "TRACK VEHICLE",
                            icon = Icons.Default.LocationOn,
                            onClick = { onTrackBookingClick(booking.bookingId) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // =====================================================================
        // SECTION D: QUICK ACTIONS (3-4 Clean Cards)
        // =====================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Quick Actions",
                color = TorqTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Book Repair",
                    subtitle = "Get your car serviced",
                    icon = Icons.Default.Build,
                    modifier = Modifier.weight(1f),
                    onClick = onBookRepairClick
                )

                QuickActionCard(
                    title = "Track Vehicle",
                    subtitle = "See your car in real time",
                    icon = Icons.Default.LocationOn,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (activeBooking != null) {
                            onTrackBookingClick(activeBooking!!.bookingId)
                        } else {
                            onTrackBookingClick("LIVE")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "My Vehicles",
                    subtitle = "Manage your cars",
                    icon = Icons.Default.DirectionsCar,
                    modifier = Modifier.weight(1f),
                    onClick = onManageVehiclesClick
                )

                QuickActionCard(
                    title = "Service History",
                    subtitle = "View previous repairs",
                    icon = Icons.Default.History,
                    modifier = Modifier.weight(1f),
                    onClick = onViewHistoryClick
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // =====================================================================
        // SECTION E: SERVICES (Clean Horizontal Carousel)
        // =====================================================================
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Services",
                    color = TorqTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Explore All",
                    color = TorqGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBookRepairClick() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val majorServices = listOf(
                MajorServiceItem("General Repair", "Engine, suspension & parts", Icons.Default.Build),
                MajorServiceItem("Oil & Maintenance", "Filter, fluids & inspection", Icons.Default.Speed),
                MajorServiceItem("Brakes", "Pads, rotors & hydraulics", Icons.Default.Shield),
                MajorServiceItem("AC Service", "Refrigerant & cabin climate", Icons.Default.AcUnit),
                MajorServiceItem("Diagnostics", "OBD-II & full telemetry", Icons.Default.Sensors),
                MajorServiceItem("Body & Paint", "Scratch repair & ceramic coat", Icons.Default.ColorLens)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(majorServices) { item ->
                    ServiceCarouselCard(
                        service = item,
                        onClick = {
                            viewModel.selectService(item.name)
                            onBookRepairClick()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // =====================================================================
        // SECTION F: TRUST & PREMIUM INFORMATION (3 Clean Pillars)
        // =====================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "The Torqfix Guarantee",
                color = TorqTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            TrustPillarCard(
                title = "Certified Master Technicians",
                description = "ASE and manufacturer-certified specialists using genuine OEM parts and precision calibration.",
                icon = Icons.Default.VerifiedUser
            )

            Spacer(modifier = Modifier.height(10.dp))

            TrustPillarCard(
                title = "Flatbed Doorstep Concierge",
                description = "Zero-mile flatbed recovery and delivery directly to your home or office, fully insured.",
                icon = Icons.Default.LocalShipping
            )

            Spacer(modifier = Modifier.height(10.dp))

            TrustPillarCard(
                title = "Transparent Digital Inspection",
                description = "Live digital health scores, high-res photographic findings, and an official 6-month repair warranty.",
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

// =============================================================================
// SUPPORTING COMPONENTS
// =============================================================================

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    LuxuryCard(
        modifier = modifier.height(118.dp),
        backgroundColor = TorqNavyCard,
        borderColor = TorqNavyCardBorder,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = TorqNavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, TorqSlateBorder),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = TorqGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    color = TorqTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TorqTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class MajorServiceItem(
    val name: String,
    val description: String,
    val icon: ImageVector
)

@Composable
private fun ServiceCarouselCard(
    service: MajorServiceItem,
    onClick: () -> Unit
) {
    LuxuryCard(
        modifier = Modifier
            .width(168.dp)
            .height(138.dp),
        backgroundColor = TorqNavyCard,
        borderColor = TorqNavyCardBorder,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TorqNavySurface,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = service.icon,
                        contentDescription = service.name,
                        tint = TorqGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column {
                Text(
                    text = service.name,
                    color = TorqTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = service.description,
                    color = TorqTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TrustPillarCard(
    title: String,
    description: String,
    icon: ImageVector
) {
    LuxuryCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TorqNavyCard,
        borderColor = TorqNavyCardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = TorqGoldSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, TorqGoldBorder),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TorqGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TorqTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = TorqTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * Clean 5-Stage Repair Progress Bar:
 * Vehicle Picked → Workshop → Repairing → Testing → Returning
 */
@Composable
private fun RepairProgressPills(currentStatus: BookingStatus) {
    val stages = listOf(
        Pair("Picked", BookingStatus.VEHICLE_PICKED),
        Pair("Workshop", BookingStatus.WORKSHOP_RECEIVED),
        Pair("Repairing", BookingStatus.REPAIRING),
        Pair("Testing", BookingStatus.TESTING),
        Pair("Returning", BookingStatus.RETURNING)
    )

    val currentStep = currentStatus.stepIndex

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, (label, status) ->
            val isCompleted = currentStep > status.stepIndex
            val isCurrent = currentStep == status.stepIndex

            val pillColor by animateColorAsState(
                targetValue = when {
                    isCompleted -> TorqGreen
                    isCurrent -> TorqGold
                    else -> TorqSlateBorder
                },
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "pillColor_$index"
            )

            val labelColor by animateColorAsState(
                targetValue = when {
                    isCurrent -> TorqGold
                    isCompleted -> TorqTextPrimary
                    else -> TorqTextMuted
                },
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                label = "pillLabelColor_$index"
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(pillColor)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = label,
                    color = labelColor,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookingStatus
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldLight
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.theme.TorqNavyCard
import com.example.ui.theme.TorqNavyCardBorder
import com.example.ui.theme.TorqSky
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary

@Composable
fun TrackingMapCanvas(
    status: BookingStatus,
    driverName: String?,
    etaMinutes: Int?,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 54f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRadius"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAlpha"
    )

    val progressFraction = when (status) {
        BookingStatus.PENDING_DRIVER -> 0.05f
        BookingStatus.DRIVER_ASSIGNED -> 0.25f
        BookingStatus.VEHICLE_PICKED -> 0.45f
        BookingStatus.WORKSHOP_RECEIVED,
        BookingStatus.REPAIRING,
        BookingStatus.TESTING,
        BookingStatus.READY_FOR_RETURN -> 0.70f
        BookingStatus.RETURNING -> 0.88f
        BookingStatus.COMPLETED -> 1.0f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color(0xFF071226), RoundedCornerShape(20.dp))
            .border(1.2.dp, TorqNavyCardBorder, RoundedCornerShape(20.dp))
    ) {
        // High-Tech Automotive Vector Map
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Grid Background
            val step = 40.dp.toPx()
            var x = 0f
            while (x < width) {
                drawLine(
                    color = Color(0x153A5A90),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += step
            }
            var y = 0f
            while (y < height) {
                drawLine(
                    color = Color(0x153A5A90),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += step
            }

            // 2. City Road Vectors (Stylized roads in Lahore)
            val roadPaint = Color(0x2A4E79B5)
            drawLine(roadPaint, Offset(0f, height * 0.35f), Offset(width, height * 0.45f), strokeWidth = 8f)
            drawLine(roadPaint, Offset(0f, height * 0.75f), Offset(width, height * 0.65f), strokeWidth = 6f)
            drawLine(roadPaint, Offset(width * 0.25f, 0f), Offset(width * 0.35f, height), strokeWidth = 5f)
            drawLine(roadPaint, Offset(width * 0.75f, 0f), Offset(width * 0.68f, height), strokeWidth = 7f)

            // 3. Service Journey Curved Route (Pickup -> Workshop -> Return)
            val pickupPt = Offset(width * 0.18f, height * 0.68f)
            val controlPt1 = Offset(width * 0.35f, height * 0.25f)
            val workshopPt = Offset(width * 0.55f, height * 0.32f)
            val controlPt2 = Offset(width * 0.75f, height * 0.75f)
            val dropoffPt = Offset(width * 0.86f, height * 0.40f)

            val fullRoute = Path().apply {
                moveTo(pickupPt.x, pickupPt.y)
                quadraticTo(controlPt1.x, controlPt1.y, workshopPt.x, workshopPt.y)
                quadraticTo(controlPt2.x, controlPt2.y, dropoffPt.x, dropoffPt.y)
            }

            // Glow Underlay for Route
            drawPath(
                path = fullRoute,
                brush = Brush.horizontalGradient(
                    colors = listOf(TorqGold.copy(alpha = 0.25f), TorqSky.copy(alpha = 0.3f), TorqGold.copy(alpha = 0.25f))
                ),
                style = Stroke(width = 12f)
            )

            // Main Route Line
            drawPath(
                path = fullRoute,
                brush = Brush.horizontalGradient(
                    colors = listOf(TorqGold, TorqSky, TorqGoldLight)
                ),
                style = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
                )
            )

            // 4. Pickup Location Marker (Point A)
            drawCircle(Color(0x44FFD700), radius = 18f, center = pickupPt)
            drawCircle(TorqGold, radius = 8f, center = pickupPt)

            // 5. Workshop Location Marker (TORQFIX Hub)
            drawCircle(Color(0x4438BDF8), radius = 22f, center = workshopPt)
            drawCircle(TorqSky, radius = 9f, center = workshopPt)

            // 6. Destination Return Pin
            drawCircle(Color(0x3310B981), radius = 16f, center = dropoffPt)
            drawCircle(Color(0xFF10B981), radius = 7f, center = dropoffPt)

            // 7. Calculate Interpolated Driver Live Marker Position along journey
            val driverPos = when {
                progressFraction <= 0.5f -> {
                    val t = progressFraction / 0.5f
                    val invT = 1f - t
                    Offset(
                        invT * invT * pickupPt.x + 2 * invT * t * controlPt1.x + t * t * workshopPt.x,
                        invT * invT * pickupPt.y + 2 * invT * t * controlPt1.y + t * t * workshopPt.y
                    )
                }
                else -> {
                    val t = (progressFraction - 0.5f) / 0.5f
                    val invT = 1f - t
                    Offset(
                        invT * invT * workshopPt.x + 2 * invT * t * controlPt2.x + t * t * dropoffPt.x,
                        invT * invT * workshopPt.y + 2 * invT * t * controlPt2.y + t * t * dropoffPt.y
                    )
                }
            }

            // Draw Pulsing Live Radar Rings around Driver Position
            drawCircle(
                color = TorqGold.copy(alpha = radarAlpha),
                radius = radarRadius,
                center = driverPos,
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = TorqNavyBackground,
                radius = 16f,
                center = driverPos
            )
            drawCircle(
                color = TorqGold,
                radius = 13f,
                center = driverPos
            )
            drawCircle(
                color = TorqNavyBackground,
                radius = 5f,
                center = driverPos
            )
        }

        // Top Left: Live Status Pill
        Surface(
            modifier = Modifier
                .padding(14.dp)
                .align(Alignment.TopStart),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD0B1B3A),
            border = androidx.compose.foundation.BorderStroke(1.dp, TorqNavyCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(TorqGold, CircleShape)
                )
                Text(
                    text = "GPS LIVE TELEMETRY",
                    color = TorqGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Top Right: Recenter / Speed
        Surface(
            modifier = Modifier
                .padding(14.dp)
                .align(Alignment.TopEnd),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD0B1B3A),
            border = androidx.compose.foundation.BorderStroke(1.dp, TorqNavyCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = "GPS Lock",
                    tint = TorqSky,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "48 km/h • DHA Phase 6",
                    color = TorqTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Map Overlays (Location Legend: Pickup & Workshop)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xEE0B1B3A),
                border = androidx.compose.foundation.BorderStroke(1.dp, TorqSlateBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = TorqGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Pickup (DHA Ph 5)",
                        color = TorqTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xEE0B1B3A),
                border = androidx.compose.foundation.BorderStroke(1.dp, TorqSlateBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = TorqSky,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "TORQFIX Elite Hub",
                        color = TorqSky,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

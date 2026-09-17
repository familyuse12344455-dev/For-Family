package com.example.ui.interactive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.PakistaniCarsCatalog
import com.example.data.models.VehicleEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.components.TorqfixOutlinedButton
import com.example.ui.theme.TorqAmber
import com.example.ui.theme.TorqAmberSurface
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldBorder
import com.example.ui.theme.TorqGoldDark
import com.example.ui.theme.TorqGoldSurface
import com.example.ui.theme.TorqGreen
import com.example.ui.theme.TorqGreenSurface
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.theme.TorqNavyCard
import com.example.ui.theme.TorqNavyCardBorder
import com.example.ui.theme.TorqNavyCardElevated
import com.example.ui.theme.TorqNavySurface
import com.example.ui.theme.TorqRed
import com.example.ui.theme.TorqRedSurface
import com.example.ui.theme.TorqSky
import com.example.ui.theme.TorqSkySurface
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CarComponentStatus(
    val id: String,
    val name: String,
    val category: String,
    val healthPercent: Int,
    val statusText: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val metrics: List<Pair<String, String>>,
    val advisoryNote: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcuDiagnosticScannerSheet(
    onDismiss: () -> Unit,
    vehicles: List<VehicleEntity>,
    onBookRepairWithDtc: (dtcCode: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableStateOf(1f) }
    var lastScannedTime by remember { mutableStateOf("Just Now (Live)") }

    var selectedVehicle by remember {
        mutableStateOf(vehicles.firstOrNull()?.fullName ?: "Honda Civic Reborn (1.8 i-VTEC)")
    }

    var activeDtcCodes by remember {
        mutableStateOf(
            listOf(
                Pair("P0136", "O2 Sensor Circuit High Voltage (Bank 1 Sensor 2) - Minor Calibration Required"),
                Pair("B1044", "HVAC Cabin Microfilter Flow Restriction (Air Flow 88%)")
            )
        )
    }

    val components = listOf(
        CarComponentStatus(
            id = "ENGINE",
            name = "Twin-Turbo 3.0L V6",
            category = "Powertrain",
            healthPercent = 96,
            statusText = "Optimal & Calibrated",
            icon = Icons.Default.Speed,
            primaryColor = TorqGreen,
            metrics = listOf(
                "Coolant Temp" to "89°C (Normal)",
                "Oil Pressure" to "42.5 PSI @ 1,800 RPM",
                "Turbo Boost" to "1.35 Bar Peak",
                "Misfires" to "0 Cylinders"
            ),
            advisoryNote = "Direct fuel injectors & spark plugs in pristine condition. Recommended oil change in 4,200 km."
        ),
        CarComponentStatus(
            id = "BRAKES",
            name = "Dynamic Disc Brakes",
            category = "Safety & Chassis",
            healthPercent = 84,
            statusText = "Good Condition",
            icon = Icons.Default.Security,
            primaryColor = TorqSky,
            metrics = listOf(
                "Front Pad Thickness" to "8.2 mm (82%)",
                "Rear Pad Thickness" to "7.9 mm (79%)",
                "Brake Fluid Temp" to "41°C",
                "ABS Module" to "Online & Synchronized"
            ),
            advisoryNote = "Brake pad wear is uniform. Disc rotors have 0.02mm lateral runout (well within factory tolerance)."
        ),
        CarComponentStatus(
            id = "BATTERY",
            name = "12V 95Ah AGM Battery",
            category = "Electrical System",
            healthPercent = 92,
            statusText = "Strong Charge (14.2V)",
            icon = Icons.Default.Bolt,
            primaryColor = TorqGold,
            metrics = listOf(
                "Resting Voltage" to "12.7 V",
                "Alternator Output" to "14.2 V Under Load",
                "Cold Cranking Amps" to "850 CCA",
                "Parasitic Draw" to "0.02 A (Normal)"
            ),
            advisoryNote = "Battery internal resistance 3.4 mΩ. Micro-hybrid start/stop ready and active."
        ),
        CarComponentStatus(
            id = "HVAC",
            name = "Dual-Zone Climate HVAC",
            category = "Comfort",
            healthPercent = 88,
            statusText = "Advisory Service Due",
            icon = Icons.Default.AcUnit,
            primaryColor = TorqAmber,
            metrics = listOf(
                "Vent Delivery Temp" to "5.1°C (Very Cold)",
                "Refrigerant Pressure" to "185 PSI High / 32 Low",
                "Cabin Filter" to "Minor Dust (Filter Code B1044)",
                "Compressor Clutch" to "Engaged Smoothly"
            ),
            advisoryNote = "AC cooling is freezing cold, but cabin pollen filter has collected Lahore smog particulate."
        ),
        CarComponentStatus(
            id = "TRANSMISSION",
            name = "7-Speed S-Tronic Dual Clutch",
            category = "Drivetrain",
            healthPercent = 94,
            statusText = "Smooth Engagement",
            icon = Icons.Default.Build,
            primaryColor = TorqGreen,
            metrics = listOf(
                "Fluid Temperature" to "78°C",
                "Clutch 1 Wear" to "12% Total",
                "Clutch 2 Wear" to "14% Total",
                "Mechatronics Solenoid" to "Optimal Response"
            ),
            advisoryNote = "Shift response instantaneous at 0.14 seconds. Gear teeth and synchronizers healthy."
        )
    )

    var selectedComponent by remember { mutableStateOf(components[0]) }

    // Radar scan transition
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    fun startDeepScan() {
        coroutineScope.launch {
            isScanning = true
            scanProgress = 0f
            while (scanProgress < 1f) {
                delay(120)
                scanProgress += 0.08f
            }
            scanProgress = 1f
            isScanning = false
            lastScannedTime = "Just Now (100% Complete)"
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyBackground,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TorqGoldSurface)
                            .border(1.dp, TorqGoldBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = TorqGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Live ECU Diagnostics",
                            color = TorqTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "TorqLink™ OBD-II Wireless Telemetry",
                            color = TorqSky,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TorqSlateGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vehicles) { v ->
                    val isSelected = selectedVehicle == v.fullName
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) TorqGoldSurface else TorqNavyCard,
                        border = BorderStroke(1.dp, if (isSelected) TorqGold else TorqNavyCardBorder),
                        modifier = Modifier.clickable { selectedVehicle = v.fullName }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = if (isSelected) TorqGold else TorqSlateGrey,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = v.fullName,
                                color = if (isSelected) TorqGold else TorqTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                items(PakistaniCarsCatalog.allCars.take(8)) { pkCar ->
                    val carName = "${pkCar.brand} ${pkCar.modelName}"
                    val isSelected = selectedVehicle.contains(pkCar.modelName)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) TorqGoldSurface else TorqNavyCard,
                        border = BorderStroke(1.dp, if (isSelected) TorqGold else TorqNavyCardBorder),
                        modifier = Modifier.clickable {
                            selectedVehicle = "$carName (${pkCar.pakistaniBadge})"
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = if (isSelected) TorqGold else TorqSlateGrey,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = carName,
                                color = if (isSelected) TorqGold else TorqTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Radar Visual Scan Centerpiece
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TorqNavyCardElevated,
                borderColor = if (isScanning) TorqSky else TorqGoldBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.minDimension / 2f

                            // Radar concentric rings
                            drawCircle(
                                color = TorqNavyCardBorder,
                                radius = radius,
                                style = Stroke(width = 1.5f)
                            )
                            drawCircle(
                                color = TorqNavyCardBorder.copy(alpha = 0.6f),
                                radius = radius * 0.7f,
                                style = Stroke(width = 1f)
                            )
                            drawCircle(
                                color = TorqNavyCardBorder.copy(alpha = 0.4f),
                                radius = radius * 0.4f,
                                style = Stroke(width = 1f)
                            )

                            // Crosshairs
                            drawLine(
                                color = TorqNavyCardBorder.copy(alpha = 0.5f),
                                start = Offset(center.x, 0f),
                                end = Offset(center.x, size.height),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = TorqNavyCardBorder.copy(alpha = 0.5f),
                                start = Offset(0f, center.y),
                                end = Offset(size.width, center.y),
                                strokeWidth = 1f
                            )

                            // Animated Radar Sweep Arc
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(
                                        Color.Transparent,
                                        TorqSky.copy(alpha = 0.05f),
                                        TorqSky.copy(alpha = 0.45f)
                                    )
                                ),
                                startAngle = sweepAngle - 60f,
                                sweepAngle = 60f,
                                useCenter = true
                            )

                            // Active scanner pulse dot
                            drawCircle(
                                color = TorqGold,
                                radius = 4.dp.toPx(),
                                center = center
                            )
                        }

                        // Car Silhouette in Center
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = if (isScanning) TorqSky else TorqGold,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isScanning) "${(scanProgress * 100).toInt()}% SCAN" else "ECU ONLINE",
                                color = if (isScanning) TorqSky else TorqGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Vehicle Health Index: 92% • EXCELLENT",
                        color = TorqTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Last synced: $lastScannedTime • 48 Sensors Online",
                        color = TorqSlateGrey,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TorqfixGoldButton(
                            text = if (isScanning) "Scanning Sensors..." else "Run Deep ECU Scan",
                            onClick = { startDeepScan() },
                            modifier = Modifier.weight(1f),
                            enabled = !isScanning,
                            icon = Icons.Default.Refresh
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Component Quick Hotspot Chips
            Text(
                text = "Interactive Component Inspection",
                color = TorqTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap any module to view live temperature, pressure and wear status",
                color = TorqSlateGrey,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(components) { comp ->
                    val isSelected = selectedComponent.id == comp.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) TorqGoldSurface else TorqNavyCard,
                        border = BorderStroke(1.2.dp, if (isSelected) TorqGold else TorqNavyCardBorder),
                        modifier = Modifier.clickable { selectedComponent = comp }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = comp.icon,
                                contentDescription = null,
                                tint = if (isSelected) TorqGold else comp.primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = comp.name,
                                    color = if (isSelected) TorqGold else TorqTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${comp.healthPercent}% • ${comp.statusText}",
                                    color = if (isSelected) TorqGold.copy(alpha = 0.8f) else TorqSlateGrey,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Component Deep Dive Card
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TorqNavyCard,
                borderColor = TorqNavyCardBorder
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(selectedComponent.primaryColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = selectedComponent.icon,
                                    contentDescription = null,
                                    tint = selectedComponent.primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = selectedComponent.name,
                                    color = TorqTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedComponent.category,
                                    color = TorqSlateGrey,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = selectedComponent.primaryColor.copy(alpha = 0.12f),
                            border = BorderStroke(0.8.dp, selectedComponent.primaryColor.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "${selectedComponent.healthPercent}% Health",
                                color = selectedComponent.primaryColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Metrics Grid
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(TorqNavySurface)
                            .padding(12.dp)
                    ) {
                        selectedComponent.metrics.chunked(2).forEachIndexed { index, rowMetrics ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowMetrics.forEach { (label, value) ->
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = label,
                                            color = TorqSlateGrey,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = value,
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                            if (index == 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = TorqNavyCardBorder)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Mechanic AI Assessment:",
                        color = TorqGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedComponent.advisoryNote,
                        color = TorqTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Diagnostic Trouble Codes (DTC) Reader Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ECU Trouble Codes (${activeDtcCodes.size})",
                    color = TorqTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                if (activeDtcCodes.isNotEmpty()) {
                    Text(
                        text = "Clear All Codes",
                        color = TorqSky,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            activeDtcCodes = emptyList()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (activeDtcCodes.isNotEmpty()) {
                activeDtcCodes.forEach { (code, desc) ->
                    LuxuryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        backgroundColor = TorqNavyCard,
                        borderColor = TorqAmber.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TorqAmberSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = TorqAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DTC Code $code",
                                    color = TorqAmber,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = desc,
                                    color = TorqTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TorqfixOutlinedButton(
                                text = "Fix",
                                onClick = {
                                    onDismiss()
                                    onBookRepairWithDtc(code)
                                },
                                modifier = Modifier.width(64.dp),
                                borderColor = TorqGold
                            )
                        }
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TorqGreenSurface,
                    border = BorderStroke(1.dp, TorqGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = TorqGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Zero Fault Codes Detected",
                                color = TorqGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "OBD-II emissions, powertrain & brake control circuits are fault-free.",
                                color = TorqTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

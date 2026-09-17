package com.example.ui.interactive

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldBorder
import com.example.ui.theme.TorqGoldLight
import com.example.ui.theme.TorqGoldSurface
import com.example.ui.theme.TorqGreen
import com.example.ui.theme.TorqGreenSurface
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.theme.TorqNavyCard
import com.example.ui.theme.TorqNavyCardBorder
import com.example.ui.theme.TorqNavyCardElevated
import com.example.ui.theme.TorqNavySurface
import com.example.ui.theme.TorqSky
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary

data class PaintColor(
    val name: String,
    val color: Color,
    val finishDesc: String,
    val isPremium: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarPaintCustomizerSheet(
    onDismiss: () -> Unit,
    onBookCustomization: (packageName: String, estimatedPkr: String) -> Unit,
    initialVehicle: String = "Audi A6 Sedan"
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val paintColors = remember {
        listOf(
            PaintColor("Nardo Grey", Color(0xFF5A6268), "Audi RS Slate Gloss"),
            PaintColor("Midnight Royal Blue", Color(0xFF0D254C), "Deep Sapphire Metallic"),
            PaintColor("Satin Sovereign Gold", Color(0xFFC5A059), "Matte Champagne Gold", isPremium = true),
            PaintColor("British Racing Green", Color(0xFF0F382A), "High Gloss Metallic"),
            PaintColor("Obsidian Stealth Black", Color(0xFF18181B), "Piano Black Shadowline"),
            PaintColor("Frozen Ruby Red", Color(0xFF8B1E2E), "Satin Pearl Metallic", isPremium = true),
            PaintColor("Pearl White Diamond", Color(0xFFE2E8F0), "Tri-Coat Ceramic White")
        )
    }

    var selectedColor by remember { mutableStateOf(paintColors[0]) }
    var selectedFinish by remember { mutableStateOf("9H Ceramic Gloss") } // "9H Ceramic Gloss", "Stealth Matte PPF", "Satin Pearl"
    var hasWindowTint by remember { mutableStateOf(true) }
    var hasRedCalipers by remember { mutableStateOf(true) }
    var hasCarbonSpoiler by remember { mutableStateOf(false) }

    val animatedCarColor by animateColorAsState(
        targetValue = selectedColor.color,
        animationSpec = tween(500),
        label = "carColor"
    )

    // Calculate dynamic cost based on options
    val baseCost = when (selectedFinish) {
        "Stealth Matte PPF" -> 165000
        "Satin Pearl" -> 85000
        else -> 48000 // 9H Ceramic
    }
    val tintCost = if (hasWindowTint) 14000 else 0
    val caliperCost = if (hasRedCalipers) 12000 else 0
    val spoilerCost = if (hasCarbonSpoiler) 24000 else 0
    val totalCost = baseCost + tintCost + caliperCost + spoilerCost

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TorqGoldSurface,
                        border = BorderStroke(1.dp, TorqGoldBorder),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = null,
                                tint = TorqGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Virtual Paint & PPF Studio",
                            color = TorqTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Interactive Car Customizer & Detailing Lab",
                            color = TorqSlateGrey,
                            fontSize = 11.sp
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // Interactive Car Canvas Display
                LuxuryCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    backgroundColor = TorqNavyCard,
                    borderColor = TorqGoldBorder
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Studio light gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            animatedCarColor.copy(alpha = 0.25f),
                                            TorqNavyCard.copy(alpha = 0.9f)
                                        )
                                    )
                                )
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val baselineY = h * 0.72f

                            // Studio ground reflection grid
                            drawLine(
                                color = TorqGold.copy(alpha = 0.25f),
                                start = Offset(w * 0.05f, baselineY + 14f),
                                end = Offset(w * 0.95f, baselineY + 14f),
                                strokeWidth = 1.5f
                            )

                            // Car Body Silhouette Path (Luxury Fastback Sedan)
                            val carPath = Path().apply {
                                moveTo(w * 0.12f, baselineY) // Front bumper base
                                lineTo(w * 0.10f, baselineY - h * 0.15f) // Front grille
                                quadraticBezierTo(w * 0.15f, baselineY - h * 0.25f, w * 0.28f, baselineY - h * 0.28f) // Hood curve
                                quadraticBezierTo(w * 0.38f, baselineY - h * 0.58f, w * 0.58f, baselineY - h * 0.58f) // Windshield to roof
                                quadraticBezierTo(w * 0.74f, baselineY - h * 0.56f, w * 0.88f, baselineY - h * 0.22f) // Rear slope fastback
                                lineTo(w * 0.92f, baselineY - h * 0.14f) // Rear bumper
                                lineTo(w * 0.90f, baselineY) // Rear base
                                close()
                            }

                            // Fill Car Body with animated paint color
                            drawPath(
                                path = carPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        animatedCarColor.copy(alpha = 0.9f),
                                        animatedCarColor,
                                        animatedCarColor.copy(alpha = 0.7f)
                                    ),
                                    startY = baselineY - h * 0.6f,
                                    endY = baselineY
                                )
                            )

                            // Ceramic Gloss Reflection Highlight
                            if (selectedFinish == "9H Ceramic Gloss") {
                                drawPath(
                                    path = carPath,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.35f),
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.15f)
                                        ),
                                        start = Offset(w * 0.2f, baselineY - h * 0.6f),
                                        end = Offset(w * 0.7f, baselineY)
                                    )
                                )
                            }

                            // Cabin Window Glass (Tinted or Clear)
                            val windowPath = Path().apply {
                                moveTo(w * 0.35f, baselineY - h * 0.28f)
                                lineTo(w * 0.42f, baselineY - h * 0.52f)
                                lineTo(w * 0.57f, baselineY - h * 0.52f)
                                lineTo(w * 0.72f, baselineY - h * 0.28f)
                                close()
                            }
                            val glassColor = if (hasWindowTint) Color(0xDD090F1C) else Color(0x6638BDF8)
                            drawPath(windowPath, color = glassColor)

                            // Wheels (Front & Rear)
                            val frontWheelCenter = Offset(w * 0.26f, baselineY)
                            val rearWheelCenter = Offset(w * 0.76f, baselineY)
                            val wheelRadius = h * 0.14f

                            listOf(frontWheelCenter, rearWheelCenter).forEach { center ->
                                // Tire rubber
                                drawCircle(color = Color(0xFF111827), radius = wheelRadius, center = center)
                                // Alloy rim
                                drawCircle(
                                    color = Color(0xFF475569),
                                    radius = wheelRadius * 0.72f,
                                    center = center,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                                // Red Brembo / Gold Caliper
                                if (hasRedCalipers) {
                                    drawCircle(
                                        color = Color(0xFFEF4444),
                                        radius = wheelRadius * 0.45f,
                                        center = Offset(center.x - 6f, center.y - 4f)
                                    )
                                }
                                // Center cap
                                drawCircle(color = TorqGold, radius = 4.dp.toPx(), center = center)
                            }

                            // Carbon Spoiler (if enabled)
                            if (hasCarbonSpoiler) {
                                drawRoundRect(
                                    color = Color(0xFF1E293B),
                                    topLeft = Offset(w * 0.85f, baselineY - h * 0.28f),
                                    size = Size(w * 0.08f, 6.dp.toPx()),
                                    cornerRadius = CornerRadius(2f, 2f)
                                )
                            }

                            // Headlight & Taillight
                            drawCircle(color = TorqSky, radius = 5.dp.toPx(), center = Offset(w * 0.11f, baselineY - h * 0.18f))
                            drawCircle(color = Color(0xFFEF4444), radius = 5.dp.toPx(), center = Offset(w * 0.91f, baselineY - h * 0.18f))
                        }

                        // Vehicle Info Pill in Studio
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TorqNavySurface.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, TorqNavyCardBorder),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "$initialVehicle • ${selectedColor.name}",
                                color = TorqGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Current Finish Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TorqGoldSurface,
                            border = BorderStroke(1.dp, TorqGoldBorder),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                        ) {
                            Text(
                                text = selectedFinish.uppercase(),
                                color = TorqGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Color Selection Palette
                Text(
                    text = "SELECT BESPOKE COLORWAY",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(paintColors) { paint ->
                        val isSelected = paint.name == selectedColor.name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedColor = paint }
                                .background(if (isSelected) TorqNavyCardElevated else TorqNavyCard)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) TorqGold else TorqNavyCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(paint.color)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (paint.color == Color(0xFFE2E8F0)) Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = paint.name.split(" ").take(2).joinToString(" "),
                                color = if (isSelected) TorqTextPrimary else TorqSlateGrey,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Detailing & PPF Coating Type
                Text(
                    text = "SELECT PROTECTION & COATING TREATMENT",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("9H Ceramic Gloss", "Stealth Matte PPF", "Satin Pearl").forEach { finish ->
                        val isSelected = finish == selectedFinish
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) TorqGold else TorqNavyCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedFinish = finish }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = finish,
                                    color = if (isSelected) TorqGold else TorqTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = when (finish) {
                                        "Stealth Matte PPF" -> "Self-Healing TPU"
                                        "Satin Pearl" -> "Semi-Matte Luster"
                                        else -> "Mirror Hydrophobic"
                                    },
                                    color = TorqSlateGrey,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 3: Add-on Customizations (Toggles)
                Text(
                    text = "PREMIUM AESTHETIC ENHANCEMENTS",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LuxuryCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = TorqNavyCard,
                    borderColor = TorqNavyCardBorder
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Toggle 1: Ceramic Heat-Rejection Window Tint
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Nano-Ceramic IR Window Tint (70%)",
                                    color = TorqTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "99% UV rejection & privacy • +PKR 14,000",
                                    color = TorqSlateGrey,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = hasWindowTint,
                                onCheckedChange = { hasWindowTint = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TorqNavyBackground,
                                    checkedTrackColor = TorqGold,
                                    uncheckedThumbColor = TorqSlateGrey,
                                    uncheckedTrackColor = TorqNavySurface
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Toggle 2: Brembo Red Caliper Powder Coat
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "High-Temp Red Caliper Powder Coating",
                                    color = TorqTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Electrostatically baked finish • +PKR 12,000",
                                    color = TorqSlateGrey,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = hasRedCalipers,
                                onCheckedChange = { hasRedCalipers = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TorqNavyBackground,
                                    checkedTrackColor = TorqGold,
                                    uncheckedThumbColor = TorqSlateGrey,
                                    uncheckedTrackColor = TorqNavySurface
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Toggle 3: Carbon Fiber Aero Spoiler
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dry Carbon Fiber Rear Ducktail Spoiler",
                                    color = TorqTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Autoclaved lightweight weave • +PKR 24,000",
                                    color = TorqSlateGrey,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = hasCarbonSpoiler,
                                onCheckedChange = { hasCarbonSpoiler = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TorqNavyBackground,
                                    checkedTrackColor = TorqGold,
                                    uncheckedThumbColor = TorqSlateGrey,
                                    uncheckedTrackColor = TorqNavySurface
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Price Summary & Concierge Pickup
                LuxuryGoldCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ESTIMATED TOTAL INVESTMENT",
                                    color = TorqTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PKR %,d".format(totalCost),
                                    color = TorqGold,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TorqGreenSurface,
                                border = BorderStroke(1.dp, TorqGreen)
                            ) {
                                Text(
                                    text = "3-YR WARRANTY",
                                    color = TorqGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Includes Doorstep Enclosed Trailer Transport, Surface Paint Decontamination & 3-Stage Polish before application.",
                            color = TorqSlateGrey,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TorqfixGoldButton(
                    text = "Book Bespoke Detailing & Pickup (PKR %,d)".format(totalCost),
                    onClick = {
                        onBookCustomization(
                            "${selectedColor.name} + $selectedFinish Treatment",
                            "PKR %,d".format(totalCost)
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.LocalShipping
                )
            }
        }
    }
}

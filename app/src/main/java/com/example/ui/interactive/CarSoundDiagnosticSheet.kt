package com.example.ui.interactive

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.theme.TorqAmber
import com.example.ui.theme.TorqAmberSurface
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
import com.example.ui.theme.TorqRed
import com.example.ui.theme.TorqRedSurface
import com.example.ui.theme.TorqSky
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary

data class CarNoiseProfile(
    val id: String,
    val title: String,
    val soundDescription: String,
    val toneFrequency: Int,
    val rootCause: String,
    val severity: String, // "CRITICAL", "MODERATE", "WATCH"
    val urgencyLevel: String,
    val estimatedRepairPkr: String,
    val partsNeeded: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarSoundDiagnosticSheet(
    onDismiss: () -> Unit,
    onBookDiagnosis: (problemTitle: String, estimatedCost: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val noiseProfiles = remember {
        listOf(
            CarNoiseProfile(
                id = "belt",
                title = "High-Pitched Squealing under Hood",
                soundDescription = "Loud screeching when starting engine or accelerating in rain",
                toneFrequency = ToneGenerator.TONE_DTMF_D,
                rootCause = "Worn Serpentine Fan Belt or Seized Alternator Idler Pulley",
                severity = "MODERATE",
                urgencyLevel = "Fix within 48 hrs to avoid battery drain & overheating",
                estimatedRepairPkr = "PKR 6,500 – 9,800",
                partsNeeded = "Bando / Continental Serpentine Drive Belt & Tensioner"
            ),
            CarNoiseProfile(
                id = "brake",
                title = "Harsh Metal-on-Metal Grinding",
                soundDescription = "Loud grinding noise when pressing brake pedal firmly",
                toneFrequency = ToneGenerator.TONE_DTMF_A,
                rootCause = "Brake friction material worn down to bare steel backing plate",
                severity = "CRITICAL",
                urgencyLevel = "Immediate Hazard: Rotors scoring & braking distance doubled",
                estimatedRepairPkr = "PKR 14,500 – 22,000",
                partsNeeded = "Akebono / Brembo Ceramic Brake Pads & Rotor Resurfacing"
            ),
            CarNoiseProfile(
                id = "lifter",
                title = "Rapid Metallic Ticking / Clatter",
                soundDescription = "Rhythmic tapping sound from top of engine that speeds up with RPM",
                toneFrequency = ToneGenerator.TONE_DTMF_1,
                rootCause = "Low Oil Pressure, Sludge Clog, or Collapsed Hydraulic Valve Lifter",
                severity = "CRITICAL",
                urgencyLevel = "High Danger: Risk of camshaft wear or engine seizure",
                estimatedRepairPkr = "PKR 18,000 – 35,000",
                partsNeeded = "Engine Flush, Liqui Moly MoS2 Anti-Friction & Lifter Cleaning"
            ),
            CarNoiseProfile(
                id = "exhaust",
                title = "Rattling Buzzing Underneath Floor",
                soundDescription = "Vibrating metallic buzz at specific RPMs or speed bumps",
                toneFrequency = ToneGenerator.TONE_DTMF_B,
                rootCause = "Loose Exhaust Heat Shield or Broken Catalytic Converter Substrate",
                severity = "WATCH",
                urgencyLevel = "Non-critical but causes cabin vibration and exhaust drone",
                estimatedRepairPkr = "PKR 4,500 – 8,000",
                partsNeeded = "TIG Welding & Exhaust Hanger Bushing Kit"
            ),
            CarNoiseProfile(
                id = "suspension",
                title = "Dull Clunk / Thud over Bumps",
                soundDescription = "Heavy clunk from front wheels when turning or entering potholes",
                toneFrequency = ToneGenerator.TONE_DTMF_4,
                rootCause = "Blown Sway Bar Links, Cracked Control Arm Bushing, or Strut Mount",
                severity = "MODERATE",
                urgencyLevel = "Causes uneven tire wear, steering pull & loose handling",
                estimatedRepairPkr = "PKR 12,000 – 19,500",
                partsNeeded = "Heavy-Duty Stabilizer Links & Polyurethane Bushings"
            )
        )
    }

    var activeNoise by remember { mutableStateOf(noiseProfiles[0]) }
    var isPlayingSound by remember { mutableStateOf(false) }

    // Audio Generator
    val toneGen = remember {
        try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            toneGen?.release()
        }
    }

    // Sound wave pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "soundWave")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveScale"
    )

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
            // Header
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
                                imageVector = Icons.Default.Hearing,
                                contentDescription = null,
                                tint = TorqGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Sound Diagnostic Doctor",
                            color = TorqTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Acoustic Noise Identifier & Precision Severity Score",
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Audio Spectrogram & Active Diagnosis Display
                item {
                    LuxuryGoldCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isPlayingSound) TorqRedSurface else TorqGoldSurface,
                                        border = BorderStroke(1.dp, if (isPlayingSound) TorqRed else TorqGold)
                                    ) {
                                        Text(
                                            text = if (isPlayingSound) "AUDIO ACTIVE" else "ACOUSTIC TESTBENCH",
                                            color = if (isPlayingSound) TorqRed else TorqGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                // Severity Badge
                                val (sevColor, sevBg) = when (activeNoise.severity) {
                                    "CRITICAL" -> Pair(TorqRed, TorqRedSurface)
                                    "MODERATE" -> Pair(TorqAmber, TorqAmberSurface)
                                    else -> Pair(TorqGreen, TorqGreenSurface)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = sevBg,
                                    border = BorderStroke(1.dp, sevColor)
                                ) {
                                    Text(
                                        text = "${activeNoise.severity} SEVERITY",
                                        color = sevColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = activeNoise.title,
                                color = TorqTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = activeNoise.soundDescription,
                                color = TorqTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Audio Waveform Canvas
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TorqNavyBackground)
                                    .border(1.dp, TorqNavyCardBorder, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val barCount = 32
                                    val barWidth = size.width / (barCount * 1.5f)
                                    val centerY = size.height / 2f

                                    for (i in 0 until barCount) {
                                        val x = i * (barWidth * 1.5f) + 12f
                                        val factor = if (isPlayingSound) {
                                            ((Math.sin(i * 0.4 + waveScale * 4) + 1f) / 2f).toFloat()
                                        } else {
                                            ((Math.sin(i * 0.4) + 1f) / 6f).toFloat()
                                        }
                                        val barHeight = (size.height * 0.75f) * factor.coerceIn(0.1f, 1f)
                                        drawRect(
                                            color = if (isPlayingSound) TorqGold else TorqSlateBorder,
                                            topLeft = Offset(x, centerY - barHeight / 2),
                                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Listen to Sample Sound Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isPlayingSound) TorqRed else TorqGold,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (isPlayingSound) {
                                                toneGen?.stopTone()
                                                isPlayingSound = false
                                            } else {
                                                isPlayingSound = true
                                                toneGen?.startTone(activeNoise.toneFrequency, 1200)
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingSound) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = TorqNavyBackground,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isPlayingSound) "STOP SOUND" else "LISTEN TO NOISE",
                                            color = TorqNavyBackground,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Cause & Parts Recommendation
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TorqNavySurface)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "DIAGNOSED ROOT CAUSE:",
                                    color = TorqGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = activeNoise.rootCause,
                                    color = TorqTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "IMPACT / RISK:",
                                    color = TorqSlateGrey,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = activeNoise.urgencyLevel,
                                    color = TorqSky,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ESTIMATED FIX: ${activeNoise.estimatedRepairPkr}",
                                    color = TorqGoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Section Title: Select Other Noises to Diagnose
                item {
                    Text(
                        text = "EXPLORE KNOWN VEHICLE SOUND PROFILES",
                        color = TorqGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                items(noiseProfiles) { profile ->
                    val isSelected = profile.id == activeNoise.id
                    LuxuryCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
                        borderColor = if (isSelected) TorqGold else TorqNavyCardBorder,
                        onClick = {
                            if (isPlayingSound) {
                                toneGen?.stopTone()
                                isPlayingSound = false
                            }
                            activeNoise = profile
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) TorqGoldSurface else TorqNavySurface,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Hearing,
                                        contentDescription = null,
                                        tint = if (isSelected) TorqGold else TorqSlateMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.title,
                                    color = if (isSelected) TorqTextPrimary else TorqTextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = profile.rootCause,
                                    color = TorqSlateGrey,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }

                            Text(
                                text = profile.estimatedRepairPkr.split("–").firstOrNull()?.trim() ?: "",
                                color = TorqGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    TorqfixGoldButton(
                        text = "Book Noise Inspection & Flatbed Pickup",
                        onClick = {
                            onBookDiagnosis(activeNoise.title, activeNoise.estimatedRepairPkr)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.LocalShipping
                    )
                }
            }
        }
    }
}

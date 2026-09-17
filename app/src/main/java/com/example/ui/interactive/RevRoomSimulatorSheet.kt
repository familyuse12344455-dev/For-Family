package com.example.ui.interactive

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LuxuryCard
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldBorder
import com.example.ui.theme.TorqGoldDark
import com.example.ui.theme.TorqGoldSurface
import com.example.ui.theme.TorqGreen
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class EngineProfile(
    val name: String,
    val description: String,
    val idleRpm: Float = 800f,
    val maxRpm: Float = 7500f,
    val redlineRpm: Float = 6400f,
    val boostPeakBar: Float = 1.45f,
    val toneType: Int = ToneGenerator.TONE_DTMF_D
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevRoomSimulatorSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val engineProfiles = listOf(
        EngineProfile(
            name = "V8 Biturbo Symphony",
            description = "4.0L Twin-Turbo V8 • Deep rumble, brutal torque & backfire pops",
            maxRpm = 7500f,
            redlineRpm = 6400f,
            boostPeakBar = 1.6f,
            toneType = ToneGenerator.TONE_DTMF_0
        ),
        EngineProfile(
            name = "Inline-6 TwinPower",
            description = "3.0L Turbo I6 • Silky high-rev scream with rapid turbo spool",
            maxRpm = 7200f,
            redlineRpm = 6200f,
            boostPeakBar = 1.3f,
            toneType = ToneGenerator.TONE_DTMF_5
        ),
        EngineProfile(
            name = "2.8L Turbo Diesel (Fortuner)",
            description = "Common Rail 500Nm • Heavy torque grunt & wastegate flutter",
            maxRpm = 4500f,
            redlineRpm = 3800f,
            boostPeakBar = 1.8f,
            toneType = ToneGenerator.TONE_DTMF_9
        ),
        EngineProfile(
            name = "Hybrid Hyper-Boost",
            description = "V6 Turbo + Dual Electric Motor • Instant torque whine",
            maxRpm = 8000f,
            redlineRpm = 7000f,
            boostPeakBar = 1.5f,
            toneType = ToneGenerator.TONE_DTMF_3
        )
    )

    var currentProfile by remember { mutableStateOf(engineProfiles[0]) }
    var isPedalPressed by remember { mutableStateOf(false) }
    var rpm by remember { mutableFloatStateOf(currentProfile.idleRpm) }
    var boostBar by remember { mutableFloatStateOf(0f) }
    var showPopsAndBangs by remember { mutableStateOf(false) }

    // Safe ToneGenerator for rev sound simulation
    val toneGen = remember {
        try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                toneGen?.stopTone()
                toneGen?.release()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    // Engine loop
    LaunchedEffect(isPedalPressed, currentProfile) {
        if (isPedalPressed) {
            showPopsAndBangs = false
            // Ramp up RPM
            while (isActive && isPedalPressed) {
                if (rpm < currentProfile.maxRpm) {
                    rpm = (rpm + 240f).coerceAtMost(currentProfile.maxRpm)
                    boostBar = (boostBar + 0.11f).coerceAtMost(currentProfile.boostPeakBar)
                }

                // Sound pulse simulation
                try {
                    val tone = if (rpm > currentProfile.redlineRpm) {
                        ToneGenerator.TONE_DTMF_A
                    } else {
                        currentProfile.toneType
                    }
                    toneGen?.startTone(tone, 60)
                } catch (e: Exception) {
                    // ignore
                }

                delay(35)
            }
        } else {
            // Pedal released: Exhaust crackles / pops if RPM was high!
            if (rpm > currentProfile.redlineRpm * 0.8f) {
                showPopsAndBangs = true
                try {
                    toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
                } catch (e: Exception) {
                    // ignore
                }
            }

            // Ramp down to idle
            while (isActive && !isPedalPressed && rpm > currentProfile.idleRpm) {
                rpm = (rpm - 180f).coerceAtLeast(currentProfile.idleRpm)
                boostBar = (boostBar - 0.15f).coerceAtLeast(0f)
                delay(30)
            }
            showPopsAndBangs = false
        }
    }

    val isRedline = rpm >= currentProfile.redlineRpm

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
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
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
                            text = "Engine Rev Room",
                            color = TorqTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Acoustic Throttle & Dyno Simulator",
                            color = TorqGold,
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

            // Engine Profile Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                engineProfiles.forEach { profile ->
                    val isSelected = currentProfile.name == profile.name
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) TorqGoldSurface else TorqNavyCard,
                        border = BorderStroke(1.dp, if (isSelected) TorqGold else TorqNavyCardBorder),
                        modifier = Modifier.clickable {
                            currentProfile = profile
                            rpm = profile.idleRpm
                            boostBar = 0f
                        }
                    ) {
                        Text(
                            text = profile.name,
                            color = if (isSelected) TorqGold else TorqTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = currentProfile.description,
                color = TorqSlateGrey,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // === TACHOMETER GAUGE CANVAS ===
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .clip(CircleShape)
                    .background(TorqNavyCardElevated)
                    .border(2.dp, if (isRedline) TorqRed else TorqGoldBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f

                    // Base dial track
                    drawArc(
                        color = TorqNavyCardBorder,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Redline section track
                    val redlineStartAngle = 135f + (270f * (currentProfile.redlineRpm / currentProfile.maxRpm))
                    val redlineSweep = 270f * ((currentProfile.maxRpm - currentProfile.redlineRpm) / currentProfile.maxRpm)
                    drawArc(
                        color = TorqRed.copy(alpha = 0.4f),
                        startAngle = redlineStartAngle,
                        sweepAngle = redlineSweep,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Active RPM fill arc
                    val activeSweep = (270f * ((rpm - currentProfile.idleRpm) / (currentProfile.maxRpm - currentProfile.idleRpm))).coerceIn(0f, 270f)
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                TorqGold,
                                TorqSky,
                                if (isRedline) TorqRed else TorqGold
                            )
                        ),
                        startAngle = 135f,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Needle pointer calculation
                    val needleAngleRad = Math.toRadians((135f + activeSweep).toDouble())
                    val needleEnd = Offset(
                        (center.x + (radius - 20.dp.toPx()) * cos(needleAngleRad)).toFloat(),
                        (center.y + (radius - 20.dp.toPx()) * sin(needleAngleRad)).toFloat()
                    )

                    drawLine(
                        color = if (isRedline) TorqRed else TorqGold,
                        start = center,
                        end = needleEnd,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Center Hub
                    drawCircle(
                        color = TorqNavyBackground,
                        radius = 16.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = if (isRedline) TorqRed else TorqGold,
                        radius = 8.dp.toPx(),
                        center = center
                    )
                }

                // Tachometer Text Center Readout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 70.dp)
                ) {
                    Text(
                        text = "${rpm.toInt()}",
                        color = if (isRedline) TorqRed else TorqTextPrimary,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "RPM x1000",
                        color = if (isRedline) TorqRed else TorqGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "BOOST: +${String.format("%.2f", boostBar)} BAR",
                        color = TorqSky,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Redline flashing banner
                if (isRedline) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TorqRedSurface,
                        border = BorderStroke(1.dp, TorqRed),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 36.dp)
                    ) {
                        Text(
                            text = "⚠ REDLINE SHIFT ⚠",
                            color = TorqRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Backfire Pops Banner
                if (showPopsAndBangs) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TorqGoldSurface,
                        border = BorderStroke(1.dp, TorqGold),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 36.dp)
                    ) {
                        Text(
                            text = "💥 OVERRUN POPS & BANGS 💥",
                            color = TorqGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sound Waves Visualizer Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TorqNavyCard)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val barCount = 18
                repeat(barCount) { i ->
                    val factor = ((rpm / currentProfile.maxRpm) * (0.3f + (i % 5) * 0.15f)).coerceIn(0.15f, 1f)
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxSize()
                            .padding(vertical = (12 * (1f - factor)).dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (isRedline) TorqRed else if (i > 12) TorqSky else TorqGold
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === INTERACTIVE ACCELERATOR THROTTLE PEDAL (HOLD TO REV) ===
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(84.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                if (isPedalPressed) TorqGoldDark else TorqGold,
                                if (isPedalPressed) TorqGold else TorqGoldDark
                            )
                        )
                    )
                    .border(
                        2.dp,
                        if (isPedalPressed) TorqSky else TorqGoldBorder,
                        RoundedCornerShape(18.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPedalPressed = true
                                tryAwaitRelease()
                                isPedalPressed = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = TorqNavyBackground,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPedalPressed) "FULL THROTTLE ACTIVE!" else "PRESS & HOLD GAS PEDAL",
                            color = TorqNavyBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = if (isPedalPressed) "Releasing triggers overrun flame crackles" else "Hold to rev engine and feel turbo boost",
                        color = TorqNavyBackground.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap or hold the pedal to rev up to 7,500 RPM with audio synthesis",
                color = TorqSlateMuted,
                fontSize = 11.sp
            )
        }
    }
}

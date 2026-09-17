package com.example.ui.screens.splash

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// Colors extracted directly from the video keyframes
private val BrandYellow = Color(0xFFF7C200)
private val BrandYellowLight = Color(0xFFFFD53D)
private val BrandRoyalBlue = Color(0xFF0C3E8A)
private val BrandRoyalBlueDark = Color(0xFF07275B)
private val BrandRoyalBlueAccent = Color(0xFF1553B0)
private val WrenchDark = Color(0xFF1E222A)
private val WrenchBevel = Color(0xFF323843)
private val HexNutSilver = Color(0xFFCFD5DE)
private val HexNutDarkSilver = Color(0xFF9AA2AF)
private val HexNutInner = Color(0xFF5A6372)

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Dynamically check if splash_video.mp4 has been placed in res/raw
    val rawVideoResId = remember {
        try {
            context.resources.getIdentifier("splash_video", "raw", context.packageName)
        } catch (e: Exception) {
            0
        }
    }

    var isVideoFinished by remember { mutableStateOf(false) }

    if (rawVideoResId != 0 && !isVideoFinished) {
        // === ACTUAL VIDEO PLAYBACK MODE (Plays when user drops splash_video.mp4 into res/raw) ===
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandRoyalBlueDark)
        ) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        val videoUri = Uri.parse("android.resource://${ctx.packageName}/$rawVideoResId")
                        setVideoURI(videoUri)
                        setOnCompletionListener {
                            isVideoFinished = true
                            onTimeout()
                        }
                        setOnErrorListener { _, _, _ ->
                            // Fallback to animated experience if playback error
                            isVideoFinished = true
                            true
                        }
                        start()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Skip button
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.45f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 20.dp)
                    .clickable { onTimeout() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "SKIP",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Skip",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    } else {
        // === NATIVE HIGH-FPS VECTOR RECREATION OF THE VIDEO ANIMATION ===
        // Matches the exact 4-second progression of the video:
        // Frame 00:00 - Yellow background, wrench tightening the silver hex nut
        // Frame 00:01 - Wrench slides right, revealing "TORQFIX" in bold dark blue
        // Frame 00:02 - Screen sweeps from yellow to deep royal blue, TORQFIX turns gold/yellow
        // Frame 00:03 - Centered clean TORQFIX branding locks with subtle glow and transitions
        NativeVideoSplashAnimation(
            onTimeout = onTimeout
        )
    }
}

@Composable
private fun NativeVideoSplashAnimation(
    onTimeout: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    // Animation Drivers
    val wrenchRotation = remember { Animatable(0f) }
    val wrenchSlideX = remember { Animatable(0f) }
    val wrenchAlpha = remember { Animatable(1f) }
    val textRevealAlpha = remember { Animatable(0f) }
    val bgBlueMorph = remember { Animatable(0f) }
    val textGoldMorph = remember { Animatable(0f) }
    val finalTaglineAlpha = remember { Animatable(0f) }
    val textScale = remember { Animatable(0.96f) }

    // Shimmer highlight transition
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    LaunchedEffect(Unit) {
        // Phase 1 (0.0s - 0.7s): Wrench tightens the nut with mechanical torque
        delay(120L)
        wrenchRotation.animateTo(
            targetValue = -14f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
        try {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (e: Exception) {
            // ignore
        }
        wrenchRotation.animateTo(
            targetValue = 8f,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
        )

        // Phase 2 (0.7s - 1.5s): Wrench slides to the right off-screen, revealing TORQFIX
        textRevealAlpha.snapTo(1f)
        launch {
            wrenchSlideX.animateTo(
                targetValue = 800f,
                animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
            )
            wrenchAlpha.snapTo(0f)
        }

        delay(400L)

        // Phase 3 (1.5s - 2.5s): Background transitions from Yellow to Royal Blue, logo shifts from blue to gold
        launch {
            bgBlueMorph.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
        }
        launch {
            textGoldMorph.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
        launch {
            textScale.animateTo(
                targetValue = 1.05f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
            textScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }

        delay(800L)

        // Phase 4 (2.5s - 3.2s): Tagline and dispatch status appear on royal blue canvas
        finalTaglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )

        // Phase 5 (3.2s - 3.5s): Hold for smooth appreciation then navigate
        delay(750L)
        onTimeout()
    }

    // Dynamic interpolated background color matching the video
    val currentBgColor = remember(bgBlueMorph.value) {
        val p = bgBlueMorph.value
        Color(
            red = BrandYellow.red * (1f - p) + BrandRoyalBlueDark.red * p,
            green = BrandYellow.green * (1f - p) + BrandRoyalBlueDark.green * p,
            blue = BrandYellow.blue * (1f - p) + BrandRoyalBlueDark.blue * p,
            alpha = 1f
        )
    }

    // Dynamic interpolated text color: Starts as Deep Navy on Yellow, becomes Brand Gold on Blue
    val currentTextColor = remember(textGoldMorph.value) {
        val p = textGoldMorph.value
        Color(
            red = BrandRoyalBlue.red * (1f - p) + BrandYellowLight.red * p,
            green = BrandRoyalBlue.green * (1f - p) + BrandYellowLight.green * p,
            blue = BrandRoyalBlue.blue * (1f - p) + BrandYellowLight.blue * p,
            alpha = 1f
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (bgBlueMorph.value > 0.05f) {
                        listOf(
                            BrandRoyalBlueAccent.copy(alpha = bgBlueMorph.value),
                            currentBgColor,
                            BrandRoyalBlueDark.copy(alpha = bgBlueMorph.value)
                        )
                    } else {
                        listOf(BrandYellowLight, BrandYellow, BrandYellow)
                    }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Skip Button (Top Right)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (bgBlueMorph.value > 0.5f) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.15f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 20.dp)
                .clickable { onTimeout() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SKIP",
                    color = if (bgBlueMorph.value > 0.5f) Color.White else Color(0xFF1E222A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Skip",
                    tint = if (bgBlueMorph.value > 0.5f) Color.White else Color(0xFF1E222A),
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Center Content Container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Layer 1: TORQFIX Wordmark (Revealed when wrench slides)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .alpha(textRevealAlpha.value)
                        .scale(textScale.value)
                ) {
                    Text(
                        text = "TORQFIX",
                        color = currentTextColor,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 4.sp,
                        textAlign = TextAlign.Center
                    )

                    AnimatedVisibility(
                        visible = bgBlueMorph.value > 0.7f,
                        enter = fadeIn(tween(300)),
                        exit = fadeOut()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "LUXURY AUTOMOTIVE SERVICE",
                                color = BrandYellowLight.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.5.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Layer 2: The Animated Wrench & Hex Nut (Matches Frame 00:00 & 00:01)
                if (wrenchAlpha.value > 0f) {
                    Box(
                        modifier = Modifier
                            .offset(x = wrenchSlideX.value.dp)
                            .rotate(wrenchRotation.value)
                            .size(240.dp, 120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val nutCenterX = w * 0.58f
                            val nutCenterY = h * 0.50f
                            val nutRadius = 26.dp.toPx()

                            // --- 1. Draw Wrench Handle ---
                            val handleLength = w * 0.56f
                            val handleHeight = 22.dp.toPx()
                            val handleStartX = nutCenterX - handleLength
                            val handleTopY = nutCenterY - handleHeight / 2

                            // Handle Body (Dark Matte Charcoal)
                            drawRoundRect(
                                color = WrenchDark,
                                topLeft = Offset(handleStartX, handleTopY),
                                size = Size(handleLength * 0.90f, handleHeight),
                                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                            )

                            // Handle Bevel highlight
                            drawRoundRect(
                                color = WrenchBevel,
                                topLeft = Offset(handleStartX + 8.dp.toPx(), handleTopY + 3.dp.toPx()),
                                size = Size((handleLength * 0.90f) - 16.dp.toPx(), 3.dp.toPx()),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                            )

                            // --- 2. Draw Open-Ended Wrench Jaw Head ---
                            val headOuterRadius = nutRadius * 1.62f
                            val wrenchHeadPath = Path().apply {
                                // Outer C-shape jaw around the nut
                                moveTo(nutCenterX - handleLength * 0.15f, nutCenterY - handleHeight / 2)
                                lineTo(nutCenterX - headOuterRadius * 0.5f, nutCenterY - headOuterRadius * 0.9f)
                                cubicTo(
                                    nutCenterX, nutCenterY - headOuterRadius * 1.1f,
                                    nutCenterX + headOuterRadius * 0.8f, nutCenterY - headOuterRadius * 0.7f,
                                    nutCenterX + headOuterRadius * 0.95f, nutCenterY - headOuterRadius * 0.35f
                                )
                                // Upper jaw tip
                                lineTo(nutCenterX + nutRadius * 0.95f, nutCenterY - nutRadius * 0.6f)
                                // Jaw inner grasping cutout for hex flat
                                lineTo(nutCenterX - nutRadius * 0.2f, nutCenterY - nutRadius * 0.95f)
                                lineTo(nutCenterX - nutRadius * 0.75f, nutCenterY)
                                lineTo(nutCenterX - nutRadius * 0.2f, nutCenterY + nutRadius * 0.95f)
                                lineTo(nutCenterX + nutRadius * 0.95f, nutCenterY + nutRadius * 0.6f)
                                // Lower jaw tip to outer
                                lineTo(nutCenterX + headOuterRadius * 0.95f, nutCenterY + headOuterRadius * 0.35f)
                                cubicTo(
                                    nutCenterX + headOuterRadius * 0.8f, nutCenterY + headOuterRadius * 0.7f,
                                    nutCenterX, nutCenterY + headOuterRadius * 1.1f,
                                    nutCenterX - headOuterRadius * 0.5f, nutCenterY + headOuterRadius * 0.9f
                                )
                                lineTo(nutCenterX - handleLength * 0.15f, nutCenterY + handleHeight / 2)
                                close()
                            }

                            drawPath(
                                path = wrenchHeadPath,
                                color = WrenchDark,
                                style = Fill
                            )

                            // Wrench head bevel line
                            drawPath(
                                path = wrenchHeadPath,
                                color = WrenchBevel,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // --- 3. Draw Silver Metallic 6-sided Hex Nut ---
                            val hexPath = Path()
                            for (i in 0 until 6) {
                                val angleRad = (i * 60.0) * (Math.PI / 180.0)
                                val px = nutCenterX + (nutRadius * cos(angleRad)).toFloat()
                                val py = nutCenterY + (nutRadius * sin(angleRad)).toFloat()
                                if (i == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                            }
                            hexPath.close()

                            // Silver Nut Body
                            drawPath(
                                path = hexPath,
                                color = HexNutSilver,
                                style = Fill
                            )

                            // Hex edge highlight
                            drawPath(
                                path = hexPath,
                                color = HexNutDarkSilver,
                                style = Stroke(width = 2.5.dp.toPx())
                            )

                            // Inner Threaded Circular Hole
                            drawCircle(
                                color = HexNutInner,
                                radius = nutRadius * 0.52f,
                                center = Offset(nutCenterX, nutCenterY)
                            )
                            drawCircle(
                                color = Color(0xFF374151),
                                radius = nutRadius * 0.38f,
                                center = Offset(nutCenterX, nutCenterY)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Subtitle / Features badge fading in on the royal blue scene
            AnimatedVisibility(
                visible = finalTaglineAlpha.value > 0.1f,
                enter = fadeIn(tween(400)),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(finalTaglineAlpha.value)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(BrandYellowLight)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VALET PICKUP & REPAIR",
                            color = Color.White.copy(alpha = 0.90f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }
            }
        }

        // Bottom Brand Signature
        Text(
            text = "TORQFIX PAKISTAN",
            color = if (bgBlueMorph.value > 0.5f) Color.White.copy(alpha = 0.45f) else Color(0xFF1E222A).copy(alpha = 0.45f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}

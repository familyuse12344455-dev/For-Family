package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookingStatus
import com.example.ui.theme.*

/**
 * Supercar-grade Luxury Card with subtle chamfer top-rim highlight
 * and spring-loaded tactile touch feedback.
 */
@Composable
fun LuxuryCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = TorqNavyCard,
    borderColor: Color = TorqNavyCardBorder,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (onClick != null && isPressed) 0.985f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "cardPress"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.95f),
                        TorqNavyBackground.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                borderWidth,
                Brush.verticalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.9f),
                        borderColor.copy(alpha = 0.4f)
                    )
                ),
                shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        // Upper glass highlight edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x33FFFFFF),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/**
 * Hypercar Gold Card with metallic dual-tone border and ambient glow.
 */
@Composable
fun LuxuryGoldCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (onClick != null && isPressed) 0.982f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "goldCardPress"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .drawBehind {
                drawRoundRect(
                    color = Color(0x18FFD700),
                    cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx()),
                    size = Size(size.width, size.height + 4.dp.toPx())
                )
            }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TorqNavyCardElevated,
                        TorqNavyCard
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.linearGradient(
                    colors = listOf(
                        TorqGold,
                        Color(0x66FFD700),
                        TorqGoldDark,
                        Color(0x2238BDF8)
                    )
                ),
                shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        // Top specular highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.2.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x80FFE55C),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/**
 * NEXT-GEN HYPERCAR LIQUID GOLD BUTTON
 * Features:
 * - Tactile spring-loaded mechanical button press (0.955f scale)
 * - Sweeping animated liquid reflection sheen pass
 * - Chamfered top edge specular light ray
 * - Warm ambient gold volumetric halo
 * - High-contrast uppercase cockpit display typography
 */
@Composable
fun TorqfixGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    height: Dp = 54.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.955f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btnSpringScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "btnSheenPass")
    val sheenProgress by infiniteTransition.animateFloat(
        initialValue = -1.2f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sheen"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            // Ambient warm golden glow shadow
            .drawBehind {
                if (enabled) {
                    drawRoundRect(
                        color = Color(0x38FFB700),
                        cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                        size = Size(size.width, size.height + 4.dp.toPx())
                    )
                }
            }
            .clip(shape)
            .background(
                if (enabled) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFEA79), // Liquid gold highlight
                            Color(0xFFFFC000), // Solid automotive gold
                            Color(0xFFE68A00)  // Deep amber metallic
                        )
                    )
                } else {
                    Brush.linearGradient(listOf(TorqSlateMuted, TorqSlateDark))
                }
            )
            .border(
                1.dp,
                if (enabled) Color(0x66FFFFFF) else Color.Transparent,
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                onClick = onClick
            )
    ) {
        // Dynamic Liquid Sheen sweep pass
        if (enabled && !loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val sheenWidth = size.width * 0.35f
                        val sheenStart = size.width * sheenProgress
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x33FFFFFF),
                                    Color(0x88FFFFFF),
                                    Color(0x33FFFFFF),
                                    Color.Transparent
                                ),
                                startX = sheenStart,
                                endX = sheenStart + sheenWidth
                            )
                        )
                    }
            )
        }

        // Top specular chamfer line (precision CNC machined look)
        if (enabled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.2.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xB3FFFFFF),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Button Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TorqTextDark,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "PROCESSING...",
                        color = TorqTextDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.2.sp
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0x220B1B3A),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = TorqTextDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(
                        text = text.uppercase(),
                        color = if (enabled) TorqTextDark else TorqNavyBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.1.sp
                    )
                }
            }
        }
    }
}

/**
 * NEXT-GEN FROSTED CYBER BORDER BUTTON
 * Features:
 * - Spring-loaded scale feedback
 * - Semi-translucent dark carbon backing
 * - Dual-tone metallic gold-cyan gradient border
 * - Top bevel highlight
 */
@Composable
fun TorqfixOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    borderColor: Color = TorqGoldBorder,
    shape: Shape = RoundedCornerShape(16.dp),
    height: Dp = 52.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.965f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btnOutlinedScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xD9142956),
                        Color(0xF20F2248)
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        TorqGold.copy(alpha = 0.85f),
                        TorqSky.copy(alpha = 0.5f),
                        TorqGoldDark.copy(alpha = 0.85f)
                    )
                ),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Subtle top bevel highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x66FFD700),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TorqGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) TorqTextPrimary else TorqSlateMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * SUPERCAR ENGINE START IGNITION BUTTON
 * Features:
 * - High-tech circular or pill ignition with rotating glow ring
 * - Tactile mechanical depression
 * - Pulsing cockpit standby LED
 */
@Composable
fun TorqfixIgnitionButton(
    title: String = "START ENGINE",
    subtitle: String = "PUSH TO DIAGNOSE",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "ignitionScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "ignitionPulse")
    val glowAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glowAngle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .drawBehind {
                drawRoundRect(
                    color = Color(0x33FFD700),
                    cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                    size = Size(size.width, size.height + 6.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B2E58),
                        TorqCarbonBlack
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.sweepGradient(
                    colors = listOf(
                        TorqGold,
                        TorqSky,
                        Color(0x33FFD700),
                        TorqGold
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Engine Core
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                TorqGoldLight,
                                TorqGold,
                                TorqGoldAmber,
                                Color(0xFF7C3AED)
                            )
                        )
                    )
                    .border(2.dp, Color(0x80FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TorqTextDark,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Ignition",
                        tint = TorqTextDark,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    color = TorqGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.3.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TorqTextSecondary,
                    fontSize = 12.sp,
                    letterSpacing = 0.3.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TorqGoldSurface,
                border = BorderStroke(0.8.dp, TorqGoldBorder)
            ) {
                Text(
                    text = "ENGAGE",
                    color = TorqGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * TEXT WITH METALLIC LIQUID GOLD SHADER
 */
@Composable
fun GoldGradientText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Black,
    letterSpacing: androidx.compose.ui.unit.TextUnit = (-0.5).sp,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        style = androidx.compose.ui.text.TextStyle(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFFEA79),
                    Color(0xFFFFC000),
                    Color(0xFFFF8F00)
                )
            )
        ),
        modifier = modifier
    )
}

/**
 * HIGH-END AUTOMOTIVE SECTION HEADER
 */
@Composable
fun AutomotiveSectionHeader(
    title: String,
    subtitle: String? = null,
    badge: String? = null,
    onBadgeClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Precision Racing Amber Indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(TorqGoldBright, TorqGoldAmber)
                        )
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title.uppercase(),
                    color = TorqTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = TorqSlateGrey,
                        fontSize = 11.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }

        if (badge != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TorqGoldSurface,
                border = BorderStroke(0.8.dp, TorqGoldBorder),
                modifier = if (onBadgeClick != null) Modifier.clickable(onClick = onBadgeClick) else Modifier
            ) {
                Text(
                    text = badge.uppercase(),
                    color = TorqGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BookingStatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    val (targetBgColor, targetTextColor, targetBorderColor, icon) = when (status) {
        BookingStatus.PENDING_DRIVER -> Quadruple(
            TorqAmberSurface,
            TorqAmber,
            TorqAmber.copy(alpha = 0.4f),
            Icons.Default.PendingActions
        )
        BookingStatus.DRIVER_ASSIGNED -> Quadruple(
            TorqSkySurface,
            TorqSky,
            TorqSky.copy(alpha = 0.5f),
            Icons.Default.LocalShipping
        )
        BookingStatus.VEHICLE_PICKED -> Quadruple(
            TorqSkySurface,
            TorqSky,
            TorqSky.copy(alpha = 0.5f),
            Icons.Default.DirectionsCar
        )
        BookingStatus.WORKSHOP_RECEIVED -> Quadruple(
            TorqGoldSurface,
            TorqGold,
            TorqGoldBorder,
            Icons.Default.Store
        )
        BookingStatus.REPAIRING -> Quadruple(
            TorqAmberSurface,
            TorqAmber,
            TorqAmber.copy(alpha = 0.5f),
            Icons.Default.Build
        )
        BookingStatus.TESTING -> Quadruple(
            TorqSkySurface,
            TorqSky,
            TorqSky.copy(alpha = 0.5f),
            Icons.Default.Speed
        )
        BookingStatus.READY_FOR_RETURN,
        BookingStatus.RETURNING -> Quadruple(
            TorqGoldSurface,
            TorqGold,
            TorqGoldBorder,
            Icons.Default.LocalShipping
        )
        BookingStatus.COMPLETED -> Quadruple(
            TorqGreenSurface,
            TorqGreen,
            TorqGreen.copy(alpha = 0.5f),
            Icons.Default.CheckCircle
        )
    }

    val animatedBg by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "badgeBg"
    )
    val animatedText by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "badgeText"
    )
    val animatedBorder by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "badgeBorder"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = animatedBg,
        border = BorderStroke(1.dp, animatedBorder)
    ) {
        AnimatedContent(
            targetState = Pair(status, icon),
            transitionSpec = {
                (slideInVertically(animationSpec = tween(320, easing = FastOutSlowInEasing)) { it / 2 } + fadeIn(tween(320)))
                    .togetherWith(slideOutVertically(animationSpec = tween(260, easing = FastOutSlowInEasing)) { -it / 2 } + fadeOut(tween(200)))
            },
            label = "badgeStatusContent",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) { (st, ic) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (st != BookingStatus.COMPLETED) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(pulseScale)
                            .background(animatedText, CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = ic,
                        contentDescription = null,
                        tint = animatedText,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = st.displayName,
                    color = animatedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun StatusProgressTimeline(
    currentStatus: BookingStatus,
    modifier: Modifier = Modifier
) {
    val steps = BookingStatus.entries.toList()
    val currentIndex = currentStatus.stepIndex

    val infiniteTransition = rememberInfiniteTransition(label = "activePulse")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "haloScale"
    )
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "haloAlpha"
    )

    val overallProgress by animateFloatAsState(
        targetValue = (currentIndex + 1f) / steps.size,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessLow),
        label = "timelineProgressFraction"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TorqNavyCard, RoundedCornerShape(18.dp))
            .border(1.dp, TorqNavyCardBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Service Progress Timeline",
                color = TorqTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = {
                    (slideInVertically(tween(300, easing = FastOutSlowInEasing)) { it / 2 } + fadeIn(tween(300)))
                        .togetherWith(slideOutVertically(tween(250)) { -it / 2 } + fadeOut(tween(200)))
                },
                label = "stepProgressHeader"
            ) { idx ->
                Text(
                    text = "Step ${idx + 1} of ${steps.size}",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Luxury Progress Track with Gold-to-Green Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(TorqNavySurface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(overallProgress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(TorqGold, TorqGreen)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        steps.forEachIndexed { index, step ->
            val isPassed = index < currentIndex
            val isCurrent = index == currentIndex

            val nodeBgColor by animateColorAsState(
                targetValue = when {
                    isCurrent -> TorqGold
                    isPassed -> TorqGreen
                    else -> TorqNavyBackground
                },
                animationSpec = tween(450, easing = FastOutSlowInEasing),
                label = "nodeBg_$index"
            )
            val nodeBorderColor by animateColorAsState(
                targetValue = when {
                    isCurrent -> TorqGoldLight
                    isPassed -> TorqGreen
                    else -> TorqSlateDark
                },
                animationSpec = tween(450, easing = FastOutSlowInEasing),
                label = "nodeBorder_$index"
            )
            val titleColor by animateColorAsState(
                targetValue = when {
                    isCurrent -> TorqGold
                    isPassed -> TorqTextPrimary
                    else -> TorqSlateMuted
                },
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "titleColor_$index"
            )
            val descColor by animateColorAsState(
                targetValue = if (isCurrent) TorqTextSecondary else TorqSlateMuted,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "descColor_$index"
            )
            val rowHighlightBg by animateColorAsState(
                targetValue = if (isCurrent) TorqGoldSurface.copy(alpha = 0.35f) else Color.Transparent,
                animationSpec = tween(450, easing = FastOutSlowInEasing),
                label = "rowHighlight_$index"
            )
            val rowBorderColor by animateColorAsState(
                targetValue = if (isCurrent) TorqGoldBorder.copy(alpha = 0.35f) else Color.Transparent,
                animationSpec = tween(450, easing = FastOutSlowInEasing),
                label = "rowBorder_$index"
            )
            val lineFillFraction by animateFloatAsState(
                targetValue = if (isPassed) 1f else 0f,
                animationSpec = tween(550, easing = FastOutSlowInEasing),
                label = "lineFill_$index"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Timeline Column (Dot + Connector Line)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp)
                ) {
                    Box(
                        modifier = Modifier.size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .scale(haloScale)
                                    .background(TorqGold.copy(alpha = haloAlpha), CircleShape)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(nodeBgColor)
                                .border(
                                    width = 1.5.dp,
                                    color = nodeBorderColor,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isPassed -> {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = TorqNavyBackground,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                isCurrent -> {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(TorqNavyBackground, CircleShape)
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "${index + 1}",
                                        color = TorqSlateMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(38.dp)
                                .background(TorqSlateBorder, RoundedCornerShape(1.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .fillMaxHeight(lineFillFraction)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(TorqGreen, TorqGold)
                                        ),
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Text Description inside animated highlight container
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = rowHighlightBg,
                    border = BorderStroke(1.dp, rowBorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (index < steps.lastIndex) 8.dp else 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = step.displayName,
                                color = titleColor,
                                fontSize = 14.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                            )
                            AnimatedVisibility(
                                visible = isCurrent,
                                enter = fadeIn(tween(350)) + expandHorizontally(),
                                exit = fadeOut(tween(250)) + shrinkHorizontally()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TorqGoldSurface,
                                    border = BorderStroke(1.dp, TorqGoldBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .background(TorqGold, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "IN PROGRESS",
                                            color = TorqGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = step.description,
                            color = descColor,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

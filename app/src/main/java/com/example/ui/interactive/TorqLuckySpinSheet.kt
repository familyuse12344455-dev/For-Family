package com.example.ui.interactive

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LuxuryCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.components.TorqfixOutlinedButton
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
import com.example.ui.theme.TorqSky
import com.example.ui.theme.TorqSkySurface
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SpinPrize(
    val title: String,
    val subtitle: String,
    val promoCode: String,
    val sliceColor: Color,
    val textColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorqLuckySpinSheet(
    onDismiss: () -> Unit,
    onApplyPromoToBooking: (promoCode: String) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val prizes = remember {
        listOf(
            SpinPrize("50% OIL OFF", "Liqui Moly 5W-40 Synthetic", "SYNTH50", Color(0xFFFFD700), Color(0xFF0B1B3A)),
            SpinPrize("FREE TOWING", "100% Free City Flatbed Recovery", "FLATBEDVIP", Color(0xFF00E5FF), Color(0xFF0B1B3A)),
            SpinPrize("CERAMIC GLOSS", "3-Stage Detailing Polish", "CERAMICFREE", Color(0xFF1E3A8A), Color.White),
            SpinPrize("PKR 1500 FUEL", "Hi-Octane Fuel Voucher", "OCTANE1500", Color(0xFF00E676), Color(0xFF0B1B3A)),
            SpinPrize("VIP BAY PASS", "Zero-Wait Express Repair", "EXPRESSBAY", Color(0xFFD97706), Color.White),
            SpinPrize("FREE SCAN", "Full OBD-II ECU Diagnostics", "SCANPASS", Color(0xFF7C3AED), Color.White),
            SpinPrize("1000 NITRO", "Torqfix Privilege Points", "NITRO1000", Color(0xFFFF5252), Color.White),
            SpinPrize("FREE AC GAS", "Freon R134a Clean Purge", "FREEAC", Color(0xFF0284C7), Color.White)
        )
    }

    val rotationAnim = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonPrize by remember { mutableStateOf<SpinPrize?>(null) }
    var copiedCode by remember { mutableStateOf(false) }

    fun spinTheWheel() {
        if (isSpinning) return
        coroutineScope.launch {
            isSpinning = true
            wonPrize = null
            copiedCode = false

            // Target random slice between 0 and 7
            val targetIndex = Random.nextInt(prizes.size)
            val sliceAngle = 360f / prizes.size
            // Pointer is at the top (270 degrees).
            // Calculate final angle to land on targetIndex slice
            val randomSpins = Random.nextInt(5, 8) * 360f
            val targetAngle = randomSpins + (targetIndex * sliceAngle) + (sliceAngle / 2f)

            rotationAnim.snapTo(rotationAnim.value % 360f)
            rotationAnim.animateTo(
                targetValue = rotationAnim.value + targetAngle,
                animationSpec = tween(
                    durationMillis = 3800,
                    easing = FastOutSlowInEasing
                )
            )

            // Determine winning slice based on pointer (top)
            val normalizedAngle = (360f - (rotationAnim.value % 360f)) % 360f
            val winnerIdx = ((normalizedAngle / sliceAngle).toInt()) % prizes.size
            wonPrize = prizes[winnerIdx]
            isSpinning = false
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
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = TorqGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Torq Nitro Spin",
                            color = TorqTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Spin & Win Exclusive Workshop Perks",
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

            Spacer(modifier = Modifier.height(18.dp))

            // Wheel container with top pointer
            Box(
                modifier = Modifier
                    .size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                // Wheel Canvas
                Canvas(
                    modifier = Modifier
                        .size(250.dp)
                ) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val sliceAngle = 360f / prizes.size

                    // Draw slices rotated by animation
                    prizes.forEachIndexed { i, prize ->
                        val startAngle = rotationAnim.value + (i * sliceAngle)
                        drawArc(
                            color = prize.sliceColor,
                            startAngle = startAngle,
                            sweepAngle = sliceAngle,
                            useCenter = true,
                            size = Size(radius * 2, radius * 2),
                            topLeft = Offset(center.x - radius, center.y - radius),
                            style = Fill
                        )
                        drawArc(
                            color = TorqNavyBackground,
                            startAngle = startAngle,
                            sweepAngle = sliceAngle,
                            useCenter = true,
                            size = Size(radius * 2, radius * 2),
                            topLeft = Offset(center.x - radius, center.y - radius),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // Outer gold rim
                    drawCircle(
                        color = TorqGold,
                        radius = radius,
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Center Gold Cap
                    drawCircle(
                        color = TorqNavyBackground,
                        radius = 28.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = TorqGold,
                        radius = 24.dp.toPx(),
                        center = center
                    )
                }

                // Center Icon
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = TorqNavyBackground,
                    modifier = Modifier.size(24.dp)
                )

                // Top Arrow Pointer
                Canvas(
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.TopCenter)
                ) {
                    val path = Path().apply {
                        moveTo(size.width / 2f, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = TorqGold)
                    drawPath(path, color = TorqNavyBackground, style = Stroke(width = 1.5.dp.toPx()))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Spin Button
            TorqfixGoldButton(
                text = if (isSpinning) "Wheel is Spinning..." else "SPIN THE NITRO WHEEL",
                onClick = { spinTheWheel() },
                enabled = !isSpinning,
                icon = Icons.Default.EmojiEvents
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Winning Prize Card
            if (wonPrize != null) {
                val prize = wonPrize!!
                LuxuryCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = TorqNavyCardElevated,
                    borderColor = TorqGold
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TorqGreenSurface,
                            border = BorderStroke(1.dp, TorqGreen.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "🎉 CONGRATULATIONS! YOU WON",
                                color = TorqGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = prize.title,
                            color = TorqGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = prize.subtitle,
                            color = TorqTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Promo Code Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TorqNavySurface,
                            border = BorderStroke(1.dp, TorqGoldBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Promo Code", prize.promoCode)
                                    clipboard.setPrimaryClip(clip)
                                    copiedCode = true
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "VOUCHER PROMO CODE",
                                        color = TorqSlateGrey,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = prize.promoCode,
                                        color = TorqGold,
                                        fontSize = 17.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (copiedCode) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = if (copiedCode) TorqGreen else TorqGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (copiedCode) "Copied!" else "Tap to Copy",
                                        color = if (copiedCode) TorqGreen else TorqGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        TorqfixOutlinedButton(
                            text = "Apply to Next Repair Booking",
                            onClick = {
                                onDismiss()
                                onApplyPromoToBooking(prize.promoCode)
                            },
                            borderColor = TorqGold
                        )
                    }
                }
            } else {
                Text(
                    text = "1 Free Spin Available Today • Guaranteed Reward Every Spin",
                    color = TorqSlateMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

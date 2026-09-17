package com.example.ui.interactive

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import com.example.ui.theme.TorqRed
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary
import kotlin.math.roundToInt

data class CarBrandRate(
    val brand: String,
    val baseDealerFactor: Float,
    val partsGrade: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostSavingsCalculatorSheet(
    onDismiss: () -> Unit,
    onBookCalculatedService: (serviceName: String, pricePkr: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val carBrands = remember {
        listOf(
            CarBrandRate("Toyota / Lexus", 1.0f, "OEM Toyota Genuine & Denso"),
            CarBrandRate("Honda", 1.05f, "OEM Honda Genuine & NGK"),
            CarBrandRate("Audi", 1.55f, "VAG Germany & Liqui Moly High Tech"),
            CarBrandRate("BMW", 1.65f, "BMW TwinPower & Mahle"),
            CarBrandRate("Mercedes-Benz", 1.70f, "Mercedes Star Certified & Bilstein"),
            CarBrandRate("KIA / Hyundai", 0.95f, "Mobis Korea & Shell Helix Ultra")
        )
    }

    var selectedBrand by remember { mutableStateOf(carBrands[0]) }
    var mileageKm by remember { mutableFloatStateOf(40000f) }
    var selectedPackage by remember { mutableStateOf("Major 40K Interval") } // "Minor Periodic", "Major 40K Interval", "Brake Overhaul"

    // Base costs
    val (baseDealer, baseTorqfix) = when (selectedPackage) {
        "Minor Periodic" -> Pair(32000, 19500)
        "Major 40K Interval" -> Pair(78000, 47000)
        else -> Pair(52000, 31000) // Brake Overhaul
    }

    val brandFactor = selectedBrand.baseDealerFactor
    val dealerPrice = (baseDealer * brandFactor).roundToInt()
    val torqfixPrice = (baseTorqfix * brandFactor).roundToInt()
    val savingsPkr = dealerPrice - torqfixPrice
    val savingsPercent = ((savingsPkr.toFloat() / dealerPrice.toFloat()) * 100).roundToInt()

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
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = TorqGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dealership vs TORQFIX Calculator",
                            color = TorqTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Zero Markup • 100% Genuine Certified Parts",
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
                // Section 1: Select Brand
                Text(
                    text = "SELECT VEHICLE BRAND",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(carBrands) { brand ->
                        val isSelected = brand.brand == selectedBrand.brand
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) TorqGold else TorqNavyCardBorder),
                            modifier = Modifier.clickable { selectedBrand = brand }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = TorqGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = brand.brand,
                                    color = if (isSelected) TorqGold else TorqTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Mileage Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CURRENT VEHICLE ODOMETER",
                        color = TorqGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${mileageKm.roundToInt().toString().chunked(3).joinToString(",")} KM",
                        color = TorqGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Slider(
                    value = mileageKm,
                    onValueChange = { mileageKm = it },
                    valueRange = 10000f..120000f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = TorqGold,
                        activeTrackColor = TorqGold,
                        inactiveTrackColor = TorqNavySurface
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Service Package Type
                Text(
                    text = "MAINTENANCE SCOPE",
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
                    listOf("Minor Periodic", "Major 40K Interval", "Brake Overhaul").forEach { pkg ->
                        val isSelected = pkg == selectedPackage
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) TorqGold else TorqNavyCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPackage = pkg }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = pkg,
                                    color = if (isSelected) TorqGold else TorqTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = when (pkg) {
                                        "Minor Periodic" -> "Oil & 24 Pt."
                                        "Major 40K Interval" -> "All Fluids & Plugs"
                                        else -> "Pads & Rotors"
                                    },
                                    color = TorqSlateGrey,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Price Comparison Showcase Card
                LuxuryGoldCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "AUTHORIZED DEALERSHIP",
                                    color = TorqSlateGrey,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PKR %,d".format(dealerPrice),
                                    color = TorqSlateGrey,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "TORQFIX DIRECT PRICE",
                                    color = TorqGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "PKR %,d".format(torqfixPrice),
                                    color = TorqGold,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Savings Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TorqGreenSurface,
                            border = BorderStroke(1.dp, TorqGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = null,
                                        tint = TorqGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "YOU SAVE: PKR %,d ($savingsPercent%%)".format(savingsPkr),
                                        color = TorqGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Text(
                                    text = "+ FREE FLATBED",
                                    color = TorqGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Itemized Parts Included
                        Text(
                            text = "100% GENUINE SPECIFICATION PARTS INCLUDED:",
                            color = TorqTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val partsList = when (selectedPackage) {
                            "Minor Periodic" -> listOf(
                                "Liqui Moly / Mobil 1 Fully Synthetic Oil (4L/5L)",
                                "OEM Certified Micron Engine Oil Filter",
                                "Magnetic Sump Plug Washer & Seal",
                                "Complimentary 35-Point Computer Diagnostic Scan"
                            )
                            "Major 40K Interval" -> listOf(
                                "Fully Synthetic Motor Oil + Flush Treatment",
                                "Iridium Long-Life Spark Plugs (Set of 4/6)",
                                "Engine Air Filter & Active Carbon Cabin Filter",
                                "DOT 4 High-Temp Brake Fluid Bleed & Flush",
                                "Throttle Body Ultrasonic Decarbonization"
                            )
                            else -> listOf(
                                "Ceramic Heavy-Duty Brake Pads (Front & Rear)",
                                "Double-Disc CNC Rotor Skimming / Lathe Machining",
                                "Brake Caliper Piston Lubrication & Bleeding",
                                "Brake Fluid Pressure Calibration Test"
                            )
                        }

                        partsList.forEach { part ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(TorqGold)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = part,
                                    color = TorqTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TorqfixGoldButton(
                    text = "Lock in PKR %,d & Book with Flatbed Pickup".format(torqfixPrice),
                    onClick = {
                        onBookCalculatedService(
                            "${selectedBrand.brand} - $selectedPackage",
                            "PKR %,d".format(torqfixPrice)
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

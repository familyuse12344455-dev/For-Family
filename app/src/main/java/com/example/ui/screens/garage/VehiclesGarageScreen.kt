package com.example.ui.screens.garage

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.PakistaniCar
import com.example.data.catalog.PakistaniCarsCatalog
import com.example.data.models.BookingEntity
import com.example.data.models.VehicleEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldCard
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
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextDark
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary
import com.example.ui.viewmodel.TorqfixViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesGarageScreen(
    viewModel: TorqfixViewModel,
    onBookServiceForVehicle: (VehicleEntity) -> Unit,
    onNavigateToHistory: () -> Unit = {}
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val completedBookings by viewModel.completedBookings.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Garage, 1: History
    var showAddVehicleSheet by remember { mutableStateOf(false) }
    var selectedInvoiceBooking by remember { mutableStateOf<BookingEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TorqNavyBackground)
            .statusBarsPadding()
    ) {
        // === Top Header ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Garage",
                    color = TorqTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${vehicles.size} vehicles registered • VIP Concierge",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TorqGoldSurface,
                border = BorderStroke(1.dp, TorqGoldBorder),
                modifier = Modifier.clickable { showAddVehicleSheet = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = TorqGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Vehicle",
                        color = TorqGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // === Segmented Control (Garage vs PK Catalog vs Service History) ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TorqNavyCard)
                .border(1.dp, TorqNavyCardBorder, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = 0 },
                shape = RoundedCornerShape(10.dp),
                color = if (selectedTab == 0) TorqNavyCardElevated else Color.Transparent,
                border = if (selectedTab == 0) BorderStroke(1.dp, TorqGoldBorder) else null
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = if (selectedTab == 0) TorqGold else TorqSlateMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Garage (${vehicles.size})",
                        color = if (selectedTab == 0) TorqTextPrimary else TorqSlateMuted,
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1.2f)
                    .clickable { selectedTab = 1 },
                shape = RoundedCornerShape(10.dp),
                color = if (selectedTab == 1) TorqNavyCardElevated else Color.Transparent,
                border = if (selectedTab == 1) BorderStroke(1.dp, TorqGoldBorder) else null
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = null,
                        tint = if (selectedTab == 1) TorqGold else TorqSlateMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PK Catalog (25)",
                        color = if (selectedTab == 1) TorqGold else TorqSlateMuted,
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = 2 },
                shape = RoundedCornerShape(10.dp),
                color = if (selectedTab == 2) TorqNavyCardElevated else Color.Transparent,
                border = if (selectedTab == 2) BorderStroke(1.dp, TorqGoldBorder) else null
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = if (selectedTab == 2) TorqGold else TorqSlateMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "History (${completedBookings.size})",
                        color = if (selectedTab == 2) TorqTextPrimary else TorqSlateMuted,
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                // === MY VEHICLES GARAGE LIST ===
                if (vehicles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = TorqSlateMuted,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Your Garage is Empty",
                                color = TorqTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Choose from 25 iconic Pakistani cars or register your custom vehicle.",
                                color = TorqTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            TorqfixGoldButton(
                                text = "Explore 25 Pakistani Cars",
                                icon = Icons.Default.GridView,
                                onClick = { selectedTab = 1 }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            androidx.compose.material3.OutlinedButton(
                                onClick = { showAddVehicleSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, TorqGoldBorder),
                                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = TorqGold)
                            ) {
                                Text("Add Custom Vehicle", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Quick shortcut to catalog
                        item {
                            LuxuryCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = TorqNavyCardElevated,
                                borderColor = TorqGoldBorder,
                                onClick = { selectedTab = 1 }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(TorqGoldSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = null,
                                            tint = TorqGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Pakistan Car Catalog (25 Cars)",
                                            color = TorqTextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Civic Reborn, Fortuner, Alto, Bolan, Prado & more",
                                            color = TorqGold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = TorqGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        items(vehicles) { vehicle ->
                            VehicleGarageCard(
                                vehicle = vehicle,
                                onBookService = {
                                    viewModel.selectVehicle(vehicle)
                                    onBookServiceForVehicle(vehicle)
                                },
                                onDeleteVehicle = {
                                    viewModel.deleteVehicle(vehicle.id)
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
            1 -> {
                // === PAKISTANI CARS CATALOG (25 MODELS) ===
                PakistaniCarsCatalogView(
                    onAddCarToGarage = { car ->
                        viewModel.addNewVehicle(
                            make = car.brand,
                            model = "${car.modelName} (${car.pakistaniBadge})",
                            year = car.defaultYear,
                            plate = "LEA-26-889",
                            color = car.defaultColor,
                            trans = car.transmission,
                            fuel = car.fuelType
                        )
                        selectedTab = 0
                    },
                    onBookServiceForCar = { car ->
                        val pkVehicle = VehicleEntity(
                            id = "pk_${car.id}",
                            customerId = "",
                            make = car.brand,
                            model = "${car.modelName} (${car.pakistaniBadge})",
                            year = car.defaultYear,
                            licensePlate = "LEA-26-889",
                            transmission = car.transmission,
                            fuelType = car.fuelType,
                            color = car.defaultColor,
                            photoUrl = car.photoUrl
                        )
                        viewModel.addNewVehicle(
                            make = car.brand,
                            model = "${car.modelName} (${car.pakistaniBadge})",
                            year = car.defaultYear,
                            plate = "LEA-26-889",
                            color = car.defaultColor,
                            trans = car.transmission,
                            fuel = car.fuelType
                        )
                        viewModel.selectVehicle(pkVehicle)
                        onBookServiceForVehicle(pkVehicle)
                    }
                )
            }
            2 -> {
            // === SERVICE HISTORY & INVOICES ===
            if (completedBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = TorqSlateMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Past Service History",
                            color = TorqTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Completed concierge repairs and digital tax invoices will appear here.",
                            color = TorqTextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(completedBookings) { booking ->
                        LuxuryCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = TorqNavyCard,
                            borderColor = TorqNavyCardBorder,
                            onClick = { selectedInvoiceBooking = booking }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = booking.vehicleName,
                                            color = TorqTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Plate: ${booking.vehiclePlate} • ${booking.service}",
                                            color = TorqTextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = TorqGreenSurface,
                                        border = BorderStroke(1.dp, TorqGreen.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "COMPLETED",
                                            color = TorqGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Invoice Total: PKR ${booking.costFinalPkr}",
                                        color = TorqGold,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "View Digital Invoice →",
                                        color = TorqSky,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

    // === ADD VEHICLE MODAL SHEET ===
    if (showAddVehicleSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddVehicleSheet = false },
            sheetState = sheetState,
            containerColor = TorqNavyCard,
            contentColor = TorqTextPrimary
        ) {
            AddVehicleForm(
                onDismiss = { showAddVehicleSheet = false },
                onAddVehicle = { make, model, year, plate, color, trans, fuel ->
                    viewModel.addNewVehicle(make, model, year, plate, color, trans, fuel)
                    showAddVehicleSheet = false
                }
            )
        }
    }

    // === INVOICE DETAIL SHEET ===
    if (selectedInvoiceBooking != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedInvoiceBooking = null },
            sheetState = sheetState,
            containerColor = TorqNavyCard,
            contentColor = TorqTextPrimary
        ) {
            val booking = selectedInvoiceBooking!!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Service Invoice",
                        color = TorqTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { selectedInvoiceBooking = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TorqSlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LuxuryGoldCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "TORQFIX LUXURY CONCIERGE", color = TorqGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Booking #${booking.bookingId.takeLast(8)}", color = TorqTextSecondary, fontSize = 11.sp)

                        Divider(color = TorqNavyCardBorder, modifier = Modifier.padding(vertical = 10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Vehicle:", color = TorqTextSecondary, fontSize = 12.sp)
                            Text(text = booking.vehicleName, color = TorqTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Plate:", color = TorqTextSecondary, fontSize = 12.sp)
                            Text(text = booking.vehiclePlate, color = TorqTextPrimary, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Service:", color = TorqTextSecondary, fontSize = 12.sp)
                            Text(text = booking.service, color = TorqTextPrimary, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Pickup Flatbed:", color = TorqTextSecondary, fontSize = 12.sp)
                            Text(text = "COMPLIMENTARY", color = TorqGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Divider(color = TorqNavyCardBorder, modifier = Modifier.padding(vertical = 10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Paid:", color = TorqTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "PKR ${booking.costFinalPkr}", color = TorqGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TorqfixGoldButton(
                    text = "Close Invoice",
                    onClick = { selectedInvoiceBooking = null }
                )
            }
        }
    }
}

@Composable
private fun VehicleGarageCard(
    vehicle: VehicleEntity,
    onBookService: () -> Unit,
    onDeleteVehicle: () -> Unit
) {
    val matchedPkCar = remember(vehicle) {
        PakistaniCarsCatalog.allCars.firstOrNull { car ->
            vehicle.model.contains(car.modelName, ignoreCase = true) ||
            vehicle.fullName.contains(car.modelName, ignoreCase = true) ||
            vehicle.model.contains(car.pakistaniBadge, ignoreCase = true)
        }
    }

    LuxuryCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TorqNavyCard,
        borderColor = TorqNavyCardBorder
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // === Top Silhouette Banner & Health Status ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Health status badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TorqGreenSurface,
                        border = BorderStroke(1.dp, TorqGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(TorqGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HEALTH: OPTIMAL (98%)",
                                color = TorqGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (matchedPkCar != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TorqGoldSurface,
                            border = BorderStroke(1.dp, TorqGoldBorder)
                        ) {
                            Text(
                                text = matchedPkCar.pakistaniBadge,
                                color = TorqGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDeleteVehicle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Vehicle",
                        tint = TorqSlateMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // === Vehicle Silhouette & Info ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // High-End Car Silhouette Container
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(TorqNavyCardElevated, TorqNavySurface)
                            )
                        )
                        .border(1.dp, TorqGoldBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = TorqGold,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vehicle.fullName,
                        color = TorqTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Plate: ${vehicle.licensePlate} • ${vehicle.color}",
                        color = TorqTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = TorqNavySurface,
                            border = BorderStroke(0.5.dp, TorqSlateBorder)
                        ) {
                            Text(
                                text = vehicle.transmission,
                                color = TorqSky,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = TorqNavySurface,
                            border = BorderStroke(0.5.dp, TorqSlateBorder)
                        ) {
                            Text(
                                text = vehicle.fuelType,
                                color = TorqGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Divider(
                color = TorqNavyCardBorder,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            // === Health Status, Last Serviced & Quick Action ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Last Serviced",
                        color = TorqSlateMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "18 Aug 2026 • 24,100 km",
                        color = TorqTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TorqfixGoldButton(
                    text = "Book Service",
                    onClick = onBookService,
                    icon = Icons.Default.Build,
                    height = 42.dp,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
private fun AddVehicleForm(
    onDismiss: () -> Unit,
    onAddVehicle: (make: String, model: String, year: Int, plate: String, color: String, trans: String, fuel: String) -> Unit
) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var yearStr by remember { mutableStateOf("2024") }
    var plate by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("Obsidian Black") }
    var transmission by remember { mutableStateOf("Automatic") }
    var fuel by remember { mutableStateOf("Petrol") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Register Vehicle to Garage",
                color = TorqTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TorqSlateMuted)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Quick Select: Popular Pakistani Cars",
            color = TorqGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(PakistaniCarsCatalog.allCars) { car ->
                val isPicked = model.contains(car.modelName)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPicked) TorqGoldSurface else TorqNavySurface,
                    border = BorderStroke(1.dp, if (isPicked) TorqGold else TorqNavyCardBorder),
                    modifier = Modifier.clickable {
                        make = car.brand
                        model = "${car.modelName} (${car.pakistaniBadge})"
                        yearStr = car.defaultYear.toString()
                        color = car.defaultColor
                        transmission = car.transmission
                        fuel = car.fuelType
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${car.brand} ${car.modelName}",
                            color = if (isPicked) TorqGold else TorqTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = make,
            onValueChange = { make = it },
            label = { Text("Make (e.g. Mercedes-Benz, Porsche, Audi)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TorqGold,
                unfocusedBorderColor = TorqNavyCardBorder,
                focusedLabelColor = TorqGold,
                unfocusedLabelColor = TorqSlateGrey,
                focusedTextColor = TorqTextPrimary,
                unfocusedTextColor = TorqTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Model (e.g. C200 AMG, Macan, A6)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TorqGold,
                unfocusedBorderColor = TorqNavyCardBorder,
                focusedLabelColor = TorqGold,
                unfocusedLabelColor = TorqSlateGrey,
                focusedTextColor = TorqTextPrimary,
                unfocusedTextColor = TorqTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = yearStr,
                onValueChange = { yearStr = it },
                label = { Text("Year") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TorqGold,
                    unfocusedBorderColor = TorqNavyCardBorder,
                    focusedLabelColor = TorqGold,
                    unfocusedLabelColor = TorqSlateGrey,
                    focusedTextColor = TorqTextPrimary,
                    unfocusedTextColor = TorqTextPrimary
                )
            )

            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text("License Plate") },
                modifier = Modifier.weight(1.5f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TorqGold,
                    unfocusedBorderColor = TorqNavyCardBorder,
                    focusedLabelColor = TorqGold,
                    unfocusedLabelColor = TorqSlateGrey,
                    focusedTextColor = TorqTextPrimary,
                    unfocusedTextColor = TorqTextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TorqGold,
                unfocusedBorderColor = TorqNavyCardBorder,
                focusedLabelColor = TorqGold,
                unfocusedLabelColor = TorqSlateGrey,
                focusedTextColor = TorqTextPrimary,
                unfocusedTextColor = TorqTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        TorqfixGoldButton(
            text = "Add to Garage",
            enabled = make.isNotBlank() && model.isNotBlank() && plate.isNotBlank(),
            onClick = {
                val year = yearStr.toIntOrNull() ?: 2024
                onAddVehicle(make.trim(), model.trim(), year, plate.trim().uppercase(), color.trim(), transmission, fuel)
            }
        )
    }
}

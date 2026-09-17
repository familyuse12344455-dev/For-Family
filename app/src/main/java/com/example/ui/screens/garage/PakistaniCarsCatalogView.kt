package com.example.ui.screens.garage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.catalog.PakistaniCar
import com.example.data.catalog.PakistaniCarsCatalog
import com.example.ui.components.LuxuryCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.theme.*

@Composable
fun PakistaniCarsCatalogView(
    onAddCarToGarage: (PakistaniCar) -> Unit,
    onBookServiceForCar: (PakistaniCar) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("All Brands") }
    var selectedCategory by remember { mutableStateOf("All Types") }
    var selectedCarForDetails by remember { mutableStateOf<PakistaniCar?>(null) }

    val filteredCars = remember(searchQuery, selectedBrand, selectedCategory) {
        PakistaniCarsCatalog.allCars.filter { car ->
            val matchesSearch = searchQuery.isBlank() ||
                    car.modelName.contains(searchQuery, ignoreCase = true) ||
                    car.brand.contains(searchQuery, ignoreCase = true) ||
                    car.pakistaniBadge.contains(searchQuery, ignoreCase = true) ||
                    car.tagPhrase.contains(searchQuery, ignoreCase = true) ||
                    car.description.contains(searchQuery, ignoreCase = true)

            val matchesBrand = selectedBrand == "All Brands" || car.brand.equals(selectedBrand, ignoreCase = true)
            val matchesCategory = selectedCategory == "All Types" || car.category.equals(selectedCategory, ignoreCase = true)

            matchesSearch && matchesBrand && matchesCategory
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // === Hero Banner ===
        item {
            LuxuryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                backgroundColor = TorqNavyCard
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_pakistan_cars_showcase),
                            contentDescription = "Pakistan Automotive Fleet",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            TorqNavyBackground.copy(alpha = 0.6f),
                                            TorqNavyBackground
                                        )
                                    )
                                )
                        )
                        Surface(
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopStart),
                            shape = RoundedCornerShape(8.dp),
                            color = TorqGoldSurface,
                            border = BorderStroke(1.dp, TorqGoldBorder)
                        ) {
                            Text(
                                text = "PAKISTAN CAR FLEET (25 CARS)",
                                color = TorqGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Official Pakistan Vehicles Catalog",
                            color = TorqTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "From Civic Reborn, Fortuner & LC 300 to Alto, Bolan & Carry Daba. 1-tap add to your garage with verified OEM maintenance specs.",
                            color = TorqTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // === Search Bar ===
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("Search car (e.g. Reborn, Fortuner, Alto, Carry Daba, Cultus)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TorqGold
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TorqSlateMuted)
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TorqGold,
                    unfocusedBorderColor = TorqNavyCardBorder,
                    focusedContainerColor = TorqNavyCard,
                    unfocusedContainerColor = TorqNavyCard,
                    focusedTextColor = TorqTextPrimary,
                    unfocusedTextColor = TorqTextPrimary,
                    cursorColor = TorqGold
                ),
                singleLine = true
            )
        }

        // === Brand Filter Chips ===
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PakistaniCarsCatalog.brands.forEach { brand ->
                    val isSelected = selectedBrand == brand
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) TorqGoldSurface else TorqNavyCard,
                        border = BorderStroke(1.dp, if (isSelected) TorqGold else TorqNavyCardBorder),
                        modifier = Modifier.clickable { selectedBrand = brand }
                    ) {
                        Text(
                            text = brand,
                            color = if (isSelected) TorqGold else TorqTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // === Category Filter Chips ===
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PakistaniCarsCatalog.categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) TorqSky.copy(alpha = 0.15f) else TorqNavyCard,
                        border = BorderStroke(1.dp, if (isSelected) TorqSky else TorqNavyCardBorder),
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) TorqSky else TorqTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // === Header Count ===
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredCars.size} Pakistani Cars",
                    color = TorqTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Verified OEM Specs",
                    color = TorqGold,
                    fontSize = 12.sp
                )
            }
        }

        // === Car Items ===
        items(filteredCars, key = { it.id }) { car ->
            PakistaniCarItemCard(
                car = car,
                onViewDetails = { selectedCarForDetails = car },
                onAddToGarage = { onAddCarToGarage(car) },
                onBookService = { onBookServiceForCar(car) }
            )
        }
    }

    // === Car Details Sheet ===
    selectedCarForDetails?.let { car ->
        PakistaniCarDetailBottomSheet(
            car = car,
            onDismiss = { selectedCarForDetails = null },
            onAddToGarage = {
                onAddCarToGarage(car)
                selectedCarForDetails = null
            },
            onBookService = {
                onBookServiceForCar(car)
                selectedCarForDetails = null
            }
        )
    }
}

@Composable
fun PakistaniCarItemCard(
    car: PakistaniCar,
    onViewDetails: () -> Unit,
    onAddToGarage: () -> Unit,
    onBookService: () -> Unit,
    modifier: Modifier = Modifier
) {
    LuxuryCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        backgroundColor = TorqNavyCard,
        borderColor = TorqNavyCardBorder,
        onClick = onViewDetails
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Brand & Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = car.accentColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, car.accentColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = car.pakistaniBadge.uppercase(),
                        color = car.accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TorqNavySurface,
                    border = BorderStroke(0.5.dp, TorqSlateBorder)
                ) {
                    Text(
                        text = car.category,
                        color = TorqTextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Icon & Model Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(TorqNavySurface)
                        .border(1.dp, car.accentColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (car.photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = car.photoUrl,
                            contentDescription = car.modelName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = when {
                                car.category.contains("SUV", true) || car.category.contains("4x4", true) -> Icons.Default.DirectionsCar
                                car.category.contains("Pickup", true) || car.category.contains("Van", true) -> Icons.Default.LocalShipping
                                car.fuelType.contains("Hybrid", true) -> Icons.Default.Bolt
                                else -> Icons.Default.DirectionsCar
                            },
                            contentDescription = car.modelName,
                            tint = car.accentColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${car.brand} ${car.modelName}",
                        color = TorqTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = car.tagPhrase,
                        color = TorqGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Engine: ${car.engineDisplacement} • ${car.transmission}",
                        color = TorqTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Specs Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TorqNavySurface,
                    border = BorderStroke(0.5.dp, TorqSlateBorder)
                ) {
                    Text(
                        text = "Oil: ${car.oilSpec}",
                        color = TorqSky,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TorqNavySurface,
                    border = BorderStroke(0.5.dp, TorqSlateBorder)
                ) {
                    Text(
                        text = "Est. Service: PKR ${car.servicePriceEstimatePkr.toInt()}",
                        color = TorqGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Divider(
                color = TorqNavyCardBorder,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToGarage,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TorqGold
                    ),
                    border = BorderStroke(1.dp, TorqGoldBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add to Garage",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onBookService,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TorqGold,
                        contentColor = TorqNavyBackground
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Book Repair",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PakistaniCarDetailBottomSheet(
    car: PakistaniCar,
    onDismiss: () -> Unit,
    onAddToGarage: () -> Unit,
    onBookService: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TorqNavyCard,
        scrimColor = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
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
                Column {
                    Text(
                        text = "${car.brand} ${car.modelName}",
                        color = TorqTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = car.pakistaniBadge,
                        color = car.accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TorqSlateMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (car.photoUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, TorqGoldBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .background(TorqNavySurface)
                ) {
                    AsyncImage(
                        model = car.photoUrl,
                        contentDescription = "${car.brand} ${car.modelName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        TorqNavyCard.copy(alpha = 0.5f),
                                        TorqNavyCard.copy(alpha = 0.9f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = car.tagPhrase,
                            color = TorqGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Tagline Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = TorqNavySurface,
                border = BorderStroke(1.dp, TorqNavyCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Pakistani Automotive Profile",
                        color = TorqGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = car.description,
                        color = TorqTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Specifications Grid
            Text(
                text = "Vehicle Specifications & Maintenance",
                color = TorqTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            val specs = listOf(
                "Category" to car.category,
                "Engine & Power" to car.engineDisplacement,
                "Transmission" to car.transmission,
                "Fuel Type" to car.fuelType,
                "OEM Oil Specification" to car.oilSpec,
                "Default Color" to car.defaultColor,
                "Major Fleet Hubs" to car.popularInCities,
                "Full Service Cost Est." to "PKR ${car.servicePriceEstimatePkr.toInt()}"
            )

            specs.chunked(2).forEach { rowPair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowPair.forEach { (label, value) ->
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = TorqNavySurface,
                            border = BorderStroke(0.5.dp, TorqSlateBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = label, color = TorqSlateMuted, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = value,
                                    color = TorqTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    if (rowPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToGarage,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TorqGoldBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TorqGold)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Garage", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBookService,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TorqGold,
                        contentColor = TorqNavyBackground
                    )
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Book Repair", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

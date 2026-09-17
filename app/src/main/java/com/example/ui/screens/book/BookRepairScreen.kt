package com.example.ui.screens.book

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.util.UiFormatters
import com.example.ui.viewmodel.PhotoUploadUiState
import com.example.data.catalog.PakistaniCar
import com.example.data.catalog.PakistaniCarsCatalog
import com.example.data.models.SavedAddressEntity
import com.example.data.models.VehicleEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.components.TorqfixOutlinedButton
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldBorder
import com.example.ui.theme.TorqGoldLight
import com.example.ui.theme.TorqGoldSurface
import com.example.ui.theme.TorqGreen
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.theme.TorqNavyCard
import com.example.ui.theme.TorqNavyCardBorder
import com.example.ui.theme.TorqNavyCardElevated
import com.example.ui.theme.TorqNavySurface
import com.example.ui.theme.TorqSky
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextDark
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary
import com.example.ui.viewmodel.TorqfixViewModel

@Composable
fun BookRepairScreen(
    viewModel: TorqfixViewModel,
    onBookingCreated: (bookingId: String) -> Unit,
    onCancel: () -> Unit
) {
    val step by viewModel.wizardStep.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()
    val addresses by viewModel.savedAddresses.collectAsState()
    val selectedVehicle by viewModel.selectedVehicle.collectAsState()
    val selectedService by viewModel.selectedService.collectAsState()
    val problemDescription by viewModel.problemDescription.collectAsState()
    val selectedPhotos by viewModel.selectedPhotos.collectAsState()
    val selectedPhotoUris by viewModel.selectedPhotoUris.collectAsState()
    val photoUploadState by viewModel.photoUploadState.collectAsState()
    val pickupLocation by viewModel.pickupLocation.collectAsState()
    val pickupCity by viewModel.pickupCity.collectAsState()
    val preferredTime by viewModel.preferredTime.collectAsState()
    val bookingDate by viewModel.bookingDate.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    val context = LocalContext.current
    var uploadErrorMessage by remember { mutableStateOf<String?>(null) }

    // Preselect first vehicle if not set
    LaunchedEffect(vehicles) {
        if (selectedVehicle == null && vehicles.isNotEmpty()) {
            viewModel.selectVehicle(vehicles.first())
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TorqNavyBackground)
            .statusBarsPadding()
    ) {
        // === Top Bar ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (step > 1) viewModel.setWizardStep(step - 1) else onCancel()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TorqGold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Book a Repair & Concierge",
                    color = TorqTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Step $step of 6 • ${getStepTitle(step)}",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Progress Bar
        LinearProgressIndicator(
            progress = { step / 6f },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = TorqGold,
            trackColor = TorqNavyCardBorder
        )

        // Supabase Storage Error Banner
        if (uploadErrorMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF2A1515),
                border = BorderStroke(1.dp, Color(0xFFE53935))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = UiFormatters.sanitizeErrorMessage(uploadErrorMessage, "Unable to upload photo. Please try again."),
                        color = Color(0xFFFFCDD2),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { uploadErrorMessage = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // === Wizard Step Content ===
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "wizardStep"
            ) { targetStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(20.dp)
                ) {
                    when (targetStep) {
                        1 -> Step1SelectVehicle(
                            vehicles = vehicles,
                            selectedVehicle = selectedVehicle,
                            onSelectVehicle = { viewModel.selectVehicle(it) }
                        )
                        2 -> Step2SelectService(
                            selectedService = selectedService,
                            onSelectService = { viewModel.selectService(it) }
                        )
                        3 -> Step3DescribeProblem(
                            problemDescription = problemDescription,
                            onDescriptionChange = { viewModel.setProblemDescription(it) }
                        )
                        4 -> Step4UploadPhotos(
                            selectedPhotos = selectedPhotos,
                            selectedPhotoUris = selectedPhotoUris,
                            photoUploadState = photoUploadState,
                            onTogglePhoto = { viewModel.togglePhoto(it) },
                            onAddPhotoUri = { viewModel.addPhotoUri(it) },
                            onRemovePhotoUri = { viewModel.removePhotoUri(it) }
                        )
                        5 -> Step5PickupAndSchedule(
                            addresses = addresses,
                            currentLocation = pickupLocation,
                            currentCity = pickupCity,
                            onLocationSelected = { loc, city -> viewModel.setPickupLocation(loc, city) },
                            currentDate = bookingDate,
                            currentTime = preferredTime,
                            onTimeSelected = { d, t -> viewModel.setPreferredSchedule(d, t) }
                        )
                        6 -> Step6ConfirmBooking(
                            vehicle = selectedVehicle ?: vehicles.firstOrNull(),
                            service = selectedService,
                            problemDesc = problemDescription,
                            photosCount = selectedPhotoUris.size + selectedPhotos.size,
                            pickupLocation = pickupLocation,
                            preferredTime = "$bookingDate ($preferredTime)"
                        )
                    }
                }
            }
        }

        // === Bottom Control Bar ===
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TorqNavyCard,
            border = BorderStroke(1.dp, TorqNavyCardBorder),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    TorqfixOutlinedButton(
                        text = "Back",
                        onClick = { viewModel.setWizardStep(step - 1) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (step < 6) {
                    TorqfixGoldButton(
                        text = "Continue",
                        onClick = { viewModel.setWizardStep(step + 1) },
                        modifier = Modifier.weight(if (step > 1) 1.5f else 1f),
                        icon = Icons.Default.ArrowForward
                    )
                } else {
                    TorqfixGoldButton(
                        text = "Confirm Booking",
                        onClick = {
                            uploadErrorMessage = null
                            viewModel.submitBooking(
                                context = context,
                                onSuccess = { newBookingId ->
                                    onBookingCreated(newBookingId)
                                },
                                onError = { err ->
                                    uploadErrorMessage = err
                                }
                            )
                        },
                        loading = isSubmitting,
                        modifier = Modifier.weight(1.8f),
                        icon = Icons.Default.CheckCircle
                    )
                }
            }
        }
    }
}

private fun getStepTitle(step: Int): String {
    return when (step) {
        1 -> "01 Vehicle"
        2 -> "02 Service"
        3 -> "03 Problem"
        4 -> "04 Photos"
        5 -> "05 Pickup & Schedule"
        6 -> "06 Confirm"
        else -> ""
    }
}

@Composable
private fun Step1SelectVehicle(
    vehicles: List<VehicleEntity>,
    selectedVehicle: VehicleEntity?,
    onSelectVehicle: (VehicleEntity) -> Unit
) {
    var selectedBrand by remember { mutableStateOf("All Brands") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredCars = remember(selectedBrand, searchQuery) {
        PakistaniCarsCatalog.allCars.filter { car ->
            val matchesBrand = selectedBrand == "All Brands" || car.brand.equals(selectedBrand, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                car.brand.contains(searchQuery, ignoreCase = true) ||
                car.modelName.contains(searchQuery, ignoreCase = true) ||
                car.pakistaniBadge.contains(searchQuery, ignoreCase = true) ||
                car.category.contains(searchQuery, ignoreCase = true)
            matchesBrand && matchesSearch
        }
    }

    Text(
        text = "Which car needs service?",
        color = TorqTextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Select from your registered garage or pick from Pakistan's 25 popular models with full photos & verified specs.",
        color = TorqTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
    )

    // Highlight Selected Vehicle if any
    if (selectedVehicle != null) {
        LuxuryGoldCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TorqGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ACTIVE SELECTION FOR CONCIERGE REPAIR",
                            color = TorqGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TorqGoldSurface,
                        border = BorderStroke(1.dp, TorqGoldBorder)
                    ) {
                        Text(
                            text = "READY FOR BOOKING",
                            color = TorqGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!selectedVehicle.photoUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, TorqGoldBorder, RoundedCornerShape(12.dp))
                                .background(TorqNavySurface)
                        ) {
                            AsyncImage(
                                model = selectedVehicle.photoUrl,
                                contentDescription = selectedVehicle.fullName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TorqGoldSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = TorqGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedVehicle.fullName,
                            color = TorqTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Plate: ${selectedVehicle.licensePlate} • ${selectedVehicle.color}",
                            color = TorqGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${selectedVehicle.transmission} • ${selectedVehicle.fuelType}",
                            color = TorqTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = TorqGold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }

    // Registered Garage section (if user has vehicles)
    if (vehicles.isNotEmpty()) {
        Text(
            text = "Your Garage Vehicles",
            color = TorqTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        vehicles.forEach { vehicle ->
            val isSelected = selectedVehicle?.id == vehicle.id
            LuxuryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                backgroundColor = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
                borderColor = if (isSelected) TorqGold else TorqNavyCardBorder,
                borderWidth = if (isSelected) 1.5.dp else 1.dp,
                onClick = { onSelectVehicle(vehicle) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) TorqGoldSurface else TorqNavySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = if (isSelected) TorqGold else TorqSlateGrey,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = vehicle.fullName,
                            color = TorqTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Plate: ${vehicle.licensePlate} • ${vehicle.color}",
                            color = TorqTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = TorqGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
    }

    // PAKISTANI CARS CATALOG WITH FULL PHOTOS & NAMES SECTION
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Section Header Card with Fleet Photo
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = TorqNavyCard,
            border = BorderStroke(1.dp, TorqGoldBorder.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                ) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.img_pakistan_cars_fleet),
                        contentDescription = "Pakistani Cars Fleet",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, TorqNavyBackground.copy(alpha = 0.85f), TorqNavyBackground)
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TorqGoldSurface,
                            border = BorderStroke(0.5.dp, TorqGoldBorder)
                        ) {
                            Text(
                                text = "PAKISTANI CAR FLEET • 25 MODELS",
                                color = TorqGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Select Any Car With Photo & Verified Specs",
                            color = TorqTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Tap any car photo below to immediately select and book concierge repair & diagnostic service.",
                        color = TorqTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Search model (Civic, Fortuner, Alto, Bolan...)",
                                color = TorqSlateMuted,
                                fontSize = 13.sp
                            )
                        },
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
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = TorqSlateGrey
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TorqGold,
                            unfocusedBorderColor = TorqNavyCardBorder,
                            focusedContainerColor = TorqNavySurface,
                            unfocusedContainerColor = TorqNavySurface,
                            focusedTextColor = TorqTextPrimary,
                            unfocusedTextColor = TorqTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Brand Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(PakistaniCarsCatalog.brands) { brand ->
                            val isChipSelected = selectedBrand == brand
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isChipSelected) TorqGold else TorqNavySurface,
                                border = BorderStroke(1.dp, if (isChipSelected) TorqGold else TorqNavyCardBorder),
                                modifier = Modifier.clickable { selectedBrand = brand }
                            ) {
                                Text(
                                    text = brand,
                                    color = if (isChipSelected) TorqTextDark else TorqTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isChipSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "25 Pakistani Popular Cars (${filteredCars.size})",
            color = TorqTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // 25 Cars List with Photos and Names
        filteredCars.forEach { car ->
            val isCarSelected = selectedVehicle?.model?.contains(car.modelName) == true
            PakistaniCarBookingCard(
                car = car,
                isSelected = isCarSelected,
                onSelect = {
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
                    onSelectVehicle(pkVehicle)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PakistaniCarBookingCard(
    car: PakistaniCar,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        backgroundColor = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
        borderColor = if (isSelected) TorqGold else TorqNavyCardBorder,
        borderWidth = if (isSelected) 2.dp else 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Car Photo with Status Badge overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(TorqNavySurface)
            ) {
                if (car.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = car.photoUrl,
                        contentDescription = "${car.brand} ${car.modelName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Dark gradient overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    TorqNavyBackground.copy(alpha = 0.4f),
                                    TorqNavyBackground.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Top badges row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TorqGoldSurface,
                        border = BorderStroke(1.dp, TorqGoldBorder)
                    ) {
                        Text(
                            text = car.category.uppercase(),
                            color = TorqGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TorqGold,
                            border = BorderStroke(1.dp, TorqGoldLight)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = TorqTextDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SELECTED",
                                    color = TorqTextDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Bottom badge on image: Brand & Tag phrase
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${car.brand.uppercase()} • ${car.defaultYear}",
                        color = TorqGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = car.modelName,
                        color = TorqTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Card Body: Details, Specs, and 1-tap select button
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = car.pakistaniBadge,
                        color = TorqSky,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Est. PKR ${car.servicePriceEstimatePkr.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")}",
                        color = TorqGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = car.description,
                    color = TorqTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = TorqNavySurface,
                            border = BorderStroke(0.5.dp, TorqNavyCardBorder)
                        ) {
                            Text(
                                text = car.engineDisplacement,
                                color = TorqSlateGrey,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = TorqNavySurface,
                            border = BorderStroke(0.5.dp, TorqNavyCardBorder)
                        ) {
                            Text(
                                text = car.fuelType,
                                color = TorqSlateGrey,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) TorqGold else TorqNavySurface,
                        border = BorderStroke(1.dp, if (isSelected) TorqGold else TorqGoldBorder),
                        modifier = Modifier.clickable { onSelect() }
                    ) {
                        Text(
                            text = if (isSelected) "✓ SELECTED FOR REPAIR" else "SELECT THIS CAR",
                            color = if (isSelected) TorqTextDark else TorqGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step2SelectService(
    selectedService: String,
    onSelectService: (String) -> Unit
) {
    Text(
        text = "Select Service Package",
        color = TorqTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "All services include multi-point electronic diagnostics & sanitized return.",
        color = TorqTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    val packages = listOf(
        Pair("Periodic Maintenance & Diagnostic", "Engine oil, genuine filters, fluid top-up, OBD scan (PKR 18,500)"),
        Pair("Brake System Overhaul & Pads", "Ceramic brake pads, rotor skimming, fluid bleed (PKR 14,500)"),
        Pair("Engine Tuning & Transmission", "Spark plugs, throttle cleaning, ECU diagnostic tune (PKR 32,000)"),
        Pair("Suspension & Steering Care", "Bushing inspection, shock testing, wheel alignment (PKR 22,000)"),
        Pair("AC Gas & Climate Disinfection", "Compressor test, evaporator flush, cabin microfilter (PKR 16,000)"),
        Pair("Ceramic Coating & Luxury Detailing", "Multi-stage paint correction & 9H hydrophobic coating (PKR 25,000)"),
        Pair("Emergency Breakdown & Flatbed Towing", "Immediate flatbed recovery dispatch & inspection (PKR 9,500)")
    )

    packages.forEach { (title, desc) ->
        val isSelected = selectedService == title
        LuxuryCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            backgroundColor = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
            borderColor = if (isSelected) TorqGold else TorqNavyCardBorder,
            borderWidth = if (isSelected) 1.5.dp else 1.dp,
            onClick = { onSelectService(title) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = if (isSelected) TorqGold else TorqTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = desc,
                        color = TorqTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = TorqGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Step3DescribeProblem(
    problemDescription: String,
    onDescriptionChange: (String) -> Unit
) {
    Text(
        text = "Describe the Issue",
        color = TorqTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Mention symptoms, odd sounds, dashboard warning lights, or specific requests.",
        color = TorqTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    // Quick symptom tags
    val tags = listOf(
        "Check Engine Light",
        "Squeaking Brakes",
        "AC Not Chilling",
        "Engine Stutter",
        "Oil Leak",
        "High Speed Vibration"
    )

    Text(
        text = "Common Symptoms (Tap to append):",
        color = TorqSlateGrey,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.take(3).forEach { tag ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TorqNavySurface,
                border = BorderStroke(1.dp, TorqSlateBorder),
                modifier = Modifier.clickable {
                    val newText = if (problemDescription.isBlank()) tag else "$problemDescription, $tag"
                    onDescriptionChange(newText)
                }
            ) {
                Text(
                    text = tag,
                    color = TorqTextPrimary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }

    OutlinedTextField(
        value = problemDescription,
        onValueChange = onDescriptionChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(14.dp),
        placeholder = {
            Text(
                text = "e.g. Faint knocking sound from suspension when driving over speed humps. Check tire pressure sensors.",
                color = TorqSlateMuted,
                fontSize = 13.sp
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = TorqNavySurface,
            unfocusedContainerColor = TorqNavySurface,
            focusedBorderColor = TorqGold,
            unfocusedBorderColor = TorqNavyCardBorder,
            focusedTextColor = TorqTextPrimary,
            unfocusedTextColor = TorqTextPrimary
        )
    )
}

@Composable
private fun Step4UploadPhotos(
    selectedPhotos: List<String>,
    selectedPhotoUris: List<Uri>,
    photoUploadState: PhotoUploadUiState,
    onTogglePhoto: (String) -> Unit,
    onAddPhotoUri: (Uri) -> Unit,
    onRemovePhotoUri: (Uri) -> Unit
) {
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        uris.forEach { onAddPhotoUri(it) }
    }

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onAddPhotoUri(uri)
    }

    Text(
        text = "Vehicle Inspection Photos",
        color = TorqTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Select photos of your vehicle or issue from your gallery or camera for master technician pre-assessment.",
        color = TorqTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    // Technician Assessment Photo Status Card
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = TorqNavySurface,
        border = BorderStroke(1.dp, TorqNavyCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = TorqGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Technician Assessment Upload",
                    color = TorqGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Attached photos will be reviewed by certified workshop supervisors",
                color = TorqSlateMuted,
                fontSize = 11.sp
            )

            when (photoUploadState) {
                is PhotoUploadUiState.Uploading -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = TorqGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Uploading photo ${photoUploadState.currentFile} of ${photoUploadState.totalFiles}...",
                            color = TorqSky,
                            fontSize = 12.sp
                        )
                    }
                }
                is PhotoUploadUiState.Success -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = TorqGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All inspection photos uploaded and attached to your service request!",
                            color = TorqGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                is PhotoUploadUiState.Error -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = UiFormatters.sanitizeErrorMessage(photoUploadState.message, "Unable to upload photos. Please try again."),
                            color = Color(0xFFFFCDD2),
                            fontSize = 11.sp
                        )
                    }
                }
                is PhotoUploadUiState.Idle -> {
                    // Ready state
                }
            }
        }
    }

    // Primary Action: Pick real photos from Android device
    TorqfixGoldButton(
        text = "Select Photos from Gallery (${selectedPhotoUris.size} selected)",
        onClick = {
            multiplePhotoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        icon = Icons.Default.AddPhotoAlternate,
        modifier = Modifier.fillMaxWidth()
    )

    // Gallery of Real Device Photos selected
    if (selectedPhotoUris.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Selected Device Images (${selectedPhotoUris.size})",
            color = TorqTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tap X to remove before booking submission",
            color = TorqSlateMuted,
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(selectedPhotoUris) { uri ->
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, TorqGold, RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Device Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Remove button overlay
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(26.dp)
                            .clickable { onRemovePhotoUri(uri) },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove photo",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(16.dp)
                        )
                    }

                    // Ready tag
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = "Binary Ready",
                            color = TorqGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 2.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Quick Angle Guide (Tap to choose photo)",
        color = TorqTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Capture key diagnostic angles for your vehicle",
        color = TorqSlateMuted,
        fontSize = 12.sp,
        modifier = Modifier.padding(bottom = 10.dp)
    )

    val photoAngleSlots = listOf(
        Pair("Front & License Plate", "front_angle"),
        Pair("Dashboard Warning Lights", "dashboard_odometer"),
        Pair("Engine Bay Overview", "engine_bay"),
        Pair("Specific Scratch / Damage", "damage_focus")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        photoAngleSlots.forEach { (label, key) ->
            val isAttached = selectedPhotos.contains(key)
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isAttached) TorqNavyCardElevated else TorqNavyCard,
                borderColor = if (isAttached) TorqGold else TorqNavyCardBorder,
                borderWidth = if (isAttached) 1.5.dp else 1.dp,
                onClick = {
                    onTogglePhoto(key)
                    singlePhotoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isAttached) TorqGoldSurface else TorqNavySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAttached) Icons.Default.Check else Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = if (isAttached) TorqGold else TorqSlateGrey,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            color = TorqTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAttached) "✓ Angle selected • Tap to pick image" else "Tap to choose image for this angle",
                            color = if (isAttached) TorqGreen else TorqSlateMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step5PickupAndSchedule(
    addresses: List<SavedAddressEntity>,
    currentLocation: String,
    currentCity: String,
    onLocationSelected: (String, String) -> Unit,
    currentDate: String,
    currentTime: String,
    onTimeSelected: (String, String) -> Unit
) {
    Text(
        text = "Pickup Location & Schedule",
        color = TorqTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Our hydraulic flatbed carrier will arrive at this address at the designated time window.",
        color = TorqTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    Text(
        text = "Saved Addresses",
        color = TorqTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    addresses.forEach { addr ->
        val isSelected = currentLocation.contains(addr.fullAddress)
        LuxuryCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            backgroundColor = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
            borderColor = if (isSelected) TorqGold else TorqNavyCardBorder,
            borderWidth = if (isSelected) 1.5.dp else 1.dp,
            onClick = { onLocationSelected("${addr.fullAddress}, ${addr.city}", addr.city) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) TorqGoldSurface else TorqNavySurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (isSelected) TorqGold else TorqSlateGrey,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = addr.title,
                        color = if (isSelected) TorqGold else TorqTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = addr.fullAddress,
                        color = TorqTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Landmark: ${addr.landmark}",
                        color = TorqSlateMuted,
                        fontSize = 11.sp
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = TorqGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Select Pickup Date",
        color = TorqTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    val dates = listOf("Tomorrow, 12 Sept", "Saturday, 13 Sept", "Sunday, 14 Sept")
    val times = listOf(
        "Morning (09:00 AM – 12:00 PM)",
        "Afternoon (01:00 PM – 04:00 PM)",
        "Evening (05:00 PM – 08:00 PM)"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        dates.forEach { date ->
            val isSelected = currentDate == date
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTimeSelected(date, currentTime) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
                border = BorderStroke(1.dp, if (isSelected) TorqGold else TorqNavyCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (isSelected) TorqGold else TorqSlateMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date.split(",").first(),
                        color = if (isSelected) TorqGold else TorqTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = date.split(",").getOrNull(1)?.trim() ?: "",
                        color = TorqSlateMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Select Time Window",
        color = TorqTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    times.forEach { timeSlot ->
        val isSelected = currentTime == timeSlot
        LuxuryCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            backgroundColor = if (isSelected) TorqNavyCardElevated else TorqNavyCard,
            borderColor = if (isSelected) TorqGold else TorqNavyCardBorder,
            onClick = { onTimeSelected(currentDate, timeSlot) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeSlot,
                    color = if (isSelected) TorqGold else TorqTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = TorqGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Step6ConfirmBooking(
    vehicle: VehicleEntity?,
    service: String,
    problemDesc: String,
    photosCount: Int,
    pickupLocation: String,
    preferredTime: String
) {
    Text(
        text = "Review Booking Summary",
        color = TorqTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Please verify your details. Once submitted, a certified TORQFIX driver will be assigned.",
        color = TorqTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    LuxuryGoldCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Automotive Care Booking",
                    color = TorqGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TorqGoldSurface,
                    border = BorderStroke(1.dp, TorqGoldBorder)
                ) {
                    Text(
                        text = "VIP CONCIERGE",
                        color = TorqGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!vehicle?.photoUrl.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TorqNavySurface,
                    border = BorderStroke(1.dp, TorqGoldBorder.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TorqNavyBackground)
                        ) {
                            AsyncImage(
                                model = vehicle.photoUrl,
                                contentDescription = vehicle.fullName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = vehicle.fullName,
                                color = TorqTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Plate: ${vehicle.licensePlate} • ${vehicle.color}",
                                color = TorqGold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${vehicle.transmission} • ${vehicle.fuelType}",
                                color = TorqTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            SummaryRow(label = "Vehicle", value = vehicle?.fullName ?: "N/A")
            SummaryRow(label = "License Plate", value = vehicle?.licensePlate ?: "N/A")
            SummaryRow(label = "Service Package", value = service)
            SummaryRow(label = "Problem Notes", value = if (problemDesc.isBlank()) "Standard diagnostic" else problemDesc)
            SummaryRow(label = "Attached Photos", value = "$photosCount photo(s) selected")
            SummaryRow(label = "Pickup Location", value = pickupLocation)
            SummaryRow(label = "Scheduled Window", value = preferredTime)

            Divider(
                color = TorqNavyCardBorder,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Flatbed Concierge Transport",
                        color = TorqTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Estimated Repair & Service",
                        color = TorqTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "COMPLIMENTARY",
                        color = TorqGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PKR 18,500",
                        color = TorqGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LuxuryCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TorqNavySurface,
        borderColor = TorqSlateBorder
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = TorqGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Includes 6-Month TORQFIX Warranty & Genuine Parts Certification.",
                color = TorqTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TorqSlateGrey,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = TorqTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.5f)
        )
    }
}

package com.example.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.components.TorqfixOutlinedButton
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldSurface
import com.example.ui.theme.TorqNavyBackground
import com.example.ui.theme.TorqNavyCardBorder
import com.example.ui.theme.TorqNavyCardElevated
import com.example.ui.theme.TorqNavySurface
import com.example.ui.theme.TorqRed
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.viewmodel.TorqfixViewModel
import kotlinx.coroutines.launch

/**
 * Modern, Professional TORQFIX Customer Profile Screen
 *
 * Implements strict specifications:
 * - Top header with Avatar, Full Name, Email, Phone, Role/Status badge, Auth ID
 * - ACCOUNT card (Personal Info, Edit Profile, Change Password)
 * - MY TORQFIX card (My Vehicles, My Bookings, Service History, Invoices/Payments)
 * - SETTINGS card (Notifications, Privacy/Security, App Settings)
 * - SUPPORT card (Help & Support, Contact TORQFIX)
 * - Professional Edit Profile screen with only existing Supabase fields (full_name, phone)
 * - Real Supabase authenticated user ID and session persistence
 * - Loading, empty, and validation states
 * - Confirmation dialog before Supabase logout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: TorqfixViewModel,
    onLogoutSuccess: () -> Unit = {}
) {
    val customer by viewModel.currentCustomer.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()
    val completedBookings by viewModel.completedBookings.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog & Modal sheet visibility states
    var showEditProfileSheet by remember { mutableStateOf(false) }
    var showPersonalInfoSheet by remember { mutableStateOf(false) }
    var showChangePasswordSheet by remember { mutableStateOf(false) }

    var showMyVehiclesSheet by remember { mutableStateOf(false) }
    var showAddVehicleSheet by remember { mutableStateOf(false) }
    var showMyBookingsSheet by remember { mutableStateOf(false) }
    var showServiceHistorySheet by remember { mutableStateOf(false) }
    var showInvoicesSheet by remember { mutableStateOf(false) }

    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showPrivacySecuritySheet by remember { mutableStateOf(false) }
    var showAppSettingsSheet by remember { mutableStateOf(false) }

    var showHelpSupportSheet by remember { mutableStateOf(false) }
    var showContactTorqfixSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TorqNavyBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .padding(bottom = 60.dp)
        ) {
            // === 1. TOP HEADER SECTION ===
            ProfileHeaderSection(
                customer = customer,
                isLoading = customer == null,
                onAvatarClick = {
                    if (customer != null) {
                        showEditProfileSheet = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // === 2. ACCOUNT SECTION ===
            ProfileSectionCard(title = "ACCOUNT") {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "Personal Information",
                    subtitle = "View full verified customer account details",
                    onClick = { showPersonalInfoSheet = true }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Edit,
                    title = "Edit Profile",
                    subtitle = "Update full name and contact number",
                    onClick = { showEditProfileSheet = true }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Update password credentials or request reset link",
                    showDivider = false,
                    onClick = { showChangePasswordSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === 3. MY TORQFIX SECTION ===
            ProfileSectionCard(title = "MY TORQFIX") {
                ProfileMenuItem(
                    icon = Icons.Default.DirectionsCar,
                    title = "My Vehicles",
                    subtitle = if (vehicles.isNotEmpty()) "${vehicles.size} garage cars registered" else "Add your vehicles for valet care",
                    badgeText = "${vehicles.size}",
                    onClick = { showMyVehiclesSheet = true }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Build,
                    title = "My Bookings",
                    subtitle = if (allBookings.isNotEmpty()) "${allBookings.size} appointments recorded" else "No appointments scheduled",
                    badgeText = if (allBookings.isNotEmpty()) "${allBookings.size}" else null,
                    onClick = { showMyBookingsSheet = true }
                )

                ProfileMenuItem(
                    icon = Icons.Default.History,
                    title = "Service History",
                    subtitle = if (completedBookings.isNotEmpty()) "${completedBookings.size} completed jobs with warranty" else "Maintenance logs & inspections",
                    badgeText = if (completedBookings.isNotEmpty()) "${completedBookings.size}" else null,
                    onClick = { showServiceHistorySheet = true }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Receipt,
                    title = "Invoices / Payments",
                    subtitle = "Official tax invoices & digital receipts",
                    showDivider = false,
                    onClick = { showInvoicesSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === 4. SETTINGS SECTION ===
            ProfileSectionCard(title = "SETTINGS") {
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Valet arrival alerts, updates & diagnostic logs",
                    badgeText = if (notifications.isNotEmpty()) "${notifications.size} updates" else null,
                    onClick = { showNotificationsSheet = true }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Security,
                    title = "Privacy / Security",
                    subtitle = "Row-Level Security & encrypted session tokens",
                    badgeText = "RLS ACTIVE",
                    badgeColor = com.example.ui.theme.TorqGreen,
                    badgeSurface = com.example.ui.theme.TorqGreenSurface,
                    onClick = { showPrivacySecuritySheet = true }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "App Settings",
                    subtitle = "Version 2.4.0 • Connectivity & preferences",
                    showDivider = false,
                    onClick = { showAppSettingsSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === 5. SUPPORT SECTION ===
            ProfileSectionCard(title = "SUPPORT") {
                ProfileMenuItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    subtitle = "Valet guide, 6-month warranty FAQs & coverage",
                    onClick = { showHelpSupportSheet = true }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Headphones,
                    title = "Contact TORQFIX",
                    subtitle = "0800-TORQFIX • WhatsApp Concierge • Email",
                    showDivider = false,
                    onClick = { showContactTorqfixSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // === 6. LOGOUT BUTTON ===
            TorqfixOutlinedButton(
                text = "Log Out from TORQFIX",
                onClick = { showLogoutDialog = true },
                icon = Icons.Default.ExitToApp,
                borderColor = TorqRed.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_logout_button")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TORQFIX Pakistan Concierge v2.4.0 • Enterprise Cloud",
                color = TorqSlateMuted,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
        )
    }

    // === MODAL SHEETS & DIALOGS ===

    // 1. Edit Profile Sheet (Allows editing only fields in Supabase profiles: full_name, phone)
    if (showEditProfileSheet && customer != null) {
        EditProfileSheet(
            customer = customer!!,
            onDismiss = { showEditProfileSheet = false },
            onSave = { newName, newPhone, onComplete ->
                viewModel.updateProfileFields(
                    fullName = newName,
                    phone = newPhone,
                    onSuccess = {
                        onComplete(true, null)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Profile updated successfully!")
                        }
                    },
                    onError = { err ->
                        onComplete(false, err)
                    }
                )
            }
        )
    }

    // 2. Personal Information Sheet
    if (showPersonalInfoSheet && customer != null) {
        PersonalInfoSheet(
            customer = customer!!,
            onDismiss = { showPersonalInfoSheet = false }
        )
    }

    // 3. Change Password Sheet
    if (showChangePasswordSheet) {
        val targetEmail = customer?.email ?: "customer@torqfix.pk"
        ChangePasswordSheet(
            email = targetEmail,
            onDismiss = { showChangePasswordSheet = false },
            onUpdatePassword = { newPassword, onComplete ->
                viewModel.updateUserPassword(
                    newPassword = newPassword,
                    onSuccess = { msg ->
                        onComplete(true, msg)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(msg)
                        }
                    },
                    onError = { err ->
                        onComplete(false, err)
                    }
                )
            },
            onSendResetLink = { emailToReset, onComplete ->
                viewModel.sendPasswordReset(emailToReset) { success ->
                    if (success) {
                        onComplete(true, "Password reset instructions sent to $emailToReset!")
                    } else {
                        onComplete(false, "Failed to send reset link. Please verify your email.")
                    }
                }
            }
        )
    }

    // 4. My Vehicles Sheet
    if (showMyVehiclesSheet) {
        MyVehiclesSheet(
            vehicles = vehicles,
            onDismiss = { showMyVehiclesSheet = false },
            onAddNewVehicle = {
                showMyVehiclesSheet = false
                showAddVehicleSheet = true
            }
        )
    }

    // Add Vehicle Modal Sheet
    if (showAddVehicleSheet) {
        var make by remember { mutableStateOf("") }
        var model by remember { mutableStateOf("") }
        var yearStr by remember { mutableStateOf("2024") }
        var plate by remember { mutableStateOf("") }
        var color by remember { mutableStateOf("") }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showAddVehicleSheet = false },
            sheetState = sheetState,
            containerColor = TorqNavyCardElevated,
            contentColor = TorqTextPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 36.dp)
            ) {
                Text(
                    text = "Add Garage Vehicle",
                    color = TorqGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Register a new car to your TORQFIX garage",
                    color = TorqSlateGrey,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = make,
                    onValueChange = { make = it },
                    label = { Text("Vehicle Make (e.g. Porsche, Audi, Honda)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TorqNavySurface,
                        unfocusedContainerColor = TorqNavySurface,
                        focusedBorderColor = TorqGold,
                        unfocusedBorderColor = TorqNavyCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model & Trim (e.g. 911 Carrera S)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TorqNavySurface,
                        unfocusedContainerColor = TorqNavySurface,
                        focusedBorderColor = TorqGold,
                        unfocusedBorderColor = TorqNavyCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = yearStr,
                        onValueChange = { yearStr = it },
                        label = { Text("Model Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TorqNavySurface,
                            unfocusedContainerColor = TorqNavySurface,
                            focusedBorderColor = TorqGold,
                            unfocusedBorderColor = TorqNavyCardBorder
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it },
                        label = { Text("License Plate") },
                        modifier = Modifier.weight(1.4f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TorqNavySurface,
                            unfocusedContainerColor = TorqNavySurface,
                            focusedBorderColor = TorqGold,
                            unfocusedBorderColor = TorqNavyCardBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color (e.g. Obsidian Black, Guards Red)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TorqNavySurface,
                        unfocusedContainerColor = TorqNavySurface,
                        focusedBorderColor = TorqGold,
                        unfocusedBorderColor = TorqNavyCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                TorqfixGoldButton(
                    text = "Save Vehicle to Garage",
                    onClick = {
                        val cleanMake = make.trim()
                        val cleanModel = model.trim()
                        val yr = yearStr.toIntOrNull() ?: 2024
                        val cleanPlate = plate.trim().ifBlank { "REG-PENDING" }
                        val cleanColor = color.trim().ifBlank { "Metallic Grey" }

                        if (cleanMake.isNotBlank() && cleanModel.isNotBlank()) {
                            viewModel.addNewVehicle(
                                make = cleanMake,
                                model = cleanModel,
                                year = yr,
                                plate = cleanPlate,
                                color = cleanColor,
                                trans = "Automatic",
                                fuel = "Petrol"
                            )
                            showAddVehicleSheet = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Vehicle registered to your garage!")
                            }
                        }
                    },
                    icon = Icons.Default.Check
                )
            }
        }
    }

    // 5. My Bookings Sheet
    if (showMyBookingsSheet) {
        MyBookingsSheet(
            bookings = allBookings,
            onDismiss = { showMyBookingsSheet = false }
        )
    }

    // 6. Service History Sheet
    if (showServiceHistorySheet) {
        ServiceHistorySheet(
            completedBookings = completedBookings,
            onDismiss = { showServiceHistorySheet = false }
        )
    }

    // 7. Invoices Sheet
    if (showInvoicesSheet) {
        InvoicesSheet(
            bookings = allBookings,
            onDismiss = { showInvoicesSheet = false }
        )
    }

    // 8. Notifications Sheet
    if (showNotificationsSheet) {
        NotificationsSheet(
            notifications = notifications,
            onDismiss = { showNotificationsSheet = false }
        )
    }

    // 9. Privacy & Security Sheet
    if (showPrivacySecuritySheet && customer != null) {
        PrivacySecuritySheet(
            customer = customer!!,
            onDismiss = { showPrivacySecuritySheet = false }
        )
    }

    // 10. App Settings Sheet
    if (showAppSettingsSheet) {
        AppSettingsSheet(
            onDismiss = { showAppSettingsSheet = false }
        )
    }

    // 11. Help & Support Sheet
    if (showHelpSupportSheet) {
        HelpSupportSheet(
            onDismiss = { showHelpSupportSheet = false }
        )
    }

    // 12. Contact TORQFIX Sheet
    if (showContactTorqfixSheet) {
        ContactTorqfixSheet(
            onDismiss = { showContactTorqfixSheet = false }
        )
    }

    // 13. Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout {
                    onLogoutSuccess()
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

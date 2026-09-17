package com.example.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookingEntity
import com.example.data.models.CustomerProfileEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.VehicleEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.components.TorqfixOutlinedButton
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldBorder
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
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.theme.TorqTextSecondary
import com.example.ui.util.UiFormatters

/**
 * 1. PROFESSIONAL EDIT PROFILE SHEET
 * Allows editing ONLY fields that already exist in the Supabase `profiles` table:
 * full_name, phone (and displays email as read-only verified login).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileSheet(
    customer: CustomerProfileEntity,
    onDismiss: () -> Unit,
    onSave: (fullName: String, phone: String, onFinished: (Boolean, String?) -> Unit) -> Unit
) {
    var fullName by remember { mutableStateOf(customer.name) }
    var phone by remember { mutableStateOf(customer.phone) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Edit Customer Profile",
                        color = TorqGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Update your verified profile details",
                        color = TorqSlateGrey,
                        fontSize = 12.sp
                    )
                }

                CustomerAvatar(
                    customer = customer.copy(name = fullName),
                    size = 46.dp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Success feedback banner
            if (successMessage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TorqGreenSurface,
                    border = BorderStroke(1.dp, TorqGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TorqGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage!!,
                            color = TorqGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Error feedback banner
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TorqRed.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, TorqRed.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = TorqRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = TorqRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Authenticated Reference (Read-only)
            OutlinedTextField(
                value = UiFormatters.formatDisplayMembershipId(customer.id),
                onValueChange = {},
                readOnly = true,
                label = { Text("Account Reference") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = TorqSlateMuted
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TorqNavySurface.copy(alpha = 0.6f),
                    unfocusedContainerColor = TorqNavySurface.copy(alpha = 0.6f),
                    disabledContainerColor = TorqNavySurface.copy(alpha = 0.6f),
                    focusedTextColor = TorqSlateGrey,
                    unfocusedTextColor = TorqSlateGrey,
                    focusedBorderColor = TorqSlateBorder,
                    unfocusedBorderColor = TorqSlateBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Registered Email (Read-only credential)
            OutlinedTextField(
                value = customer.email,
                onValueChange = {},
                readOnly = true,
                label = { Text("Registered Email (Auth Locked)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = TorqGold
                    )
                },
                trailingIcon = {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TorqGreenSurface,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "VERIFIED",
                            color = TorqGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TorqNavySurface,
                    unfocusedContainerColor = TorqNavySurface,
                    focusedBorderColor = TorqSlateBorder,
                    unfocusedBorderColor = TorqSlateBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Editable: Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = null
                },
                label = { Text("Full Name *") },
                placeholder = { Text("e.g. Hamza Malik") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TorqGold
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TorqNavySurface,
                    unfocusedContainerColor = TorqNavySurface,
                    focusedBorderColor = TorqGold,
                    unfocusedBorderColor = TorqNavyCardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Editable: Phone Number
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    errorMessage = null
                },
                label = { Text("Mobile Phone Number *") },
                placeholder = { Text("e.g. +92 300 8472910") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = TorqGold
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TorqNavySurface,
                    unfocusedContainerColor = TorqNavySurface,
                    focusedBorderColor = TorqGold,
                    unfocusedBorderColor = TorqNavyCardBorder
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            if (isSaving) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = TorqGold,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Saving your changes...",
                        color = TorqGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                TorqfixGoldButton(
                    text = "Save Changes",
                    onClick = {
                        val cleanName = fullName.trim()
                        val cleanPhone = phone.trim()

                        if (cleanName.length < 2) {
                            errorMessage = "Please enter your valid full name."
                            return@TorqfixGoldButton
                        }
                        if (cleanPhone.length < 7) {
                            errorMessage = "Please enter a valid phone number (at least 7 digits)."
                            return@TorqfixGoldButton
                        }

                        isSaving = true
                        errorMessage = null
                        onSave(cleanName, cleanPhone) { success, err ->
                            isSaving = false
                            if (success) {
                                successMessage = "Profile updated successfully!"
                            } else {
                                errorMessage = UiFormatters.sanitizeErrorMessage(err, "Failed to update profile. Please try again.")
                            }
                        }
                    },
                    icon = Icons.Default.Check
                )
            }
        }
    }
}

/**
 * 2. PERSONAL INFORMATION SHEET
 * Displays complete verified profile record.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoSheet(
    customer: CustomerProfileEntity,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Personal Information",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Verified account details linked to your secure session",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            InfoItemRow(
                icon = Icons.Default.Shield,
                label = "Membership ID",
                value = UiFormatters.formatDisplayMembershipId(customer.id),
                isMonospace = true
            )

            InfoItemRow(
                icon = Icons.Default.Person,
                label = "Full Name",
                value = customer.name.ifBlank { "TORQFIX Member" }
            )

            InfoItemRow(
                icon = Icons.Default.Email,
                label = "Registered Email",
                value = customer.email,
                badge = "Verified"
            )

            InfoItemRow(
                icon = Icons.Default.Phone,
                label = "Phone Number",
                value = customer.phone.ifBlank { "Not provided" }
            )

            InfoItemRow(
                icon = Icons.Default.LocationOn,
                label = "City of Residence",
                value = customer.city.ifBlank { "Lahore, Pakistan" }
            )

            InfoItemRow(
                icon = Icons.Default.LocationOn,
                label = "Primary Pickup Address",
                value = customer.primaryAddress.ifBlank { "DHA Phase 5, Lahore" }
            )

            InfoItemRow(
                icon = Icons.Default.Build,
                label = "Member Tier",
                value = customer.memberTier.ifBlank { "VIP Concierge Elite" },
                badge = "Active"
            )

            Spacer(modifier = Modifier.height(20.dp))

            TorqfixOutlinedButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 3. CHANGE PASSWORD SHEET
 * Real Supabase password update + password reset email request.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordSheet(
    email: String,
    onDismiss: () -> Unit,
    onUpdatePassword: (newPassword: String, onComplete: (Boolean, String) -> Unit) -> Unit,
    onSendResetLink: (email: String, onComplete: (Boolean, String) -> Unit) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Change Password",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Update your credentials or request a secure recovery link",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (statusMessage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSuccess) TorqGreenSurface else TorqRed.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, if (isSuccess) TorqGreen.copy(alpha = 0.5f) else TorqRed.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isSuccess) TorqGreen else TorqRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusMessage!!,
                            color = if (isSuccess) TorqGreen else TorqRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // New Password Field
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    statusMessage = null
                },
                label = { Text("New Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TorqGold
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = TorqSlateGrey
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TorqNavySurface,
                    unfocusedContainerColor = TorqNavySurface,
                    focusedBorderColor = TorqGold,
                    unfocusedBorderColor = TorqNavyCardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    statusMessage = null
                },
                label = { Text("Confirm New Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TorqGold
                    )
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TorqNavySurface,
                    unfocusedContainerColor = TorqNavySurface,
                    focusedBorderColor = TorqGold,
                    unfocusedBorderColor = TorqNavyCardBorder
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = TorqGold,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Updating your password securely...",
                        color = TorqGold,
                        fontSize = 13.sp
                    )
                }
            } else {
                TorqfixGoldButton(
                    text = "Update Password Now",
                    onClick = {
                        val cleanNew = newPassword.trim()
                        val cleanConfirm = confirmPassword.trim()

                        if (cleanNew.length < 6) {
                            isSuccess = false
                            statusMessage = "Password must be at least 6 characters long."
                            return@TorqfixGoldButton
                        }
                        if (cleanNew != cleanConfirm) {
                            isSuccess = false
                            statusMessage = "Passwords do not match."
                            return@TorqfixGoldButton
                        }

                        isLoading = true
                        statusMessage = null
                        onUpdatePassword(cleanNew) { success, msg ->
                            isLoading = false
                            isSuccess = success
                            statusMessage = if (success) msg else UiFormatters.sanitizeErrorMessage(msg)
                            if (success) {
                                newPassword = ""
                                confirmPassword = ""
                            }
                        }
                    },
                    icon = Icons.Default.Lock
                )

                Spacer(modifier = Modifier.height(14.dp))

                TorqfixOutlinedButton(
                    text = "Send Reset Link to $email",
                    onClick = {
                        isLoading = true
                        statusMessage = null
                        onSendResetLink(email) { success, msg ->
                            isLoading = false
                            isSuccess = success
                            statusMessage = if (success) msg else UiFormatters.sanitizeErrorMessage(msg)
                        }
                    },
                    icon = Icons.Default.Email
                )
            }
        }
    }
}

/**
 * 4. MY VEHICLES SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVehiclesSheet(
    vehicles: List<VehicleEntity>,
    onDismiss: () -> Unit,
    onAddNewVehicle: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Registered Vehicles",
                        color = TorqGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${vehicles.size} vehicles in your personal garage",
                        color = TorqSlateGrey,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TorqGoldSurface,
                    border = BorderStroke(0.5.dp, TorqGoldBorder)
                ) {
                    Text(
                        text = "${vehicles.size} CARS",
                        color = TorqGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (vehicles.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TorqNavySurface,
                    border = BorderStroke(1.dp, TorqNavyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = TorqSlateMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No registered vehicles found",
                            color = TorqTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Add your car to schedule valet pickup and tracking",
                            color = TorqSlateGrey,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                vehicles.forEach { vehicle ->
                    LuxuryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        backgroundColor = TorqNavySurface,
                        borderColor = TorqNavyCardBorder
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TorqGoldSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = TorqGold,
                                    modifier = Modifier.size(20.dp)
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
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Plate: ${vehicle.licensePlate} • ${vehicle.color}",
                                    color = TorqSlateGrey,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TorqfixGoldButton(
                text = "+ Add New Vehicle",
                onClick = onAddNewVehicle
            )
        }
    }
}

/**
 * 5. MY BOOKINGS SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsSheet(
    bookings: List<BookingEntity>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "My Bookings",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Active & scheduled appointments with TORQFIX",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (bookings.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TorqNavySurface,
                    border = BorderStroke(1.dp, TorqNavyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = TorqSlateMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active bookings",
                            color = TorqTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "You can book a periodic service or repair anytime",
                            color = TorqSlateGrey,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                bookings.forEach { booking ->
                    LuxuryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        backgroundColor = TorqNavySurface,
                        borderColor = TorqNavyCardBorder
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = booking.service,
                                    color = TorqTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (booking.status.equals("COMPLETED", ignoreCase = true)) TorqGreenSurface else TorqGoldSurface,
                                    border = BorderStroke(0.5.dp, if (booking.status.equals("COMPLETED", ignoreCase = true)) TorqGreen else TorqGoldBorder)
                                ) {
                                    Text(
                                        text = booking.status.uppercase(),
                                        color = if (booking.status.equals("COMPLETED", ignoreCase = true)) TorqGreen else TorqGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Pickup: ${booking.pickupLocation}",
                                color = TorqSlateGrey,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Preferred Slot: ${booking.preferredTime}",
                                color = TorqSlateGrey,
                                fontSize = 12.sp
                            )

                            if (booking.costEstimatePkr > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Estimated Cost: Rs. ${String.format("%,d", booking.costEstimatePkr)}",
                                    color = TorqGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TorqfixOutlinedButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 6. SERVICE HISTORY SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceHistorySheet(
    completedBookings: List<BookingEntity>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Service History",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Past maintenance records and service warranty logs",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (completedBookings.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TorqNavySurface,
                    border = BorderStroke(1.dp, TorqNavyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TorqSlateMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No completed services yet",
                            color = TorqTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Your completed repair jobs and warranties will appear here",
                            color = TorqSlateGrey,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                completedBookings.forEach { booking ->
                    LuxuryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        backgroundColor = TorqNavySurface,
                        borderColor = TorqNavyCardBorder
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = booking.service,
                                    color = TorqTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "6 Mo. Warranty",
                                    color = TorqGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Service completed & verified by Master Technician",
                                color = TorqSlateGrey,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TorqfixOutlinedButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 7. INVOICES / PAYMENTS SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesSheet(
    bookings: List<BookingEntity>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Invoices & Payments",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Official digital invoices with breakdown",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            val invoicesList = bookings.filter { it.costEstimatePkr > 0 || it.costFinalPkr > 0 }

            if (invoicesList.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TorqNavySurface,
                    border = BorderStroke(1.dp, TorqNavyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = TorqSlateMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No invoices generated yet",
                            color = TorqTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Invoices will be published after diagnostic inspections",
                            color = TorqSlateGrey,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                invoicesList.forEachIndexed { index, booking ->
                    LuxuryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        backgroundColor = TorqNavySurface,
                        borderColor = TorqNavyCardBorder
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "INV-2026-00${index + 1}",
                                    color = TorqGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = TorqGreenSurface,
                                    border = BorderStroke(0.5.dp, TorqGreen.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "PAID",
                                        color = TorqGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = booking.service,
                                color = TorqTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Payment: Bank Transfer / 1Link",
                                    color = TorqSlateGrey,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Rs. ${String.format("%,d", if (booking.costFinalPkr > 0) booking.costFinalPkr else booking.costEstimatePkr)}",
                                    color = TorqTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TorqfixOutlinedButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 8. NOTIFICATIONS SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    notifications: List<NotificationEntity>,
    onDismiss: () -> Unit
) {
    var valAlerts by remember { mutableStateOf(true) }
    var serviceAlerts by remember { mutableStateOf(true) }
    var promoAlerts by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Notification Settings",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage push alerts and real-time vehicle updates",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Toggles
            NotificationToggleRow(
                title = "Driver & Valet Dispatch Alerts",
                subtitle = "Receive live notifications when your concierge is en route",
                checked = valAlerts,
                onCheckedChange = { valAlerts = it }
            )

            NotificationToggleRow(
                title = "Diagnostics & Work Progress",
                subtitle = "Instant alerts for health inspections and completed milestones",
                checked = serviceAlerts,
                onCheckedChange = { serviceAlerts = it }
            )

            NotificationToggleRow(
                title = "TORQFIX Privilege & Offers",
                subtitle = "Special seasonal discounts on synthetic oil & wheel alignment",
                checked = promoAlerts,
                onCheckedChange = { promoAlerts = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recent Alerts (${notifications.size})",
                color = TorqGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (notifications.isEmpty()) {
                Text(
                    text = "No recent notifications",
                    color = TorqSlateGrey,
                    fontSize = 12.sp
                )
            } else {
                notifications.take(5).forEach { notif ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TorqNavySurface,
                        border = BorderStroke(0.5.dp, TorqNavyCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = notif.title,
                                color = TorqTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notif.message,
                                color = TorqSlateGrey,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TorqfixOutlinedButton(
                text = "Done",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 9. PRIVACY & SECURITY SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecuritySheet(
    customer: CustomerProfileEntity,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Privacy & Security",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enterprise-grade protection for customer accounts",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            InfoItemRow(
                icon = Icons.Default.Shield,
                label = "Customer Data Isolation",
                value = "Active • Only your verified account can access records",
                badge = "PROTECTED"
            )

            InfoItemRow(
                icon = Icons.Default.Lock,
                label = "Account Security",
                value = "Encrypted session tokens with biometrics & auto-refresh"
            )

            InfoItemRow(
                icon = Icons.Default.Security,
                label = "Network Protection",
                value = "TLS 1.3 End-to-End Encrypted Communication"
            )

            InfoItemRow(
                icon = Icons.Default.CheckCircle,
                label = "Device Security",
                value = "Hardware-backed on-device sandbox encryption"
            )

            Spacer(modifier = Modifier.height(20.dp))

            TorqfixOutlinedButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 10. APP SETTINGS SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "App Settings",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "TORQFIX Customer Application configuration",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            InfoItemRow(
                icon = Icons.Default.Info,
                label = "Application Version",
                value = "v2.4.0 (Build 2026.09)"
            )

            InfoItemRow(
                icon = Icons.Default.CheckCircle,
                label = "Service Connectivity",
                value = "TORQFIX Cloud Network",
                badge = "ONLINE"
            )

            InfoItemRow(
                icon = Icons.Default.Shield,
                label = "Theme",
                value = "TORQFIX 24K Gold & Obsidian Navy"
            )

            InfoItemRow(
                icon = Icons.Default.LocationOn,
                label = "Regional Network",
                value = "Pakistan (Lahore, Karachi, Islamabad)"
            )

            Spacer(modifier = Modifier.height(20.dp))

            TorqfixOutlinedButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 11. HELP & SUPPORT SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Help & Support",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Frequently asked questions about TORQFIX services",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            FaqAccordionItem(
                question = "How does doorstep valet pickup work?",
                answer = "Once you submit a booking, our vetted chauffeur arrives at your designated address with digital inspection gear. Your car is safely driven or transported on a flatbed to our specialized facility."
            )

            FaqAccordionItem(
                question = "What is covered under the 6-Month Warranty?",
                answer = "All mechanical replacements, diagnostic services, and OEM parts carry a 6-month or 10,000 km warranty. Any covered defects are rectified with zero labor fees."
            )

            FaqAccordionItem(
                question = "Are OEM parts genuine?",
                answer = "Yes, 100% of parts installed by TORQFIX are factory-sealed genuine OEM components imported directly with verifiable serial numbers and warranty cards."
            )

            FaqAccordionItem(
                question = "What payment methods are supported?",
                answer = "We support all major payment options: 1Link Online Bank Transfer, Visa, MasterCard, UnionPay, JazzCash, and cash upon valet delivery."
            )

            Spacer(modifier = Modifier.height(16.dp))

            TorqfixOutlinedButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 12. CONTACT TORQFIX SHEET
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactTorqfixSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TorqNavyCardElevated,
        contentColor = TorqTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Contact TORQFIX",
                color = TorqGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "24/7 Concierge Hotline & Emergency Roadside Support",
                color = TorqSlateGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Phone Hotline
            ContactCard(
                icon = Icons.Default.Phone,
                title = "24/7 Toll-Free Hotline",
                value = "0800-TORQFIX (0800-8677349)",
                actionLabel = "CALL",
                onAction = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:08008677349"))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore if no dialer
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // WhatsApp Concierge
            ContactCard(
                icon = Icons.Default.Headphones,
                title = "VIP WhatsApp Concierge",
                value = "+92 300 8472910",
                actionLabel = "CHAT",
                onAction = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/923008472910"))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Email Support
            ContactCard(
                icon = Icons.Default.Email,
                title = "Official Email Support",
                value = "support@torqfix.pk",
                actionLabel = "EMAIL",
                onAction = {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@torqfix.pk"))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Headquarters
            ContactCard(
                icon = Icons.Default.LocationOn,
                title = "Flagship Facility",
                value = "Sector H, Phase 5 DHA, Lahore, Pakistan",
                actionLabel = null,
                onAction = {}
            )

            Spacer(modifier = Modifier.height(20.dp))

            TorqfixOutlinedButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

/**
 * 13. LOGOUT CONFIRMATION DIALOG
 */
@Composable
fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TorqNavyCardElevated,
        title = {
            Text(
                text = "Log Out from TORQFIX?",
                color = TorqTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = "You will be signed out of your account on this device. Your registered vehicles, service history, and settings remain securely saved to your account.",
                color = TorqTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = TorqRed)
            ) {
                Text(
                    text = "Log Out",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TorqGold)
            ) {
                Text("Cancel")
            }
        }
    )
}

// === Helper Sub-components ===

@Composable
private fun InfoItemRow(
    icon: ImageVector,
    label: String,
    value: String,
    badge: String? = null,
    isMonospace: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = TorqNavySurface,
        border = BorderStroke(0.5.dp, TorqNavyCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(TorqGoldSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TorqGold,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = TorqSlateGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = TorqTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default
                )
            }

            if (!badge.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = TorqGreenSurface,
                    border = BorderStroke(0.5.dp, TorqGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = badge,
                        color = TorqGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TorqTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TorqSlateGrey,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TorqNavyBackground,
                checkedTrackColor = TorqGold,
                uncheckedThumbColor = TorqSlateGrey,
                uncheckedTrackColor = TorqNavySurface
            )
        )
    }
    HorizontalDivider(color = TorqNavyCardBorder.copy(alpha = 0.5f))
}

@Composable
private fun FaqAccordionItem(
    question: String,
    answer: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = TorqNavySurface,
        border = BorderStroke(0.5.dp, TorqNavyCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    color = TorqTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TorqGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = TorqNavyCardBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = answer,
                        color = TorqTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactCard(
    icon: ImageVector,
    title: String,
    value: String,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = TorqNavySurface,
        border = BorderStroke(0.5.dp, TorqNavyCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TorqGoldSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TorqGold,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TorqSlateGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = TorqTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (actionLabel != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TorqGoldSurface,
                    border = BorderStroke(0.5.dp, TorqGoldBorder),
                    modifier = Modifier.clickable { onAction() }
                ) {
                    Text(
                        text = actionLabel,
                        color = TorqGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

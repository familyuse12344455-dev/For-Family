package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LuxuryCard
import com.example.ui.components.TorqfixGoldButton
import com.example.ui.theme.TorqGold
import com.example.ui.theme.TorqGoldBorder
import com.example.ui.theme.TorqGoldSurface
import com.example.ui.theme.TorqGreen
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
import com.example.ui.util.UiFormatters
import com.example.ui.viewmodel.TorqfixViewModel

enum class AuthTab {
    PHONE,
    EMAIL
}

enum class EmailMode {
    SIGN_IN,
    CREATE_ACCOUNT
}

@Composable
fun AuthScreen(
    viewModel: TorqfixViewModel,
    onAuthSuccess: () -> Unit
) {
    val phone by viewModel.authPhone.collectAsState()
    val otp by viewModel.authOtp.collectAsState()
    val isOtpSent by viewModel.isOtpSent.collectAsState()
    val error by viewModel.authError.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsState()
    val isResettingPassword by viewModel.isResettingPassword.collectAsState()
    val resetPasswordError by viewModel.resetPasswordError.collectAsState()
    val isPasswordResetSent by viewModel.isPasswordResetSent.collectAsState()

    var activeTab by remember { mutableStateOf(AuthTab.EMAIL) }
    var emailMode by remember { mutableStateOf(EmailMode.CREATE_ACCOUNT) }

    // Form inputs
    var customerName by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerPassword by remember { mutableStateOf("") }
    var customerConfirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf("Lahore") }

    // Forgot password dialog
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmailInput by remember { mutableStateOf("") }

    val cities = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad")
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TorqNavyBackground)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Premium Brand Emblem
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TorqNavyCardElevated)
                        .border(1.5.dp, TorqGold, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.torqfix_launcher_icon_1789152539383),
                        contentDescription = "Torqfix Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "TORQFIX",
                        color = TorqGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "LUXURY CAR CARE & FLEET CONCIERGE",
                        color = TorqSlateGrey,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Auth Container Card
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TorqNavyCard,
                borderColor = TorqNavyCardBorder
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Primary Method Switcher: Email vs Phone
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(TorqNavySurface)
                            .border(1.dp, TorqNavyCardBorder, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .clickable {
                                    activeTab = AuthTab.EMAIL
                                    viewModel.clearAuthMessages()
                                },
                            color = if (activeTab == AuthTab.EMAIL) TorqGoldSurface else Color.Transparent,
                            border = if (activeTab == AuthTab.EMAIL) androidx.compose.foundation.BorderStroke(1.dp, TorqGoldBorder) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (activeTab == AuthTab.EMAIL) TorqGold else TorqSlateGrey,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Email Account",
                                    color = if (activeTab == AuthTab.EMAIL) TorqGold else TorqSlateGrey,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .clickable {
                                    activeTab = AuthTab.PHONE
                                    viewModel.clearAuthMessages()
                                },
                            color = if (activeTab == AuthTab.PHONE) TorqGoldSurface else Color.Transparent,
                            border = if (activeTab == AuthTab.PHONE) androidx.compose.foundation.BorderStroke(1.dp, TorqGoldBorder) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = if (activeTab == AuthTab.PHONE) TorqGold else TorqSlateGrey,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Phone (Instant)",
                                    color = if (activeTab == AuthTab.PHONE) TorqGold else TorqSlateGrey,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "authTabTransition"
                    ) { tab ->
                        when (tab) {
                            AuthTab.EMAIL -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Sub-toggle: Sign In vs Create Account
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(TorqNavyBackground)
                                            .padding(3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .clickable {
                                                    emailMode = EmailMode.CREATE_ACCOUNT
                                                    viewModel.clearAuthMessages()
                                                },
                                            color = if (emailMode == EmailMode.CREATE_ACCOUNT) TorqNavyCardElevated else Color.Transparent,
                                            border = if (emailMode == EmailMode.CREATE_ACCOUNT) androidx.compose.foundation.BorderStroke(1.dp, TorqGold.copy(alpha = 0.5f)) else null
                                        ) {
                                            Text(
                                                text = "New Customer? Register",
                                                color = if (emailMode == EmailMode.CREATE_ACCOUNT) TorqGold else TorqSlateGrey,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }

                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .clickable {
                                                    emailMode = EmailMode.SIGN_IN
                                                    viewModel.clearAuthMessages()
                                                },
                                            color = if (emailMode == EmailMode.SIGN_IN) TorqNavyCardElevated else Color.Transparent,
                                            border = if (emailMode == EmailMode.SIGN_IN) androidx.compose.foundation.BorderStroke(1.dp, TorqGold.copy(alpha = 0.5f)) else null
                                        ) {
                                            Text(
                                                text = "Existing? Sign In",
                                                color = if (emailMode == EmailMode.SIGN_IN) TorqGold else TorqSlateGrey,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Header Text
                                    Text(
                                        text = if (emailMode == EmailMode.CREATE_ACCOUNT) "Create Your Garage Profile" else "Customer Sign In",
                                        color = TorqTextPrimary,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (emailMode == EmailMode.CREATE_ACCOUNT)
                                            "Register in seconds to book certified mechanics, monitor live bay streaming & track towing."
                                        else
                                            "Enter your registered email & password to access your vehicles and bookings.",
                                        color = TorqTextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                    )

                                    if (emailMode == EmailMode.CREATE_ACCOUNT) {
                                        // Full Name
                                        Text(
                                            text = "Full Name",
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        OutlinedTextField(
                                            value = customerName,
                                            onValueChange = { customerName = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            placeholder = { Text("e.g. Ali Raza", color = TorqSlateGrey, fontSize = 13.sp) },
                                            leadingIcon = {
                                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TorqGold)
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

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Mobile Phone
                                        Text(
                                            text = "Mobile Phone (For Booking WhatsApp & SMS)",
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        OutlinedTextField(
                                            value = customerPhone,
                                            onValueChange = { customerPhone = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            placeholder = { Text("300 1234567", color = TorqSlateGrey, fontSize = 13.sp) },
                                            leadingIcon = {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = TorqNavyCardElevated,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, TorqSlateBorder),
                                                    modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "🇵🇰 +92",
                                                        color = TorqGold,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                    )
                                                }
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

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // City Selection Chips
                                        Text(
                                            text = "Service City",
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(cities) { city ->
                                                val isSelected = selectedCity == city
                                                Surface(
                                                    shape = RoundedCornerShape(20.dp),
                                                    color = if (isSelected) TorqGoldSurface else TorqNavySurface,
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        1.dp,
                                                        if (isSelected) TorqGold else TorqNavyCardBorder
                                                    ),
                                                    modifier = Modifier.clickable { selectedCity = city }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.LocationOn,
                                                            contentDescription = null,
                                                            tint = if (isSelected) TorqGold else TorqSlateGrey,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = city,
                                                            color = if (isSelected) TorqGold else TorqSlateGrey,
                                                            fontSize = 12.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    // Email Address
                                    Text(
                                        text = "Email Address",
                                        color = TorqTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = customerEmail,
                                        onValueChange = { customerEmail = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        placeholder = { Text("name@example.com", color = TorqSlateGrey, fontSize = 13.sp) },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = TorqGold)
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

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Password
                                    Text(
                                        text = if (emailMode == EmailMode.CREATE_ACCOUNT) "Create Password (min 6 characters)" else "Password",
                                        color = TorqTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = customerPassword,
                                        onValueChange = { customerPassword = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TorqGold)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle password",
                                                    tint = if (isPasswordVisible) TorqGold else TorqSlateGrey,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
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

                                    if (emailMode == EmailMode.CREATE_ACCOUNT) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        // Confirm Password
                                        Text(
                                            text = "Confirm Password",
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        OutlinedTextField(
                                            value = customerConfirmPassword,
                                            onValueChange = { customerConfirmPassword = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                            leadingIcon = {
                                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TorqGold)
                                            },
                                            trailingIcon = {
                                                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                                    Icon(
                                                        imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                        contentDescription = "Toggle confirm password",
                                                        tint = if (isConfirmPasswordVisible) TorqGold else TorqSlateGrey,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
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
                                    } else {
                                        // Forgot password link
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 6.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = "Forgot Password?",
                                                color = TorqGold,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier
                                                    .clickable {
                                                        forgotPasswordEmailInput = customerEmail
                                                        viewModel.clearAuthMessages()
                                                        showForgotPasswordDialog = true
                                                    }
                                                    .padding(4.dp)
                                            )
                                        }
                                    }

                                    // Error Notification Banner
                                    if (!error.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = TorqRed.copy(alpha = 0.12f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, TorqRed.copy(alpha = 0.4f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = TorqRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = UiFormatters.sanitizeErrorMessage(error),
                                                    color = TorqRed,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }

                                    // Success Notification Banner
                                    if (!authSuccessMessage.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = TorqGreen.copy(alpha = 0.12f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, TorqGreen.copy(alpha = 0.4f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
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
                                                    text = authSuccessMessage ?: "",
                                                    color = TorqGreen,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Primary Action Button
                                    TorqfixGoldButton(
                                        text = if (isAuthenticating) {
                                            if (emailMode == EmailMode.CREATE_ACCOUNT) "Creating Account..." else "Signing In..."
                                        } else {
                                            if (emailMode == EmailMode.CREATE_ACCOUNT) "Create Account & Enter Garage" else "Sign In to Garage"
                                        },
                                        onClick = {
                                            if (emailMode == EmailMode.CREATE_ACCOUNT) {
                                                val formattedPhone = if (customerPhone.startsWith("+92")) customerPhone else "+92 ${customerPhone.trim()}"
                                                viewModel.signUpWithEmail(
                                                    fullName = customerName.ifBlank { "Customer" },
                                                    email = customerEmail,
                                                    phone = formattedPhone,
                                                    password = customerPassword,
                                                    confirmPassword = customerConfirmPassword,
                                                    onSuccess = { onAuthSuccess() },
                                                    onConfirmationRequired = { onAuthSuccess() }
                                                )
                                            } else {
                                                viewModel.loginWithEmail(
                                                    email = customerEmail,
                                                    password = customerPassword,
                                                    onSuccess = { onAuthSuccess() }
                                                )
                                            }
                                        },
                                        enabled = !isAuthenticating,
                                        icon = Icons.Default.ArrowForward
                                    )
                                }
                            }

                            AuthTab.PHONE -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    if (!isOtpSent) {
                                        Text(
                                            text = "Instant Mobile Access",
                                            color = TorqTextPrimary,
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Enter your Pakistani mobile number for instant OTP verification and garage booking.",
                                            color = TorqTextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                        )

                                        // Optional Name
                                        Text(
                                            text = "Your Name",
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        OutlinedTextField(
                                            value = customerName,
                                            onValueChange = { customerName = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            placeholder = { Text("e.g. Usman Tariq", color = TorqSlateGrey, fontSize = 13.sp) },
                                            leadingIcon = {
                                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TorqGold)
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

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Mobile Phone
                                        Text(
                                            text = "Mobile Phone Number",
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        OutlinedTextField(
                                            value = phone,
                                            onValueChange = { viewModel.setPhone(it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            placeholder = { Text("300 1234567", color = TorqSlateGrey, fontSize = 13.sp) },
                                            leadingIcon = {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = TorqNavyCardElevated,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, TorqSlateBorder),
                                                    modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "🇵🇰 +92",
                                                        color = TorqGold,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                    )
                                                }
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

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // City Selection Chips
                                        Text(
                                            text = "City",
                                            color = TorqTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(cities) { city ->
                                                val isSelected = selectedCity == city
                                                Surface(
                                                    shape = RoundedCornerShape(20.dp),
                                                    color = if (isSelected) TorqGoldSurface else TorqNavySurface,
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        1.dp,
                                                        if (isSelected) TorqGold else TorqNavyCardBorder
                                                    ),
                                                    modifier = Modifier.clickable { selectedCity = city }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.LocationOn,
                                                            contentDescription = null,
                                                            tint = if (isSelected) TorqGold else TorqSlateGrey,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = city,
                                                            color = if (isSelected) TorqGold else TorqSlateGrey,
                                                            fontSize = 12.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        if (error != null) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = UiFormatters.sanitizeErrorMessage(error),
                                                color = TorqRed,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        TorqfixGoldButton(
                                            text = "Send Secure OTP",
                                            onClick = { viewModel.sendOtp() },
                                            icon = Icons.Default.ArrowForward
                                        )
                                    } else {
                                        // OTP Verification Step
                                        Text(
                                            text = "Enter 4-Digit Code",
                                            color = TorqTextPrimary,
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "A verification code has been sent to +92 $phone.",
                                            color = TorqTextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                        )

                                        OutlinedTextField(
                                            value = otp,
                                            onValueChange = { if (it.length <= 6) viewModel.setOtp(it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            placeholder = { Text("Enter code (e.g. 1234)", color = TorqSlateGrey) },
                                            leadingIcon = {
                                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TorqGold)
                                            },
                                            trailingIcon = {
                                                // Quick test helper chip
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = TorqGoldSurface,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, TorqGoldBorder),
                                                    modifier = Modifier
                                                        .padding(end = 8.dp)
                                                        .clickable { viewModel.setOtp("1234") }
                                                ) {
                                                    Text(
                                                        text = "Auto-fill 1234",
                                                        color = TorqGold,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                    )
                                                }
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

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Change Number",
                                                color = TorqSlateGrey,
                                                fontSize = 12.sp,
                                                modifier = Modifier.clickable {
                                                    viewModel.setOtp("")
                                                }
                                            )
                                            Text(
                                                text = "Resend Code",
                                                color = TorqGold,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.clickable { viewModel.sendOtp() }
                                            )
                                        }

                                        if (error != null) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = UiFormatters.sanitizeErrorMessage(error),
                                                color = TorqRed,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        TorqfixGoldButton(
                                            text = if (isAuthenticating) "Verifying..." else "Verify & Enter App",
                                            onClick = {
                                                viewModel.verifyOtp(
                                                    name = customerName.ifBlank { "Customer" },
                                                    city = selectedCity,
                                                    onSuccess = { onAuthSuccess() }
                                                )
                                            },
                                            enabled = !isAuthenticating,
                                            icon = Icons.Default.CheckCircle
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Trust & Concierge Assurance Card
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TorqNavySurface,
                borderColor = TorqSlateBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = TorqGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TORQFIX Pakistan Concierge Network",
                            color = TorqTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Authorized repair hubs in Lahore, Karachi, Islamabad & Rawalpindi. 100% Genuine OEM parts, transparent digital quotes, and live workshop bay streaming.",
                        color = TorqTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Reset Password Dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!isResettingPassword) {
                        showForgotPasswordDialog = false
                        viewModel.clearAuthMessages()
                    }
                },
                containerColor = TorqNavyCard,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = TorqGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reset Password",
                            color = TorqTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Enter your registered email address. We will send a secure password reset link to your inbox.",
                            color = TorqTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = forgotPasswordEmailInput,
                            onValueChange = { forgotPasswordEmailInput = it },
                            placeholder = { Text("Registered Email", color = TorqSlateGrey, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = TorqGold)
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

                        if (!resetPasswordError.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = UiFormatters.sanitizeErrorMessage(resetPasswordError),
                                color = TorqRed,
                                fontSize = 11.sp
                            )
                        }

                        if (isPasswordResetSent) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✓ Password reset email sent. Please check your inbox.",
                                color = TorqGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                confirmButton = {
                    TorqfixGoldButton(
                        text = if (isResettingPassword) "Sending Link..." else if (isPasswordResetSent) "Link Sent" else "Send Reset Link",
                        onClick = {
                            if (!isResettingPassword && !isPasswordResetSent) {
                                viewModel.sendPasswordReset(forgotPasswordEmailInput) { }
                            }
                        },
                        enabled = !isResettingPassword && !isPasswordResetSent,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showForgotPasswordDialog = false
                            viewModel.clearAuthMessages()
                        },
                        enabled = !isResettingPassword
                    ) {
                        Text("Close", color = TorqSlateGrey)
                    }
                }
            )
        }
    }
}

package com.example.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookingEntity
import com.example.data.models.BookingStatus
import com.example.data.remote.supabase.SupabaseInvoiceDto
import com.example.data.remote.supabase.SupabasePaymentDto
import com.example.ui.components.BookingStatusBadge
import com.example.ui.components.LuxuryCard
import com.example.ui.components.TorqfixGoldButton
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
import com.example.ui.theme.TorqRedSurface
import com.example.ui.theme.TorqSlateBorder
import com.example.ui.theme.TorqSlateGrey
import com.example.ui.theme.TorqSlateMuted
import com.example.ui.theme.TorqTextPrimary
import com.example.ui.util.UiFormatters
import com.example.ui.theme.TorqTextSecondary
import com.example.ui.viewmodel.SelectedInvoiceState
import com.example.ui.viewmodel.TorqfixViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: TorqfixViewModel,
    onBack: () -> Unit
) {
    val completedBookings by viewModel.completedBookings.collectAsState()
    val invoicesByBookingId by viewModel.invoicesByBookingId.collectAsState()
    val isLoadingInvoices by viewModel.isLoadingInvoices.collectAsState()
    val selectedInvoiceState by viewModel.selectedInvoiceState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TorqGold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Service History & Invoices",
                    color = TorqTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${completedBookings.size} completed concierge services",
                    color = TorqGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { viewModel.loadInvoicesForCustomer() },
                enabled = !isLoadingInvoices
            ) {
                if (isLoadingInvoices) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = TorqGold,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Invoices",
                        tint = TorqGold
                    )
                }
            }
        }

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
                        text = "No Service History Yet",
                        color = TorqTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Completed car repairs, diagnostics and digital invoices will appear here.",
                        color = TorqTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
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
                    val invoice = invoicesByBookingId[booking.bookingId]

                    LuxuryCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = TorqNavyCard,
                        borderColor = TorqNavyCardBorder,
                        onClick = { viewModel.selectBookingForInvoice(booking) }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = TorqGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = booking.vehicleName,
                                        color = TorqTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                BookingStatusBadge(status = BookingStatus.COMPLETED)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${booking.service} • ${booking.bookingDate}",
                                color = TorqTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Workshop: ${booking.workshopName ?: "TORQFIX Elite Hub"}",
                                color = TorqSlateGrey,
                                fontSize = 11.sp
                            )

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
                                    if (invoice != null) {
                                        Text(
                                            text = "Invoice: ${invoice.invoiceNumber}",
                                            color = TorqSlateMuted,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "PKR ${formatAmount(invoice.totalAmount)}",
                                            color = TorqGold,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    } else {
                                        Text(
                                            text = "Invoice: Pending",
                                            color = TorqSlateMuted,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "No invoice available yet.",
                                            color = TorqSlateGrey,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = TorqNavySurface,
                                    border = BorderStroke(1.dp, TorqGoldBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = TorqGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "View Invoice",
                                            color = TorqGold,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
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

    // Direct Supabase Invoice & Payment Detail Bottom Sheet
    if (selectedInvoiceState !is SelectedInvoiceState.Idle) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissSelectedInvoice() },
            sheetState = sheetState,
            containerColor = TorqNavyCardElevated,
            contentColor = TorqTextPrimary
        ) {
            when (val state = selectedInvoiceState) {
                is SelectedInvoiceState.Idle -> Unit
                is SelectedInvoiceState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = TorqGold,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Retrieving Official Records",
                            color = TorqTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Loading invoice & payment details...",
                            color = TorqTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                is SelectedInvoiceState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = TorqRed,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Failed to Load Financial Records",
                            color = TorqTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = UiFormatters.sanitizeErrorMessage(state.message, "Unable to load financial details. Please try again."),
                            color = TorqSlateMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TorqNavySurface,
                                border = BorderStroke(1.dp, TorqSlateBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.dismissSelectedInvoice() }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Close",
                                        color = TorqTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TorqGoldSurface,
                                border = BorderStroke(1.dp, TorqGoldBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.retryLoadSelectedInvoice(state.booking) }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Retry",
                                        color = TorqGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                is SelectedInvoiceState.Success -> {
                    val booking = state.booking
                    val invoice = state.invoice
                    val payments = state.payments

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TAX INVOICE",
                                    color = TorqGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "TORQFIX Pakistan (Pvt) Ltd • NTN 8492041-3",
                                    color = TorqSlateGrey,
                                    fontSize = 11.sp
                                )
                            }

                            if (invoice != null) {
                                val isPaid = invoice.status.equals("PAID", ignoreCase = true)
                                val statusColor = if (isPaid) TorqGreen else TorqGold
                                val statusBg = if (isPaid) TorqGreenSurface else TorqGoldSurface

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = statusBg,
                                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = invoice.status.uppercase(),
                                        color = statusColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (invoice == null) {
                            // Safe empty state for completed service without invoice (Requirement 11)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TorqNavySurface,
                                border = BorderStroke(1.dp, TorqNavyCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = TorqSlateMuted,
                                        modifier = Modifier.size(38.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No invoice available yet.",
                                        color = TorqTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "An official tax invoice will be generated upon final workshop billing for Booking #${UiFormatters.formatDisplayBookingNumber(booking.bookingId)}.",
                                        color = TorqTextSecondary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            TorqfixGoldButton(
                                text = "Close",
                                onClick = { viewModel.dismissSelectedInvoice() }
                            )
                        } else {
                            // Real Invoice Information (Requirement 8)
                            LuxuryCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = TorqNavySurface,
                                borderColor = TorqNavyCardBorder
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    InvoiceRow("Invoice Number", invoice.invoiceNumber)
                                    InvoiceRow("Booking Reference", "Booking #${UiFormatters.formatDisplayBookingNumber(invoice.bookingId)}")
                                    InvoiceRow("Vehicle", booking.vehicleName)
                                    InvoiceRow("Registration", booking.vehiclePlate)
                                    InvoiceRow("Service", booking.service)
                                    InvoiceRow("Issued Date", formatIsoDate(invoice.issuedAt ?: invoice.createdAt))

                                    invoice.dueAt?.takeIf { it.isNotBlank() }?.let {
                                        InvoiceRow("Due Date", formatIsoDate(it))
                                    }

                                    invoice.paidAt?.takeIf { it.isNotBlank() }?.let {
                                        InvoiceRow("Paid Date", formatIsoDate(it))
                                    }

                                    invoice.notes?.takeIf { it.isNotBlank() }?.let {
                                        InvoiceRow("Notes", it)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Itemized Breakdown",
                                color = TorqTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Real breakdown from Supabase invoice columns
                            InvoiceItem("Subtotal", "PKR ${formatAmount(invoice.subtotal)}")

                            if (invoice.partsAmount > 0) {
                                InvoiceItem("Genuine OEM Parts & Consumables", "PKR ${formatAmount(invoice.partsAmount)}")
                            }

                            if (invoice.laborAmount > 0) {
                                InvoiceItem("Authorized Workshop Labor & Scan", "PKR ${formatAmount(invoice.laborAmount)}")
                            }

                            if (invoice.taxAmount > 0) {
                                InvoiceItem("Sales Tax & Regulatory Levies", "PKR ${formatAmount(invoice.taxAmount)}")
                            }

                            if (invoice.discountAmount > 0) {
                                InvoiceItem("Complimentary Privilege Discount", "- PKR ${formatAmount(invoice.discountAmount)}")
                            }

                            Divider(
                                color = TorqNavyCardBorder,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Amount (PKR)",
                                    color = TorqTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PKR ${formatAmount(invoice.totalAmount)}",
                                    color = TorqGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Payment Section (Requirement 9 & 12)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Payment Records",
                                    color = TorqTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${payments.size} recorded",
                                    color = TorqSlateMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (payments.isEmpty()) {
                                // Requirement 12: "If there is an invoice but no payments, show: 'No payments recorded.'"
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = TorqNavySurface,
                                    border = BorderStroke(1.dp, TorqNavyCardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Payment,
                                            contentDescription = null,
                                            tint = TorqSlateMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "No payments recorded.",
                                            color = TorqSlateMuted,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    payments.forEach { payment ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = TorqNavySurface,
                                            border = BorderStroke(1.dp, TorqNavyCardBorder),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.CheckCircle,
                                                            contentDescription = null,
                                                            tint = TorqGreen,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = payment.paymentMethod.replace('_', ' '),
                                                            color = TorqTextPrimary,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    Text(
                                                        text = "PKR ${formatAmount(payment.amount)}",
                                                        color = TorqGold,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))

                                                InvoiceRow("Status", payment.status)

                                                payment.transactionReference?.takeIf { it.isNotBlank() }?.let { ref ->
                                                    val displayRef = if (ref.contains("-") && ref.length > 20) {
                                                        "TXN-${ref.takeLast(8).uppercase()}"
                                                    } else {
                                                        ref
                                                    }
                                                    InvoiceRow("Transaction Ref", displayRef)
                                                }

                                                val paymentDate = payment.paidAt ?: payment.createdAt
                                                if (!paymentDate.isNullOrBlank()) {
                                                    InvoiceRow("Paid Date", formatIsoDate(paymentDate))
                                                }

                                                payment.notes?.takeIf { it.isNotBlank() }?.let { note ->
                                                    InvoiceRow("Notes", note)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Warranty Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TorqGoldSurface,
                                border = BorderStroke(1.dp, TorqGoldBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = TorqGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "TORQFIX 6-Month Warranty Active",
                                            color = TorqGold,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "All parts & workshop workmanship covered under warranty guarantee.",
                                            color = TorqTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            TorqfixGoldButton(
                                text = "Close Invoice",
                                onClick = { viewModel.dismissSelectedInvoice() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TorqSlateMuted, fontSize = 12.sp)
        Text(text = value, color = TorqTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InvoiceItem(title: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = TorqTextSecondary, fontSize = 12.sp)
        Text(
            text = price,
            color = if (price.startsWith("-")) TorqGreen else TorqTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatAmount(amount: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }.format(amount)
}

private fun formatIsoDate(iso: String?): String {
    if (iso.isNullOrBlank()) return "N/A"
    return try {
        val clean = iso.substringBefore('.').substringBefore('+').substringBefore('Z')
        clean.replace('T', ' ')
    } catch (e: Exception) {
        iso
    }
}

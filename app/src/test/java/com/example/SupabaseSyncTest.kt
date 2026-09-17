package com.example

import com.example.data.remote.supabase.SupabaseBookingDto
import com.example.data.remote.supabase.SupabaseConfig
import com.example.data.remote.supabase.SupabaseInvoiceDto
import com.example.data.remote.supabase.SupabasePaymentDto
import com.example.data.remote.supabase.SupabaseVehicleDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseSyncTest {

    @Test
    fun supabaseConfig_isValid() {
        assertTrue(SupabaseConfig.url.startsWith("https://"))
        assertTrue(SupabaseConfig.url.contains("supabase.co"))
        assertTrue(SupabaseConfig.anonKey.isNotEmpty())
        assertTrue(SupabaseConfig.isConfigured)
    }

    @Test
    fun supabaseBookingDto_creation() {
        val dto = SupabaseBookingDto(
            id = "tq_test_101",
            customerId = "cust_1",
            vehicleId = "veh_1",
            serviceType = "Oil & Filter Service",
            problemDescription = "Periodic maintenance",
            pickupAddress = "Gulberg III, Lahore",
            preferredPickupTime = "10:00 AM",
            status = "CONFIRMED",
            estimatedPrice = 45000.0
        )
        assertEquals("tq_test_101", dto.id)
        assertEquals("CONFIRMED", dto.status)
        assertEquals(45000.0, dto.estimatedPrice ?: 0.0, 0.01)
        assertNotNull(dto.serviceType)
    }

    @Test
    fun supabaseVehicleDto_creation() {
        val vehicle = SupabaseVehicleDto(
            id = "v_1",
            customerId = "cust_1",
            make = "Porsche",
            model = "911 GT3",
            year = 2023,
            color = "Guards Red",
            registrationNumber = "TORQ-911"
        )
        assertEquals("Porsche", vehicle.make)
        assertEquals("911 GT3", vehicle.model)
        assertEquals(2023, vehicle.year)
        assertEquals("TORQ-911", vehicle.registrationNumber)
        assertEquals("Guards Red", vehicle.color)
    }

    @Test
    fun supabaseInvoiceDto_creation() {
        val invoice = SupabaseInvoiceDto(
            id = "inv_uuid_101",
            invoiceNumber = "TQ-INV-2026-0042",
            bookingId = "booking_uuid_201",
            repairJobId = "job_uuid_301",
            customerId = "cust_uuid_1",
            vehicleId = "veh_uuid_1",
            workshopId = "ws_uuid_1",
            subtotal = 32000.0,
            partsAmount = 18000.0,
            laborAmount = 14000.0,
            taxAmount = 5120.0,
            discountAmount = 2000.0,
            totalAmount = 35120.0,
            status = "PAID",
            issuedAt = "2026-09-12T10:00:00Z",
            paidAt = "2026-09-12T10:15:00Z",
            notes = "Customer approved all OEM parts."
        )
        assertEquals("inv_uuid_101", invoice.id)
        assertEquals("TQ-INV-2026-0042", invoice.invoiceNumber)
        assertEquals("booking_uuid_201", invoice.bookingId)
        assertEquals(32000.0, invoice.subtotal, 0.01)
        assertEquals(18000.0, invoice.partsAmount, 0.01)
        assertEquals(14000.0, invoice.laborAmount, 0.01)
        assertEquals(5120.0, invoice.taxAmount, 0.01)
        assertEquals(2000.0, invoice.discountAmount, 0.01)
        assertEquals(35120.0, invoice.totalAmount, 0.01)
        assertEquals("PAID", invoice.status)
        assertEquals("Customer approved all OEM parts.", invoice.notes)
    }

    @Test
    fun supabasePaymentDto_creation() {
        val payment = SupabasePaymentDto(
            id = "pay_uuid_501",
            invoiceId = "inv_uuid_101",
            bookingId = "booking_uuid_201",
            customerId = "cust_uuid_1",
            amount = 35120.0,
            paymentMethod = "ONLINE_BANK_TRANSFER",
            status = "COMPLETED",
            transactionReference = "TXN-HBL-994821",
            paidAt = "2026-09-12T10:15:00Z",
            notes = "Settled via 1Link payment portal"
        )
        assertEquals("pay_uuid_501", payment.id)
        assertEquals("inv_uuid_101", payment.invoiceId)
        assertEquals(35120.0, payment.amount, 0.01)
        assertEquals("ONLINE_BANK_TRANSFER", payment.paymentMethod)
        assertEquals("COMPLETED", payment.status)
        assertEquals("TXN-HBL-994821", payment.transactionReference)
        assertEquals("Settled via 1Link payment portal", payment.notes)
    }
}

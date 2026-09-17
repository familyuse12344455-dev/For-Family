package com.example.data.remote.supabase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// === Supabase Auth DTOs ===

@JsonClass(generateAdapter = true)
data class SupabaseSignUpRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "data") val data: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseSignInRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SupabaseVerifyOtpRequest(
    @Json(name = "type") val type: String = "signup",
    @Json(name = "email") val email: String,
    @Json(name = "token") val token: String
)

@JsonClass(generateAdapter = true)
data class SupabaseRecoverPasswordRequest(
    @Json(name = "email") val email: String
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "user") val user: SupabaseAuthUser? = null,
    // When signUp is called with email confirmation or direct user response, fields are at the root
    @Json(name = "id") val rootId: String? = null,
    @Json(name = "email") val rootEmail: String? = null,
    @Json(name = "phone") val rootPhone: String? = null,
    @Json(name = "created_at") val rootCreatedAt: String? = null
) {
    val effectiveUserId: String?
        get() = user?.id?.ifBlank { null } ?: rootId?.ifBlank { null }

    val effectiveEmail: String?
        get() = user?.email?.ifBlank { null } ?: rootEmail?.ifBlank { null }
}

@JsonClass(generateAdapter = true)
data class SupabaseAuthUser(
    @Json(name = "id") val id: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

// === Database Table DTOs ===

@JsonClass(generateAdapter = true)
data class SupabaseProfileDto(
    @Json(name = "id") val id: String,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "role") val role: String = "CUSTOMER",
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseVehicleDto(
    @Json(name = "id") val id: String,
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "make") val make: String,
    @Json(name = "model") val model: String,
    @Json(name = "year") val year: Int,
    @Json(name = "color") val color: String? = null,
    @Json(name = "registration_number") val registrationNumber: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseBookingDto(
    @Json(name = "id") val id: String,
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "vehicle_id") val vehicleId: String,
    @Json(name = "service_type") val serviceType: String,
    @Json(name = "problem_description") val problemDescription: String? = null,
    @Json(name = "pickup_address") val pickupAddress: String,
    @Json(name = "pickup_latitude") val pickupLatitude: Double? = null,
    @Json(name = "pickup_longitude") val pickupLongitude: Double? = null,
    @Json(name = "preferred_pickup_time") val preferredPickupTime: String? = null,
    @Json(name = "customer_notes") val customerNotes: String? = null,
    @Json(name = "status") val status: String = "PENDING",
    @Json(name = "estimated_price") val estimatedPrice: Double? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseBookingPhotoDto(
    @Json(name = "id") val id: String,
    @Json(name = "booking_id") val bookingId: String,
    @Json(name = "uploaded_by") val uploadedBy: String,
    @Json(name = "photo_url") val photoUrl: String,
    @Json(name = "photo_type") val photoType: String = "CUSTOMER_UPLOAD",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseRepairPhotoDto(
    @Json(name = "id") val id: String,
    @Json(name = "repair_job_id") val repairJobId: String? = null,
    @Json(name = "photo_url") val photoUrl: String,
    @Json(name = "caption") val caption: String? = null,
    @Json(name = "uploaded_by") val uploadedBy: String? = null,
    @Json(name = "photo_type") val photoType: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseStatusHistoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "booking_id") val bookingId: String,
    @Json(name = "status") val status: String,
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseNotificationDto(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "booking_id") val bookingId: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "message") val message: String,
    @Json(name = "type") val type: String = "BOOKING_UPDATE",
    @Json(name = "is_read") val isRead: Boolean = false,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseInvoiceDto(
    @Json(name = "id") val id: String,
    @Json(name = "invoice_number") val invoiceNumber: String,
    @Json(name = "booking_id") val bookingId: String,
    @Json(name = "repair_job_id") val repairJobId: String? = null,
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "vehicle_id") val vehicleId: String? = null,
    @Json(name = "workshop_id") val workshopId: String? = null,
    @Json(name = "subtotal") val subtotal: Double = 0.0,
    @Json(name = "parts_amount") val partsAmount: Double = 0.0,
    @Json(name = "labor_amount") val laborAmount: Double = 0.0,
    @Json(name = "tax_amount") val taxAmount: Double = 0.0,
    @Json(name = "discount_amount") val discountAmount: Double = 0.0,
    @Json(name = "total_amount") val totalAmount: Double = 0.0,
    @Json(name = "status") val status: String = "PAID",
    @Json(name = "issued_at") val issuedAt: String? = null,
    @Json(name = "due_at") val dueAt: String? = null,
    @Json(name = "paid_at") val paidAt: String? = null,
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabasePaymentDto(
    @Json(name = "id") val id: String,
    @Json(name = "invoice_id") val invoiceId: String,
    @Json(name = "booking_id") val bookingId: String? = null,
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "payment_method") val paymentMethod: String = "ONLINE",
    @Json(name = "status") val status: String = "COMPLETED",
    @Json(name = "transaction_reference") val transactionReference: String? = null,
    @Json(name = "paid_at") val paidAt: String? = null,
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseRepairJobDto(
    @Json(name = "id") val id: String,
    @Json(name = "booking_id") val bookingId: String,
    @Json(name = "workshop_id") val workshopId: String? = null,
    @Json(name = "assigned_mechanic") val assignedMechanic: String? = null,
    @Json(name = "status") val status: String = "IN_PROGRESS",
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseRepairUpdateDto(
    @Json(name = "id") val id: String,
    @Json(name = "booking_id") val bookingId: String,
    @Json(name = "repair_job_id") val repairJobId: String? = null,
    @Json(name = "status") val status: String,
    @Json(name = "note") val note: String? = null,
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseRepairApprovalDto(
    @Json(name = "id") val id: String,
    @Json(name = "repair_job_id") val repairJobId: String,
    @Json(name = "estimated_price") val estimatedPrice: Double,
    @Json(name = "status") val status: String = "pending",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseServiceHistoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "booking_id") val bookingId: String,
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "service_summary") val serviceSummary: String,
    @Json(name = "final_price") val finalPrice: Double? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseDriverLocationDto(
    @Json(name = "id") val id: String,
    @Json(name = "driver_id") val driverId: String,
    @Json(name = "booking_id") val bookingId: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "heading") val heading: Double? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

data class SupabaseSyncStatus(
    val isConnected: Boolean = false,
    val isSyncing: Boolean = false,
    val lastSyncTime: String = "Never",
    val latencyMs: Long? = null,
    val errorMessage: String? = null,
    val syncedRecordsCount: Int = 0
)

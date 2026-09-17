package com.example.data.remote.supabase

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApiService {

    // === Connectivity Test ===
    @GET("rest/v1/")
    suspend fun pingRoot(): Response<ResponseBody>

    // === Supabase Auth Endpoints ===
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Body request: SupabaseSignUpRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signInWithPassword(
        @Body request: SupabaseSignInRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/verify")
    suspend fun verifyOtp(
        @Body request: SupabaseVerifyOtpRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body request: SupabaseRecoverPasswordRequest
    ): Response<ResponseBody>

    @POST("auth/v1/logout")
    suspend fun logout(): Response<ResponseBody>

    @retrofit2.http.PUT("auth/v1/user")
    suspend fun updateUser(
        @Body request: Map<String, String>
    ): Response<ResponseBody>

    // === Profiles ===
    @GET("rest/v1/profiles?select=*")
    suspend fun getProfileById(
        @Query("id") idFilter: String
    ): Response<List<SupabaseProfileDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("rest/v1/profiles")
    suspend fun upsertProfile(
        @Body profile: SupabaseProfileDto
    ): Response<List<SupabaseProfileDto>>

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("id") idFilter: String,
        @Body updates: Map<String, String>
    ): Response<List<SupabaseProfileDto>>

    // === Vehicles ===
    @GET("rest/v1/vehicles?select=*&order=created_at.desc")
    suspend fun getVehiclesByCustomer(
        @Query("customer_id") custFilter: String
    ): Response<List<SupabaseVehicleDto>>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/vehicles")
    suspend fun createVehicle(
        @Body vehicle: SupabaseVehicleDto
    ): Response<List<SupabaseVehicleDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("rest/v1/vehicles")
    suspend fun upsertVehicles(
        @Body vehicles: List<SupabaseVehicleDto>
    ): Response<List<SupabaseVehicleDto>>

    @DELETE("rest/v1/vehicles")
    suspend fun deleteVehicle(
        @Query("id") idFilter: String,
        @Query("customer_id") custFilter: String
    ): Response<ResponseBody>

    // === Bookings ===
    @GET("rest/v1/bookings?select=*&order=created_at.desc")
    suspend fun getBookingsByCustomer(
        @Query("customer_id") custFilter: String
    ): Response<List<SupabaseBookingDto>>

    @GET("rest/v1/bookings?select=*&limit=1")
    suspend fun getBookingById(
        @Query("id") idFilter: String
    ): Response<List<SupabaseBookingDto>>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/bookings")
    suspend fun createBooking(
        @Body booking: SupabaseBookingDto
    ): Response<List<SupabaseBookingDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("rest/v1/bookings")
    suspend fun upsertBookings(
        @Body bookings: List<SupabaseBookingDto>
    ): Response<List<SupabaseBookingDto>>

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/bookings")
    suspend fun updateBookingStatus(
        @Query("id") idFilter: String,
        @Body updates: Map<String, String>
    ): Response<ResponseBody>

    // === Booking Photos ===
    @GET("rest/v1/booking_photos?select=*&order=created_at.desc")
    suspend fun getBookingPhotos(
        @Query("booking_id") bookingFilter: String
    ): Response<List<SupabaseBookingPhotoDto>>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/booking_photos")
    suspend fun addBookingPhoto(
        @Body photo: SupabaseBookingPhotoDto
    ): Response<List<SupabaseBookingPhotoDto>>

    // === Repair Photos ===
    @GET("rest/v1/repair_photos?select=*&order=created_at.desc")
    suspend fun getRepairPhotos(
        @Query("repair_job_id") repairJobFilter: String? = null
    ): Response<List<SupabaseRepairPhotoDto>>

    // === Status History ===
    @GET("rest/v1/booking_status_history?select=*&order=created_at.asc")
    suspend fun getBookingStatusHistory(
        @Query("booking_id") bookingFilter: String
    ): Response<List<SupabaseStatusHistoryDto>>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/booking_status_history")
    suspend fun addStatusHistory(
        @Body history: SupabaseStatusHistoryDto
    ): Response<ResponseBody>

    // === Notifications ===
    @GET("rest/v1/notifications?select=*&order=created_at.desc")
    suspend fun getNotificationsByUser(
        @Query("user_id") userFilter: String
    ): Response<List<SupabaseNotificationDto>>

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/notifications")
    suspend fun markNotificationRead(
        @Query("id") idFilter: String,
        @Body body: Map<String, Boolean>
    ): Response<ResponseBody>

    // === Invoices & Payments ===
    @GET("rest/v1/invoices?select=*&order=created_at.desc")
    suspend fun getInvoicesByCustomer(
        @Query("customer_id") custFilter: String
    ): Response<List<SupabaseInvoiceDto>>

    @GET("rest/v1/invoices?select=*&order=created_at.desc")
    suspend fun getInvoiceByBookingAndCustomer(
        @Query("booking_id") bookingFilter: String,
        @Query("customer_id") custFilter: String
    ): Response<List<SupabaseInvoiceDto>>

    @GET("rest/v1/payments?select=*&order=created_at.desc")
    suspend fun getPaymentsByCustomer(
        @Query("customer_id") custFilter: String
    ): Response<List<SupabasePaymentDto>>

    @GET("rest/v1/payments?select=*&order=created_at.desc")
    suspend fun getPaymentsByInvoiceAndCustomer(
        @Query("invoice_id") invoiceFilter: String,
        @Query("customer_id") custFilter: String
    ): Response<List<SupabasePaymentDto>>

    // === Service History ===
    @GET("rest/v1/service_history?select=*")
    suspend fun getServiceHistoryByCustomer(
        @Query("customer_id") custFilter: String
    ): Response<List<SupabaseServiceHistoryDto>>

    // === Repair Updates & Approvals ===
    @GET("rest/v1/repair_updates?select=*&order=created_at.asc")
    suspend fun getRepairUpdates(
        @Query("booking_id") bookingFilter: String
    ): Response<List<SupabaseRepairUpdateDto>>

    @GET("rest/v1/repair_approvals?select=*")
    suspend fun getRepairApprovals(
        @Query("repair_job_id") jobFilter: String
    ): Response<List<SupabaseRepairApprovalDto>>

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/repair_approvals")
    suspend fun updateRepairApprovalStatus(
        @Query("id") idFilter: String,
        @Body body: Map<String, String>
    ): Response<ResponseBody>

    // === Live Driver Locations ===
    @GET("rest/v1/driver_locations?select=*&order=created_at.desc&limit=1")
    suspend fun getLatestDriverLocation(
        @Query("booking_id") bookingFilter: String
    ): Response<List<SupabaseDriverLocationDto>>
}

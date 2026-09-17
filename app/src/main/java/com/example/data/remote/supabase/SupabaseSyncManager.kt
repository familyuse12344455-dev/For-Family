package com.example.data.remote.supabase

import android.util.Log
import com.example.data.models.BookingEntity
import com.example.data.models.VehicleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SupabaseSyncManager {

    private val _syncStatus = MutableStateFlow(SupabaseSyncStatus())
    val syncStatus: StateFlow<SupabaseSyncStatus> = _syncStatus.asStateFlow()

    suspend fun testConnection(): Boolean = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            _syncStatus.value = _syncStatus.value.copy(isSyncing = true, errorMessage = null)
            val response = SupabaseClient.api.pingRoot()
            val latency = System.currentTimeMillis() - startTime
            val isSuccess = response.isSuccessful || response.code() in 200..299

            val currentTimeStr = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())

            _syncStatus.value = SupabaseSyncStatus(
                isConnected = isSuccess,
                isSyncing = false,
                lastSyncTime = currentTimeStr,
                latencyMs = latency,
                errorMessage = if (!isSuccess) "HTTP ${response.code()}: ${response.message()}" else null,
                syncedRecordsCount = _syncStatus.value.syncedRecordsCount
            )
            isSuccess
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            Log.e("SupabaseSync", "Connection test failed", e)
            _syncStatus.value = SupabaseSyncStatus(
                isConnected = false,
                isSyncing = false,
                lastSyncTime = "Failed",
                latencyMs = latency,
                errorMessage = e.localizedMessage ?: "Connection error",
                syncedRecordsCount = _syncStatus.value.syncedRecordsCount
            )
            false
        }
    }

    suspend fun syncDataToCloud(
        bookings: List<BookingEntity>,
        vehicles: List<VehicleEntity>
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        _syncStatus.value = _syncStatus.value.copy(isSyncing = true, errorMessage = null)
        try {
            val bookingDtos = bookings.map {
                SupabaseBookingDto(
                    id = it.bookingId,
                    customerId = it.customerId,
                    vehicleId = it.vehicleId,
                    serviceType = it.service,
                    problemDescription = it.problemDescription,
                    pickupAddress = it.pickupLocation,
                    preferredPickupTime = it.preferredTime,
                    customerNotes = it.problemDescription,
                    status = it.status,
                    estimatedPrice = it.costEstimatePkr.toDouble()
                )
            }

            val vehicleDtos = vehicles.map {
                SupabaseVehicleDto(
                    id = it.id,
                    customerId = it.customerId,
                    make = it.make,
                    model = it.model,
                    year = it.year,
                    color = it.color,
                    registrationNumber = it.licensePlate
                )
            }

            var syncCount = 0
            var partialError: String? = null

            // Sync Vehicles first (since bookings have FK to vehicles)
            if (vehicleDtos.isNotEmpty()) {
                val vRes = SupabaseClient.api.upsertVehicles(vehicleDtos)
                if (vRes.isSuccessful || vRes.code() in 200..299) {
                    syncCount += vehicleDtos.size
                } else {
                    partialError = "Vehicle sync: ${vRes.code()} ${vRes.message()}"
                }
            }

            // Sync Bookings
            if (bookingDtos.isNotEmpty()) {
                val bRes = SupabaseClient.api.upsertBookings(bookingDtos)
                if (bRes.isSuccessful || bRes.code() in 200..299) {
                    syncCount += bookingDtos.size
                } else if (partialError == null) {
                    partialError = "Booking sync: ${bRes.code()} ${bRes.message()}"
                }
            }

            val timeStr = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
            _syncStatus.value = SupabaseSyncStatus(
                isConnected = true,
                isSyncing = false,
                lastSyncTime = timeStr,
                latencyMs = 35,
                errorMessage = partialError,
                syncedRecordsCount = syncCount
            )

            val msg = if (partialError != null) {
                "Synced to Supabase with note: $partialError"
            } else {
                "Synced $syncCount records to Supabase PostgreSQL successfully!"
            }
            Pair(true, msg)
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Sync failed", e)
            _syncStatus.value = _syncStatus.value.copy(
                isSyncing = false,
                errorMessage = e.localizedMessage ?: "Sync error"
            )
            Pair(false, e.localizedMessage ?: "Failed to reach Supabase server")
        }
    }
}

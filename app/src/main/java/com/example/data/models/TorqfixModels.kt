package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookingStatus(val displayName: String, val stepIndex: Int, val description: String) {
    PENDING_DRIVER(
        displayName = "Booking Confirmed",
        stepIndex = 0,
        description = "Your booking is confirmed. Broadcasting to nearby certified drivers..."
    ),
    DRIVER_ASSIGNED(
        displayName = "Driver Assigned",
        stepIndex = 1,
        description = "Driver is on the way to your pickup location."
    ),
    VEHICLE_PICKED(
        displayName = "Vehicle Picked Up",
        stepIndex = 2,
        description = "Vehicle inspected and loaded for transport to workshop."
    ),
    WORKSHOP_RECEIVED(
        displayName = "Workshop Received",
        stepIndex = 3,
        description = "Vehicle safely arrived at TORQFIX Elite Center."
    ),
    REPAIRING(
        displayName = "Repairing",
        stepIndex = 4,
        description = "Certified master technicians are servicing your vehicle."
    ),
    TESTING(
        displayName = "Quality Testing",
        stepIndex = 5,
        description = "Diagnostic scans, road performance and safety tests in progress."
    ),
    READY_FOR_RETURN(
        displayName = "Ready for Return",
        stepIndex = 6,
        description = "Service completed & sanitized. Preparing for return dispatch."
    ),
    RETURNING(
        displayName = "Returning to Customer",
        stepIndex = 7,
        description = "Driver is en route to deliver your car back to you."
    ),
    COMPLETED(
        displayName = "Booking Completed",
        stepIndex = 8,
        description = "Delivered successfully. Service warranty activated."
    );

    companion object {
        fun fromString(value: String): BookingStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PENDING_DRIVER
        }
    }
}

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val bookingId: String,
    val customerId: String,
    val vehicleId: String,
    val vehicleName: String,
    val vehiclePlate: String,
    val service: String,
    val problemDescription: String,
    val vehiclePhotosJson: String = "",
    val pickupLocation: String,
    val pickupCity: String = "Lahore",
    val preferredTime: String,
    val bookingDate: String,
    val status: String = BookingStatus.PENDING_DRIVER.name,
    val assignedDriverId: String? = null,
    val driverName: String? = null,
    val driverPhone: String? = null,
    val driverRating: Float? = 4.9f,
    val driverVehicle: String? = null,
    val driverEtaMinutes: Int? = null,
    val driverLiveLat: Double? = null,
    val driverLiveLng: Double? = null,
    val assignedWorkshopId: String? = null,
    val workshopName: String? = null,
    val workshopAddress: String? = null,
    val workshopMechanic: String? = null,
    val estimatedCompletion: String? = null,
    val costEstimatePkr: Int = 18500,
    val costFinalPkr: Int = 18500,
    val invoiceDetailsJson: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val transmission: String = "Automatic",
    val fuelType: String = "Petrol",
    val color: String = "Metallic Black",
    val photoUrl: String? = null
) {
    val fullName: String get() = "$year $make $model"
}

@Entity(tableName = "customers")
data class CustomerProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val city: String,
    val avatarType: String = "GOLD_CREST",
    val avatarColorHex: String = "#FFD700",
    val primaryAddress: String = "House 42, Sector J, DHA Phase 5, Lahore",
    val emergencyContactName: String = "Zain Malik (Emergency Contact)",
    val emergencyContactPhone: String = "+92 321 4567890",
    val bio: String = "Automotive enthusiast. Demands OEM German parts & synthetic care.",
    val preferredFuel: String = "Hi-Octane 97 RON",
    val memberTier: String = "VIP Concierge Elite",
    val passwordHash: String = "",
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "saved_addresses")
data class SavedAddressEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val title: String, // Home, Office, Studio
    val fullAddress: String,
    val city: String,
    val landmark: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val bookingId: String,
    val title: String,
    val message: String,
    val statusType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class InvoiceItem(
    val title: String,
    val category: String,
    val amountPkr: Int
)

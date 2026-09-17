package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.BookingEntity
import com.example.data.models.CustomerProfileEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.SavedAddressEntity
import com.example.data.models.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TorqfixDao {

    // === Security & Customer Isolation ===
    @Query("SELECT * FROM customers WHERE isLoggedIn = 1 LIMIT 1")
    fun getCurrentCustomer(): Flow<CustomerProfileEntity?>

    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    suspend fun getCustomerById(customerId: String): CustomerProfileEntity?

    @Query("SELECT * FROM customers WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getCustomerByEmail(email: String): CustomerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerProfileEntity)

    @Query("UPDATE customers SET isLoggedIn = 0")
    suspend fun logoutAll()

    // === Bookings (Filtered by Customer ID) ===
    @Query("SELECT * FROM bookings WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getBookingsForCustomer(customerId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE customerId = :customerId AND status != 'COMPLETED' ORDER BY createdAt DESC LIMIT 1")
    fun getActiveBookingForCustomer(customerId: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId LIMIT 1")
    fun getBookingById(bookingId: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId LIMIT 1")
    suspend fun getBookingByIdDirect(bookingId: String): BookingEntity?

    @Query("SELECT * FROM bookings WHERE customerId = :customerId AND status = 'COMPLETED' ORDER BY createdAt DESC")
    fun getCompletedBookingsForCustomer(customerId: String): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    // === Vehicles (Filtered by Customer ID) ===
    @Query("SELECT * FROM vehicles WHERE customerId = :customerId ORDER BY year DESC")
    fun getVehiclesForCustomer(customerId: String): Flow<List<VehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE id = :id AND customerId = :customerId")
    suspend fun deleteVehicle(id: String, customerId: String)

    // === Saved Addresses (Filtered by Customer ID) ===
    @Query("SELECT * FROM saved_addresses WHERE customerId = :customerId")
    fun getAddressesForCustomer(customerId: String): Flow<List<SavedAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: SavedAddressEntity)

    // === Notifications (Filtered by Customer ID) ===
    @Query("SELECT * FROM notifications WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getNotificationsForCustomer(customerId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE customerId = :customerId AND isRead = 0")
    fun getUnreadNotificationCount(customerId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)
}

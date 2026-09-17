package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.BookingEntity
import com.example.data.models.CustomerProfileEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.SavedAddressEntity
import com.example.data.models.VehicleEntity
import com.example.data.remote.supabase.SupabaseClient
import com.example.data.remote.supabase.SupabaseInvoiceDto
import com.example.data.remote.supabase.SupabasePaymentDto
import com.example.data.remote.supabase.SupabaseRepairPhotoDto
import com.example.data.remote.supabase.SupabaseSessionManager
import com.example.data.remote.supabase.SupabaseStorageService
import com.example.data.remote.supabase.SupabaseSyncManager
import com.example.data.remote.supabase.SupabaseSyncStatus
import com.example.data.repository.SignUpResult
import com.example.data.repository.TorqfixRepository
import com.example.ui.util.UiFormatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SelectedInvoiceState {
    object Idle : SelectedInvoiceState()
    data class Loading(val booking: BookingEntity) : SelectedInvoiceState()
    data class Success(
        val booking: BookingEntity,
        val invoice: SupabaseInvoiceDto?,
        val payments: List<SupabasePaymentDto>
    ) : SelectedInvoiceState()
    data class Error(
        val booking: BookingEntity,
        val message: String
    ) : SelectedInvoiceState()
}

sealed class PhotoUploadUiState {
    object Idle : PhotoUploadUiState()
    data class Uploading(val currentFile: Int, val totalFiles: Int) : PhotoUploadUiState()
    data class Success(val uploadedUrls: List<String>) : PhotoUploadUiState()
    data class Error(val errorType: SupabaseStorageService.StorageErrorType, val message: String) : PhotoUploadUiState()
}

class TorqfixViewModel(
    private val repository: TorqfixRepository
) : ViewModel() {

    // === Customer Profile & Authentication State ===
    val currentCustomer: StateFlow<CustomerProfileEntity?> = repository.currentCustomer
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _authPhone = MutableStateFlow("+92 300 8472910")
    val authPhone = _authPhone.asStateFlow()

    private val _authEmail = MutableStateFlow("")
    val authEmail = _authEmail.asStateFlow()

    private val _authPassword = MutableStateFlow("")
    val authPassword = _authPassword.asStateFlow()

    private val _authOtp = MutableStateFlow("")
    val authOtp = _authOtp.asStateFlow()

    private val _isOtpSent = MutableStateFlow(false)
    val isOtpSent = _isOtpSent.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError = _authError.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage = _authSuccessMessage.asStateFlow()

    private val _isResettingPassword = MutableStateFlow(false)
    val isResettingPassword = _isResettingPassword.asStateFlow()

    private val _resetPasswordError = MutableStateFlow<String?>(null)
    val resetPasswordError = _resetPasswordError.asStateFlow()

    private val _isPasswordResetSent = MutableStateFlow(false)
    val isPasswordResetSent = _isPasswordResetSent.asStateFlow()

    // === Bookings & Vehicles (Scoped to logged in Customer) ===
    val vehicles: StateFlow<List<VehicleEntity>> = currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.getVehicles(customer.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedAddresses: StateFlow<List<SavedAddressEntity>> = currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.getSavedAddresses(customer.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBooking: StateFlow<BookingEntity?> = currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.getActiveBooking(customer.id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val completedBookings: StateFlow<List<BookingEntity>> = currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.getCompletedBookings(customer.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<BookingEntity>> = currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.getCustomerBookings(customer.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === Supabase Backend Sync Integration ===
    private val supabaseSyncManager = SupabaseSyncManager()
    val supabaseSyncStatus: StateFlow<SupabaseSyncStatus> = supabaseSyncManager.syncStatus

    // === Direct Supabase Invoices & Payments State ===
    private val _invoicesByBookingId = MutableStateFlow<Map<String, SupabaseInvoiceDto>>(emptyMap())
    val invoicesByBookingId: StateFlow<Map<String, SupabaseInvoiceDto>> = _invoicesByBookingId.asStateFlow()

    private val _isLoadingInvoices = MutableStateFlow(false)
    val isLoadingInvoices: StateFlow<Boolean> = _isLoadingInvoices.asStateFlow()

    private val _invoicesError = MutableStateFlow<String?>(null)
    val invoicesError: StateFlow<String?> = _invoicesError.asStateFlow()

    private val _selectedInvoiceState = MutableStateFlow<SelectedInvoiceState>(SelectedInvoiceState.Idle)
    val selectedInvoiceState: StateFlow<SelectedInvoiceState> = _selectedInvoiceState.asStateFlow()

    init {
        viewModelScope.launch {
            supabaseSyncManager.testConnection()
        }
        viewModelScope.launch {
            currentCustomer.collect { customer ->
                if (customer != null) {
                    loadInvoicesForCustomer(customer.id)
                } else {
                    _invoicesByBookingId.value = emptyMap()
                    _selectedInvoiceState.value = SelectedInvoiceState.Idle
                }
            }
        }
    }

    fun loadInvoicesForCustomer(customerId: String? = null) {
        val targetCustomerId = customerId ?: currentCustomer.value?.id ?: return
        viewModelScope.launch {
            _isLoadingInvoices.value = true
            _invoicesError.value = null
            val result = repository.getInvoicesForCustomer(targetCustomerId)
            _isLoadingInvoices.value = false
            if (result.isSuccess) {
                val list: List<SupabaseInvoiceDto> = result.getOrNull() ?: emptyList()
                _invoicesByBookingId.value = list.associateBy { it.bookingId }
            } else {
                _invoicesError.value = result.exceptionOrNull()?.message ?: "Failed to load invoices"
            }
        }
    }

    fun selectBookingForInvoice(booking: BookingEntity) {
        val customer = currentCustomer.value
        if (customer == null) {
            _selectedInvoiceState.value = SelectedInvoiceState.Error(booking, "Customer not authenticated")
            return
        }
        _selectedInvoiceState.value = SelectedInvoiceState.Loading(booking)
        viewModelScope.launch {
            // Load invoice using invoices.booking_id = booking.bookingId and customer_id = authenticated customer id
            val invoiceResult = repository.getInvoiceForBooking(booking.bookingId, customer.id)
            if (invoiceResult.isFailure) {
                _selectedInvoiceState.value = SelectedInvoiceState.Error(
                    booking = booking,
                    message = UiFormatters.sanitizeErrorMessage(
                        invoiceResult.exceptionOrNull()?.message,
                        "Unable to load invoice details. Please try again."
                    )
                )
                return@launch
            }

            val invoice: SupabaseInvoiceDto? = invoiceResult.getOrNull()
            if (invoice == null) {
                _selectedInvoiceState.value = SelectedInvoiceState.Success(
                    booking = booking,
                    invoice = null,
                    payments = emptyList()
                )
                return@launch
            }

            // Cache in booking map
            _invoicesByBookingId.value = _invoicesByBookingId.value + (booking.bookingId to invoice)

            // Load payments using payments.invoice_id = invoice.id and customer_id = authenticated customer id
            val paymentsResult = repository.getPaymentsForInvoice(invoice.id, customer.id)
            val payments: List<SupabasePaymentDto> = paymentsResult.getOrNull() ?: emptyList()

            _selectedInvoiceState.value = SelectedInvoiceState.Success(
                booking = booking,
                invoice = invoice,
                payments = payments
            )
        }
    }

    fun dismissSelectedInvoice() {
        _selectedInvoiceState.value = SelectedInvoiceState.Idle
    }

    fun retryLoadSelectedInvoice(booking: BookingEntity) {
        selectBookingForInvoice(booking)
    }

    suspend fun testSupabaseConnection(): Boolean {
        return supabaseSyncManager.testConnection()
    }

    suspend fun syncToSupabase(onComplete: (Boolean, String) -> Unit) {
        val currentBookings = allBookings.value
        val currentVehicles = vehicles.value
        val result = supabaseSyncManager.syncDataToCloud(currentBookings, currentVehicles)
        onComplete(result.first, result.second)
    }

    val notifications: StateFlow<List<NotificationEntity>> = currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.getNotifications(customer.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.getUnreadCount(customer.id) else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // === Booking Wizard State (7-step flow) ===
    private val _wizardStep = MutableStateFlow(1)
    val wizardStep = _wizardStep.asStateFlow()

    private val _selectedVehicle = MutableStateFlow<VehicleEntity?>(null)
    val selectedVehicle = _selectedVehicle.asStateFlow()

    private val _selectedService = MutableStateFlow("Periodic Maintenance & Diagnostic")
    val selectedService = _selectedService.asStateFlow()

    private val _problemDescription = MutableStateFlow("")
    val problemDescription = _problemDescription.asStateFlow()

    private val _selectedPhotos = MutableStateFlow<List<String>>(emptyList())
    val selectedPhotos = _selectedPhotos.asStateFlow()

    private val _selectedPhotoUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedPhotoUris = _selectedPhotoUris.asStateFlow()

    private val _photoUploadState = MutableStateFlow<PhotoUploadUiState>(PhotoUploadUiState.Idle)
    val photoUploadState = _photoUploadState.asStateFlow()

    private val _repairPhotos = MutableStateFlow<List<SupabaseRepairPhotoDto>>(emptyList())
    val repairPhotos = _repairPhotos.asStateFlow()

    private val _isLoadingRepairPhotos = MutableStateFlow(false)
    val isLoadingRepairPhotos = _isLoadingRepairPhotos.asStateFlow()

    private val _pickupLocation = MutableStateFlow("House 42, Sector J, DHA Phase 5, Lahore")
    val pickupLocation = _pickupLocation.asStateFlow()

    private val _pickupCity = MutableStateFlow("Lahore")
    val pickupCity = _pickupCity.asStateFlow()

    private val _preferredTime = MutableStateFlow("Morning (09:00 AM – 12:00 PM)")
    val preferredTime = _preferredTime.asStateFlow()

    private val _bookingDate = MutableStateFlow("Tomorrow, 12 Sept")
    val bookingDate = _bookingDate.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    // === Auth Functions ===
    fun setPhone(phone: String) {
        _authPhone.value = phone
    }

    fun setOtp(otp: String) {
        _authOtp.value = otp
    }

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating = _isAuthenticating.asStateFlow()

    fun sendOtp() {
        if (_authPhone.value.trim().length >= 9) {
            _isOtpSent.value = true
            _authError.value = null
        } else {
            _authError.value = "Please enter a valid Pakistani mobile number"
        }
    }

    fun verifyOtp(name: String? = null, city: String? = null, onSuccess: () -> Unit) {
        if (_authOtp.value.length >= 4) {
            _isAuthenticating.value = true
            _authError.value = null
            viewModelScope.launch {
                try {
                    val customerName = name?.trim()?.ifBlank { null } ?: "Valued Customer"
                    val customerCity = city?.trim()?.ifBlank { null } ?: "Lahore"
                    repository.loginWithOtp(_authPhone.value, name = customerName, city = customerCity)
                    _isOtpSent.value = false
                    _authOtp.value = ""
                    _isAuthenticating.value = false
                    onSuccess()
                } catch (e: Exception) {
                    _isAuthenticating.value = false
                    _authError.value = e.localizedMessage ?: "Verification error"
                }
            }
        } else {
            _authError.value = "Please enter the 4-digit code"
        }
    }

    fun setEmail(email: String) {
        _authEmail.value = email
    }

    fun setPassword(password: String) {
        _authPassword.value = password
    }

    fun loginWithEmail(
        email: String? = null,
        password: String? = null,
        onSuccess: () -> Unit
    ) {
        val targetEmail = (email ?: _authEmail.value).trim()
        val targetPass = (password ?: _authPassword.value).trim()
        if (targetEmail.isBlank() || !targetEmail.contains("@")) {
            _authError.value = "Please enter a valid email address."
            return
        }
        if (targetPass.isBlank()) {
            _authError.value = "Please enter your password."
            return
        }
        _isAuthenticating.value = true
        _authError.value = null
        _authSuccessMessage.value = null
        viewModelScope.launch {
            val result = repository.loginWithEmail(targetEmail, targetPass)
            _isAuthenticating.value = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                _authError.value = result.exceptionOrNull()?.message ?: "Invalid email or password."
            }
        }
    }

    fun signUpWithEmail(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onConfirmationRequired: () -> Unit
    ) {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim()
        val cleanPhone = phone.trim()
        val cleanPass = password.trim()
        val cleanConfirm = confirmPassword.trim()

        if (cleanName.isBlank()) {
            _authError.value = "Please enter your full name."
            return
        }
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            _authError.value = "Please enter a valid email address."
            return
        }
        if (cleanPass.length < 6) {
            _authError.value = "Password must be at least 6 characters long."
            return
        }
        if (cleanPass != cleanConfirm) {
            _authError.value = "Passwords do not match. Please re-enter."
            return
        }

        _isAuthenticating.value = true
        _authError.value = null
        _authSuccessMessage.value = null
        viewModelScope.launch {
            val result = repository.registerWithSupabase(
                email = cleanEmail,
                password = cleanPass,
                fullName = cleanName,
                phone = cleanPhone.ifBlank { null }
            )
            _isAuthenticating.value = false
            if (result.isSuccess) {
                when (val data = result.getOrNull()) {
                    is SignUpResult.Authenticated -> {
                        onSuccess()
                    }
                    is SignUpResult.ConfirmationRequired -> {
                        _authSuccessMessage.value = data.message
                        onSuccess()
                    }
                    null -> {
                        _authError.value = "Registration response was empty."
                    }
                }
            } else {
                _authError.value = result.exceptionOrNull()?.message ?: "Registration failed. Please try again."
            }
        }
    }

    fun sendPasswordReset(email: String, onComplete: (Boolean) -> Unit) {
        val targetEmail = email.trim()
        if (targetEmail.isBlank() || !targetEmail.contains("@")) {
            _resetPasswordError.value = "Please enter a valid email address."
            return
        }
        _isResettingPassword.value = true
        _resetPasswordError.value = null
        viewModelScope.launch {
            val result = repository.sendPasswordReset(targetEmail)
            _isResettingPassword.value = false
            if (result.isSuccess) {
                _isPasswordResetSent.value = true
                _authSuccessMessage.value = result.getOrNull()
                onComplete(true)
            } else {
                _resetPasswordError.value = result.exceptionOrNull()?.message ?: "Failed to send reset link."
                onComplete(false)
            }
        }
    }

    fun clearAuthMessages() {
        _authError.value = null
        _authSuccessMessage.value = null
        _resetPasswordError.value = null
        _isPasswordResetSent.value = false
    }

    fun isUserAuthenticated(): Boolean {
        return SupabaseSessionManager.isAuthenticated()
    }

    fun verifyEmailOtp(email: String, token: String, onSuccess: () -> Unit) {
        if (token.length < 4) {
            _authError.value = "Please enter the verification code sent to your email"
            return
        }
        _isAuthenticating.value = true
        _authError.value = null
        viewModelScope.launch {
            val result = repository.verifyOtp(email, token)
            _isAuthenticating.value = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                _authError.value = result.exceptionOrNull()?.message ?: "OTP verification failed."
            }
        }
    }

    fun saveCustomProfile(updatedCustomer: CustomerProfileEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.updateFullCustomerProfile(updatedCustomer)
            onComplete?.invoke()
        }
    }

    fun updateProfileFields(
        fullName: String,
        phone: String,
        onSuccess: (CustomerProfileEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updateProfileFields(fullName, phone)
            if (result.isSuccess) {
                onSuccess(result.getOrNull()!!)
            } else {
                onError(result.exceptionOrNull()?.message ?: "Failed to update profile.")
            }
        }
    }

    fun updateUserPassword(
        newPassword: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updateUserPassword(newPassword)
            if (result.isSuccess) {
                onSuccess(result.getOrNull() ?: "Password updated successfully.")
            } else {
                onError(result.exceptionOrNull()?.message ?: "Failed to update password.")
            }
        }
    }

    fun updateProfile(name: String, email: String, city: String) {
        viewModelScope.launch {
            repository.updateProfile(name, email, city)
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onLoggedOut()
        }
    }

    // === Booking Wizard Functions ===
    fun setWizardStep(step: Int) {
        _wizardStep.value = step.coerceIn(1, 7)
    }

    fun selectVehicle(vehicle: VehicleEntity) {
        _selectedVehicle.value = vehicle
    }

    fun selectService(service: String) {
        _selectedService.value = service
    }

    fun setProblemDescription(desc: String) {
        _problemDescription.value = desc
    }

    fun togglePhoto(photoTag: String) {
        val current = _selectedPhotos.value.toMutableList()
        if (current.contains(photoTag)) {
            current.remove(photoTag)
        } else {
            current.add(photoTag)
        }
        _selectedPhotos.value = current
    }

    fun addPhotoUri(uri: Uri) {
        val current = _selectedPhotoUris.value.toMutableList()
        if (!current.contains(uri)) {
            current.add(uri)
            _selectedPhotoUris.value = current
        }
    }

    fun removePhotoUri(uri: Uri) {
        val current = _selectedPhotoUris.value.toMutableList()
        current.remove(uri)
        _selectedPhotoUris.value = current
    }

    fun clearPhotoUris() {
        _selectedPhotoUris.value = emptyList()
        _photoUploadState.value = PhotoUploadUiState.Idle
    }

    fun setPickupLocation(location: String, city: String) {
        _pickupLocation.value = location
        _pickupCity.value = city
    }

    fun setPreferredSchedule(date: String, timeSlot: String) {
        _bookingDate.value = date
        _preferredTime.value = timeSlot
    }

    fun resetWizard() {
        _wizardStep.value = 1
        _problemDescription.value = ""
        _selectedPhotos.value = emptyList()
        _selectedPhotoUris.value = emptyList()
        _photoUploadState.value = PhotoUploadUiState.Idle
    }

    fun loadRepairPhotos(repairJobId: String? = null) {
        viewModelScope.launch {
            _isLoadingRepairPhotos.value = true
            try {
                val resp = SupabaseClient.api.getRepairPhotos(repairJobId)
                if (resp.isSuccessful) {
                    _repairPhotos.value = resp.body() ?: emptyList()
                } else {
                    Log.w("TorqfixVM", "getRepairPhotos error: ${resp.code()} ${resp.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("TorqfixVM", "getRepairPhotos exception", e)
            } finally {
                _isLoadingRepairPhotos.value = false
            }
        }
    }

    fun submitBooking(
        context: Context? = null,
        onSuccess: (bookingId: String) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val customer = currentCustomer.value ?: return
        val vehicle = _selectedVehicle.value ?: vehicles.value.firstOrNull() ?: return
        val desc = if (_problemDescription.value.isBlank()) "Standard diagnostic and service checklist." else _problemDescription.value

        _isSubmitting.value = true
        viewModelScope.launch {
            // 1. Create booking record with empty photos (no fake placeholders)
            val bookingId = repository.createBooking(
                customerId = customer.id,
                vehicle = vehicle,
                service = _selectedService.value,
                problemDescription = desc,
                photosJson = "",
                pickupLocation = _pickupLocation.value,
                pickupCity = _pickupCity.value,
                preferredTime = _preferredTime.value,
                bookingDate = _bookingDate.value,
                costEstimate = calculateEstimateForService(_selectedService.value)
            )

            // 2. Binary photo upload to Supabase Storage if customer selected device photos
            val urisToUpload = _selectedPhotoUris.value
            var hasUploadError = false
            var failureMessage = ""
            val uploadedUrls = mutableListOf<String>()

            if (context != null && urisToUpload.isNotEmpty()) {
                _photoUploadState.value = PhotoUploadUiState.Uploading(0, urisToUpload.size)
                for ((index, uri) in urisToUpload.withIndex()) {
                    _photoUploadState.value = PhotoUploadUiState.Uploading(index + 1, urisToUpload.size)
                    val uploadResult = SupabaseStorageService.uploadBookingPhoto(
                        context = context,
                        imageUri = uri,
                        bookingId = bookingId,
                        customerId = customer.id,
                        photoType = "CUSTOMER_INSPECTION"
                    )
                    when (uploadResult) {
                        is SupabaseStorageService.StorageUploadResult.Success -> {
                            uploadedUrls.add(uploadResult.publicUrl)
                        }
                        is SupabaseStorageService.StorageUploadResult.Failure -> {
                            hasUploadError = true
                            failureMessage = uploadResult.message
                            _photoUploadState.value = PhotoUploadUiState.Error(uploadResult.errorType, uploadResult.message)
                            Log.e("TorqfixVM", "Storage upload failed: ${uploadResult.message}")
                            break
                        }
                    }
                }
            }

            _isSubmitting.value = false
            if (!hasUploadError) {
                if (uploadedUrls.isNotEmpty()) {
                    _photoUploadState.value = PhotoUploadUiState.Success(uploadedUrls)
                }
                resetWizard()
                // Auto-sync booking to Supabase PostgreSQL cloud in background
                viewModelScope.launch {
                    val b = repository.getCustomerBookings(customer.id).firstOrNull() ?: emptyList()
                    val v = repository.getVehicles(customer.id).firstOrNull() ?: emptyList()
                    supabaseSyncManager.syncDataToCloud(b, v)
                }
                onSuccess(bookingId)
            } else {
                onError(failureMessage)
            }
        }
    }

    private fun calculateEstimateForService(service: String): Int {
        return when {
            service.contains("Brake") -> 14500
            service.contains("Engine") -> 32000
            service.contains("AC") -> 16000
            service.contains("Detailing") -> 25000
            service.contains("Suspension") -> 22000
            service.contains("Electrical") -> 12500
            else -> 18500
        }
    }

    // === Shared Backend Simulation Actions ===
    fun advanceStatusManually(bookingId: String) {
        viewModelScope.launch {
            repository.advanceBookingStep(bookingId)
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun addNewVehicle(make: String, model: String, year: Int, plate: String, color: String, trans: String, fuel: String) {
        val customer = currentCustomer.value ?: return
        viewModelScope.launch {
            repository.addVehicle(customer.id, make, model, year, plate, color, trans, fuel)
        }
    }

    fun deleteVehicle(vehicleId: String) {
        val customer = currentCustomer.value ?: return
        viewModelScope.launch {
            repository.deleteVehicle(vehicleId, customer.id)
        }
    }

    fun addAddress(title: String, address: String, city: String, landmark: String) {
        val customer = currentCustomer.value ?: return
        viewModelScope.launch {
            repository.addSavedAddress(customer.id, title, address, city, landmark)
        }
    }
}

class TorqfixViewModelFactory(
    private val repository: TorqfixRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TorqfixViewModel::class.java)) {
            return TorqfixViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.data.remote.supabase

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.UUID

object SupabaseStorageService {
    private const val TAG = "SupabaseStorage"

    const val BUCKET_BOOKING_PHOTOS = "booking-photos"
    const val BUCKET_REPAIR_PHOTOS = "repair-photos"

    sealed class StorageUploadResult {
        data class Success(
            val storagePath: String,
            val publicUrl: String,
            val bucket: String,
            val photoRecordId: String
        ) : StorageUploadResult()

        data class Failure(
            val errorType: StorageErrorType,
            val message: String,
            val httpStatusCode: Int? = null,
            val rawErrorBody: String? = null
        ) : StorageUploadResult()
    }

    enum class StorageErrorType {
        SELECTION_CANCELLED,
        AUTH_MISSING,
        INVALID_IMAGE,
        BUCKET_NOT_FOUND,
        PERMISSION_DENIED_RLS,
        NETWORK_ERROR,
        DATABASE_INSERT_FAILURE,
        UNKNOWN
    }

    /**
     * Inspects whether a specific Supabase storage bucket exists.
     * Does NOT create or alter the bucket.
     */
    suspend fun checkBucketExists(bucketName: String): Boolean = withContext(Dispatchers.IO) {
        val checkUrl = "${SupabaseConfig.url.trimEnd('/')}/storage/v1/bucket/$bucketName"
        val request = Request.Builder()
            .url(checkUrl)
            .get()
            .build()

        try {
            val response = SupabaseClient.okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check bucket existence for $bucketName: ${e.message}")
            false
        }
    }

    /**
     * Uploads a real binary image to the Supabase Storage bucket and inserts the corresponding
     * metadata row into the `booking_photos` table.
     */
    suspend fun uploadBookingPhoto(
        context: Context,
        imageUri: Uri,
        bookingId: String,
        customerId: String,
        photoType: String = "CUSTOMER_INSPECTION"
    ): StorageUploadResult = withContext(Dispatchers.IO) {
        // 1. Verify authenticated user
        if (customerId.isBlank()) {
            return@withContext StorageUploadResult.Failure(
                errorType = StorageErrorType.AUTH_MISSING,
                message = "Authentication session missing. Please sign in to upload photos."
            )
        }

        // 2. Read binary file data from ContentResolver
        val bytes: ByteArray
        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                return@withContext StorageUploadResult.Failure(
                    errorType = StorageErrorType.INVALID_IMAGE,
                    message = "Could not open selected image input stream on device."
                )
            }
            bytes = inputStream.use { it.readBytes() }
            if (bytes.isEmpty()) {
                return@withContext StorageUploadResult.Failure(
                    errorType = StorageErrorType.INVALID_IMAGE,
                    message = "Selected image is empty (0 bytes) or corrupted."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed reading image binary bytes", e)
            return@withContext StorageUploadResult.Failure(
                errorType = StorageErrorType.INVALID_IMAGE,
                message = "Error reading selected image: ${e.message}"
            )
        }

        val extension = when {
            mimeType.contains("png", ignoreCase = true) -> "png"
            mimeType.contains("webp", ignoreCase = true) -> "webp"
            mimeType.contains("heic", ignoreCase = true) -> "heic"
            else -> "jpg"
        }

        // 3. Safe unique storage path: customer/{authenticated_user_id}/booking/{booking_id}/{unique_filename}
        val uniqueFilename = "inspection_${UUID.randomUUID()}.$extension"
        val storagePath = "customer/$customerId/booking/$bookingId/$uniqueFilename"
        val bucket = BUCKET_BOOKING_PHOTOS

        val uploadUrl = "${SupabaseConfig.url.trimEnd('/')}/storage/v1/object/$bucket/$storagePath"

        val mediaType = mimeType.toMediaTypeOrNull()
        val requestBody = bytes.toRequestBody(mediaType)

        val uploadRequest = Request.Builder()
            .url(uploadUrl)
            .header("Content-Type", mimeType)
            .header("x-upsert", "true")
            .post(requestBody)
            .build()

        try {
            val response = SupabaseClient.okHttpClient.newCall(uploadRequest).execute()
            val responseCode = response.code
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Storage binary upload failed: HTTP $responseCode - $responseBody")
                return@withContext when {
                    responseCode == 404 || responseBody.contains("Bucket not found", ignoreCase = true) || responseCode == 400 -> {
                        StorageUploadResult.Failure(
                            errorType = StorageErrorType.BUCKET_NOT_FOUND,
                            message = "Supabase Storage bucket '$bucket' does not exist. Please configure it in Supabase Dashboard.",
                            httpStatusCode = responseCode,
                            rawErrorBody = responseBody
                        )
                    }
                    responseCode == 401 || responseCode == 403 || responseBody.contains("row-level security", ignoreCase = true) -> {
                        StorageUploadResult.Failure(
                            errorType = StorageErrorType.PERMISSION_DENIED_RLS,
                            message = "Access denied by Supabase Storage RLS policy. Authenticated customer lacks upload permission.",
                            httpStatusCode = responseCode,
                            rawErrorBody = responseBody
                        )
                    }
                    else -> {
                        StorageUploadResult.Failure(
                            errorType = StorageErrorType.UNKNOWN,
                            message = "Storage upload failed (HTTP $responseCode): $responseBody",
                            httpStatusCode = responseCode,
                            rawErrorBody = responseBody
                        )
                    }
                }
            }

            // 4. Successful upload -> Construct public URL
            val publicUrl = "${SupabaseConfig.url.trimEnd('/')}/storage/v1/object/public/$bucket/$storagePath"

            // 5. Insert corresponding booking_photos row
            val photoRecordId = UUID.randomUUID().toString()
            val bookingPhotoDto = SupabaseBookingPhotoDto(
                id = photoRecordId,
                bookingId = bookingId,
                uploadedBy = customerId,
                photoUrl = publicUrl,
                photoType = photoType
            )

            val dbResponse = SupabaseClient.api.addBookingPhoto(bookingPhotoDto)
            if (!dbResponse.isSuccessful) {
                val dbError = dbResponse.errorBody()?.string() ?: "Database insertion failed"
                Log.e(TAG, "Storage upload succeeded, but database metadata insertion failed: $dbError")
                return@withContext StorageUploadResult.Failure(
                    errorType = StorageErrorType.DATABASE_INSERT_FAILURE,
                    message = "Photo uploaded to storage, but registering record in database failed: $dbError",
                    httpStatusCode = dbResponse.code(),
                    rawErrorBody = dbError
                )
            }

            Log.i(TAG, "Photo uploaded & registered successfully! Path: $storagePath, ID: $photoRecordId")
            StorageUploadResult.Success(
                storagePath = storagePath,
                publicUrl = publicUrl,
                bucket = bucket,
                photoRecordId = photoRecordId
            )
        } catch (e: java.io.IOException) {
            Log.e(TAG, "Network failure during storage upload", e)
            StorageUploadResult.Failure(
                errorType = StorageErrorType.NETWORK_ERROR,
                message = "Network connection failed during photo upload: ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during storage upload", e)
            StorageUploadResult.Failure(
                errorType = StorageErrorType.UNKNOWN,
                message = "Unexpected upload error: ${e.message}"
            )
        }
    }
}

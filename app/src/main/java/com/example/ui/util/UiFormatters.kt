package com.example.ui.util

/**
 * UI formatting and error sanitization utilities for the customer-facing TORQFIX application.
 * Ensures internal technical details, database names, UUIDs, and API responses are never exposed.
 */
object UiFormatters {

    /**
     * Converts an internal booking ID / UUID into a customer-friendly display number.
     * Example: "b-1729384920" -> "TQX-84920", "6da76611-9871-47ad-82f4-42dfa5460242" -> "TQX-460242"
     */
    fun formatDisplayBookingNumber(bookingId: String): String {
        if (bookingId.isBlank()) return "TQX-1001"
        val clean = bookingId.removePrefix("b-").replace("-", "").trim()
        val suffix = if (clean.length >= 6) {
            clean.takeLast(6).uppercase()
        } else {
            clean.uppercase()
        }
        return "TQX-$suffix"
    }

    /**
     * Converts a customer internal UUID into a customer-facing membership reference code.
     * Example: "6da76611-9871-47ad-82f4-42dfa5460242" -> "TF-460242"
     */
    fun formatDisplayMembershipId(customerId: String): String {
        if (customerId.isBlank()) return "TF-MEMBER"
        val clean = customerId.replace("-", "").trim()
        val suffix = if (clean.length >= 6) {
            clean.takeLast(6).uppercase()
        } else {
            clean.uppercase()
        }
        return "TF-$suffix"
    }

    /**
     * Sanitizes any raw exception or backend error message before displaying to the customer.
     * Strips SQL errors, PGRST codes, Supabase URLs/tokens, and JSON stack traces.
     */
    fun sanitizeErrorMessage(
        rawMessage: String?,
        defaultMessage: String = "Something went wrong. Please try again."
    ): String {
        if (rawMessage.isNullOrBlank()) return defaultMessage
        val trimmed = rawMessage.trim()
        val lower = trimmed.lowercase()

        return when {
            // Credential errors
            lower.contains("invalid login") || lower.contains("invalid_grant") || lower.contains("invalid credentials") || lower.contains("invalid email or password") ->
                "Invalid email or password. Please try again."

            // Existing account
            lower.contains("already registered") || lower.contains("user_already_exists") || lower.contains("email address already in use") ->
                "An account with this email already exists. Please sign in."

            // Email confirmation required
            lower.contains("email_not_confirmed") || lower.contains("email not confirmed") ->
                "Please check your inbox to confirm your email before signing in."

            // Network and host connectivity
            lower.contains("network") || lower.contains("unable to resolve host") || lower.contains("timeout") ||
                    lower.contains("connection") || lower.contains("socket") || lower.contains("connectexception") ||
                    lower.contains("failed to connect") || lower.contains("no address associated") ->
                "Unable to connect. Please check your internet connection and try again."

            // Database, RLS, PGRST, SQL, Table or Internal errors
            lower.contains("pgrst") || lower.contains("postgres") || lower.contains("database") ||
                    lower.contains("supabase") || lower.contains("relation") || lower.contains("column") ||
                    lower.contains("jwt") || lower.contains("bearer") || lower.contains("foreign key") ||
                    lower.contains("duplicate key") || lower.contains("violates") || lower.contains("null value in column") ||
                    lower.contains("http 40") || lower.contains("http 50") || lower.contains("unauthorized") ||
                    lower.contains("forbidden") || lower.contains("permission denied") || lower.contains("internal server") ||
                    lower.contains("bad request") || lower.contains("storage") || lower.contains("bucket") ->
                defaultMessage

            // Raw JSON strings
            (trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]")) ->
                defaultMessage

            // Length guard - overly long technical messages
            trimmed.length > 120 ->
                defaultMessage

            else -> trimmed
        }
    }
}

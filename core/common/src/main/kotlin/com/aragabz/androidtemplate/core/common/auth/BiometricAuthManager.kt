package com.aragabz.androidtemplate.core.common.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages biometric authentication using BiometricPrompt API.
 *
 * Provides:
 * - Biometric availability checking
 * - Biometric authentication flow with callbacks
 * - Support for fingerprint, face, and iris authentication
 */
@Singleton
class BiometricAuthManager
    @Inject
    constructor() {
        private val _authResults = Channel<BiometricAuthResult>(Channel.BUFFERED)
        val authResults: Flow<BiometricAuthResult> = _authResults.receiveAsFlow()

        /**
         * Check if biometric authentication is available on this device.
         */
        fun canAuthenticate(activity: FragmentActivity): BiometricAvailability {
            val biometricManager = BiometricManager.from(activity)
            return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS ->
                    BiometricAvailability.Available

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    BiometricAvailability.NoHardware

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    BiometricAvailability.HardwareUnavailable

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    BiometricAvailability.NoneEnrolled

                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                    BiometricAvailability.SecurityUpdateRequired

                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                    BiometricAvailability.Unsupported

                BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                    BiometricAvailability.Unknown

                else -> BiometricAvailability.Unknown
            }
        }

        /**
         * Authenticate using biometric prompt.
         *
         * @param activity The FragmentActivity to display the prompt
         * @param title Title shown in the biometric prompt
         * @param subtitle Optional subtitle shown in the prompt
         * @param negativeButtonText Text for the negative button (e.g., "Cancel")
         */
        fun authenticate(
            activity: FragmentActivity,
            title: String,
            subtitle: String? = null,
            negativeButtonText: String,
        ) {
            val executor = ContextCompat.getMainExecutor(activity)

            val callback =
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        _authResults.trySend(BiometricAuthResult.Success)
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        _authResults.trySend(
                            BiometricAuthResult.Error(
                                errorCode = errorCode,
                                errorMessage = errString.toString(),
                            ),
                        )
                    }

                    override fun onAuthenticationFailed() {
                        _authResults.trySend(BiometricAuthResult.Failed)
                    }
                }

            val biometricPrompt = BiometricPrompt(activity, executor, callback)

            val promptInfo =
                BiometricPrompt.PromptInfo
                    .Builder()
                    .setTitle(title)
                    .apply {
                        subtitle?.let { setSubtitle(it) }
                    }.setNegativeButtonText(negativeButtonText)
                    .setAllowedAuthenticators(BIOMETRIC_STRONG)
                    .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }

/**
 * Result of a biometric authentication attempt.
 */
sealed interface BiometricAuthResult {
    /** Authentication succeeded. */
    data object Success : BiometricAuthResult

    /** Authentication failed (biometric not recognized). */
    data object Failed : BiometricAuthResult

    /** Authentication error (hardware issue, user cancelled, etc.). */
    data class Error(
        val errorCode: Int,
        val errorMessage: String,
    ) : BiometricAuthResult
}

/**
 * Availability status of biometric authentication.
 */
sealed interface BiometricAvailability {
    /** Biometric authentication is available and ready to use. */
    data object Available : BiometricAvailability

    /** No biometric hardware detected on device. */
    data object NoHardware : BiometricAvailability

    /** Biometric hardware is present but currently unavailable. */
    data object HardwareUnavailable : BiometricAvailability

    /** No biometric credentials enrolled (user needs to set up biometrics). */
    data object NoneEnrolled : BiometricAvailability

    /** A security update is required before biometrics can be used. */
    data object SecurityUpdateRequired : BiometricAvailability

    /** Biometric authentication is not supported on this device. */
    data object Unsupported : BiometricAvailability

    /** Biometric status is unknown. */
    data object Unknown : BiometricAvailability

    /** Check if biometric authentication can be used. */
    fun isAvailable(): Boolean = this is Available
}

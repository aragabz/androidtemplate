package com.aragabz.androidtemplate.feature.auth.presentation.register

/**
 * Sealed class representing register screen events.
 */
public sealed class RegisterEvent {
    /**
     * Name input changed.
     */
    public data class NameChanged(val name: String) : RegisterEvent()
    
    /**
     * Email input changed.
     */
    public data class EmailChanged(val email: String) : RegisterEvent()
    
    /**
     * Password input changed.
     */
    public data class PasswordChanged(val password: String) : RegisterEvent()
    
    /**
     * Confirm password input changed.
     */
    public data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    
    /**
     * Register button clicked.
     */
    public data object RegisterClicked : RegisterEvent()
    
    /**
     * Sign in link clicked.
     */
    public data object SignInClicked : RegisterEvent()
    
    /**
     * Error dismissed.
     */
    public data object DismissError : RegisterEvent()
}

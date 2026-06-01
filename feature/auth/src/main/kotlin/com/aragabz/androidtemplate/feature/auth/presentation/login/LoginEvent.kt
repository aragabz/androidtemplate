package com.aragabz.androidtemplate.feature.auth.presentation.login

/**
 * Sealed class representing login screen events.
 */
public sealed class LoginEvent {
    /**
     * Email input changed.
     */
    public data class EmailChanged(val email: String) : LoginEvent()
    
    /**
     * Password input changed.
     */
    public data class PasswordChanged(val password: String) : LoginEvent()
    
    /**
     * Login button clicked.
     */
    public data object LoginClicked : LoginEvent()
    
    /**
     * Sign up link clicked.
     */
    public data object SignUpClicked : LoginEvent()
    
    /**
     * Error dismissed.
     */
    public data object DismissError : LoginEvent()
}

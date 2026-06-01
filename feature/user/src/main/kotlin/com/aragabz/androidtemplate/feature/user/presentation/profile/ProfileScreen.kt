package com.aragabz.androidtemplate.feature.user.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.core.ui.components.OfflineBanner
import com.aragabz.androidtemplate.core.ui.screens.ErrorScreen
import com.aragabz.androidtemplate.core.ui.screens.LoadingScreen

/**
 * Profile screen composable.
 * 
 * @param onEditProfile Callback when edit profile is clicked
 * @param onLogout Callback when logout is successful
 * @param viewModel The profile view model
 */
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(ProfileEvent.OnDismissError)
        }
    }
    
    // Navigate to log in on logout success
    LaunchedEffect(uiState.profile) {
        if (uiState.profile == null && !uiState.isLoading && !uiState.error.isNullOrEmpty().not()) {
            onLogout()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.profile == null -> {
                    LoadingScreen()
                }
                
                uiState.error != null && uiState.profile == null -> {
                    ErrorScreen(
                        message = uiState.error ?: "An error occurred",
                        onRetry = { viewModel.onEvent(ProfileEvent.OnRetry) }
                    )
                }
                
                uiState.profile != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Offline banner
                        if (uiState.isOffline) {
                            com.aragabz.androidtemplate.core.ui.components.OfflineBanner(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Profile content
                        ProfileContent(
                            profile = uiState.profile!!,
                            isUpdating = uiState.isUpdating,
                            onEditProfile = onEditProfile,
                            onLogout = { viewModel.onEvent(ProfileEvent.OnLogoutClicked) },
                            modifier = Modifier.padding(spacing.medium)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Profile content composable showing user information.
 */
@Composable
private fun ProfileContent(
    profile: com.aragabz.androidtemplate.feature.user.domain.model.UserProfile,
    isUpdating: Boolean,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(spacing.large))
        
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Avatar",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(spacing.medium))
        
        // Name
        Text(
            text = profile.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(spacing.extraLarge))
        
        // Profile details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(spacing.medium)
            ) {
                // Email
                ProfileInfoRow(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = profile.email
                )
                
                if (profile.bio != null) {
                    Spacer(modifier = Modifier.height(spacing.medium))
                    
                    // Bio
                    Column {
                        Text(
                            text = "Bio",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(spacing.extraSmall))
                        Text(
                            text = profile.bio,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.extraLarge))
        
        // Edit profile button
        AppButton(
            text = "Edit Profile",
            onClick = onEditProfile,
            variant = AppButtonVariant.PRIMARY,
            leadingIcon = Icons.Default.Edit,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(spacing.medium))
        
        // Logout button
        AppButton(
            text = "Logout",
            onClick = onLogout,
            variant = AppButtonVariant.DESTRUCTIVE,
            leadingIcon = Icons.AutoMirrored.Filled.Logout,
            isLoading = isUpdating,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(spacing.large))
    }
}

/**
 * Profile information row showing an icon, label, and value.
 */
@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

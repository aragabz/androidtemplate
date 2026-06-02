package com.aragabz.androidtemplate.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.core.navigation.Route

/**
 * Settings screen displaying active profile information, account list, account switcher, and preferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    // Redirect to log in if user logs out (only after session data is loaded)
    LaunchedEffect(uiState.isSessionLoaded, uiState.activeUserId) {
        if (uiState.isSessionLoaded && uiState.activeUserId == null) {
            navController.navigate(Route.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = spacing.medium)
                .verticalScroll(scrollState)
        ) {
            // Active Account Card
            uiState.activeAccount?.let { account ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Column {
                            Text(
                                text = account.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = account.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            account.bio?.let { bio ->
                                Text(
                                    text = bio,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.large))

            // Accounts Switcher Section
            Text(
                text = "Accounts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(spacing.small))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(vertical = spacing.small)) {
                    uiState.accounts.forEachIndexed { index, account ->
                        val isActive = account.id == uiState.activeUserId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!isActive) viewModel.switchAccount(account.id)
                                }
                                .padding(horizontal = spacing.medium, vertical = spacing.medium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isActive) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.outline
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(spacing.medium))
                                Column {
                                    Text(
                                        text = account.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    Text(
                                        text = account.email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (isActive) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (index < uiState.accounts.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = spacing.medium),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )
                        }
                    }

                    if (uiState.accounts.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = spacing.medium),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }

                    // Add Account row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Route.Register)
                            }
                            .padding(horizontal = spacing.medium, vertical = spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Text(
                            text = "Add Account",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.large))

            // Preferences Section
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(spacing.small))

            // Theme Dropdown Selector
            var themeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = themeExpanded,
                onExpandedChange = { themeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.currentTheme.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("App Theme") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = themeExpanded,
                    onDismissRequest = { themeExpanded = false }
                ) {
                    AppTheme.values().forEach { theme ->
                        DropdownMenuItem(
                            text = { Text(theme.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                viewModel.updateTheme(theme)
                                themeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.extraLarge))

            // Logout Button
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text("Logout current account")
            }

            Spacer(modifier = Modifier.height(spacing.large))
        }
    }
}

package com.aragabz.androidtemplate.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.database.dao.AccountDao
import com.aragabz.androidtemplate.core.database.model.AccountEntity
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing application settings, app themes, languages, and switching active local accounts.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    data class SettingsUiState(
        val isSessionLoaded: Boolean = false,
        val activeUserId: String? = null,
        val activeAccount: AccountEntity? = null,
        val accounts: List<AccountEntity> = emptyList(),
        val currentTheme: AppTheme = AppTheme.SYSTEM,
        val currentLanguage: String = "en"
    )

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Observe accounts in local DB
        viewModelScope.launch {
            accountDao.getAccountsFlow().collect { accounts ->
                _uiState.update { it.copy(accounts = accounts) }
                updateActiveAccount(accounts, _uiState.value.activeUserId)
            }
        }
        // Observe preferences (user ID, theme, language)
        viewModelScope.launch {
            preferencesRepository.userPreferences.collect { prefs ->
                _uiState.update {
                    it.copy(
                        isSessionLoaded = true,
                        activeUserId = prefs.userId,
                        currentTheme = prefs.theme,
                        currentLanguage = prefs.language
                    )
                }
                updateActiveAccount(_uiState.value.accounts, prefs.userId)
            }
        }
    }

    private fun updateActiveAccount(accounts: List<AccountEntity>, activeUserId: String?) {
        val active = accounts.find { it.id == activeUserId }
        _uiState.update { it.copy(activeAccount = active) }
    }

    fun switchAccount(accountId: String) {
        viewModelScope.launch {
            preferencesRepository.saveUserId(accountId)
            preferencesRepository.saveAuthToken("offline_token_$accountId")
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.updateTheme(theme)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            preferencesRepository.updateLanguage(language)
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.clearSession()
        }
    }
}

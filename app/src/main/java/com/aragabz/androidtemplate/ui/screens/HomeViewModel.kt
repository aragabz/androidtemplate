package com.aragabz.androidtemplate.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 * Demonstrates lifecycle-viewmodel-navigation3 integration.
 */
class HomeViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(
                title = "Home Screen",
                items = listOf("Item 1", "Item 2", "Item 3")
            )
        }
    }
    
    fun onItemClick(item: String) {
        _uiState.value = _uiState.value.copy(selectedItem = item)
    }
}

data class HomeUiState(
    val title: String = "Home Screen",
    val items: List<String> = emptyList(),
    val selectedItem: String? = null
)

package com.aragabz.androidtemplate.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

/**
 * ViewModel for the Details screen.
 * Demonstrates lifecycle-viewmodel-navigation3 integration with kotlinx-serialization-core.
 */
class DetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    fun loadDetails(id: String) {
        viewModelScope.launch {
            // Simulate loading data
            _uiState.value =
                DetailsUiState(
                    id = id,
                    title = "Details for $id",
                    description = "This is a detailed view for item: $id",
                    metadata =
                        DetailMetadata(
                            createdAt = System.currentTimeMillis(),
                            author = "System",
                        ),
                )
        }
    }
}

data class DetailsUiState(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val metadata: DetailMetadata? = null,
)

@Serializable
data class DetailMetadata(
    val createdAt: Long,
    val author: String,
)

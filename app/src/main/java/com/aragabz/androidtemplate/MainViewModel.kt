package com.aragabz.androidtemplate

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Main view model for app-level state.
 */
@HiltViewModel
class MainViewModel
    @Inject
    constructor() : ViewModel()

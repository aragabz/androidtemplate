package com.aragabz.androidtemplate.core.flags

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.featureFlagDataStore: DataStore<Preferences> by preferencesDataStore(name = "feature_flags")

/**
 * DataStore-backed implementation of [FeatureFlagStore].
 *
 * Persists feature flag overrides using AndroidX DataStore.
 * Each feature flag is stored as a boolean preference with the feature's key as the preference key.
 */
@Singleton
internal class FeatureFlagStoreImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : FeatureFlagStore {
        override fun getOverride(feature: Feature): Flow<Boolean?> =
            context.featureFlagDataStore.data.map { preferences ->
                preferences[booleanPreferencesKey(feature.key)]
            }

        override suspend fun setOverride(
            feature: Feature,
            enabled: Boolean,
        ) {
            context.featureFlagDataStore.edit { preferences ->
                preferences[booleanPreferencesKey(feature.key)] = enabled
            }
        }

        override suspend fun clearOverride(feature: Feature) {
            context.featureFlagDataStore.edit { preferences ->
                preferences.remove(booleanPreferencesKey(feature.key))
            }
        }

        override suspend fun clearAllOverrides() {
            context.featureFlagDataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

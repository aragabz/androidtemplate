package com.aragabz.androidtemplate.feature.profile.data.mapper

import com.aragabz.androidtemplate.feature.profile.data.api.ProfileDto
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile

/**
 * Maps ProfileDto to UserProfile domain model.
 */
fun ProfileDto.toDomain(): UserProfile =
    UserProfile(
        userId = userId,
        displayName = displayName,
        email = email,
    )

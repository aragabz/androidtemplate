package com.aragabz.androidtemplate.feature.user.data.mapper

import com.aragabz.androidtemplate.feature.user.data.remote.dto.UserProfileDto
import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile

/**
 * Mapper for converting between DTOs and domain models.
 */
public object UserMapper {
    
    /**
     * Converts [UserProfileDto] to [UserProfile] domain model.
     */
    public fun UserProfileDto.toDomain(): UserProfile = UserProfile(
        id = id,
        name = name,
        email = email,
        avatar = avatar,
        bio = bio
    )
}

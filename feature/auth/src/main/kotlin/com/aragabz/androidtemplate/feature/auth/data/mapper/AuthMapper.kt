package com.aragabz.androidtemplate.feature.auth.data.mapper

import com.aragabz.androidtemplate.feature.auth.data.remote.dto.AuthResponse
import com.aragabz.androidtemplate.feature.auth.data.remote.dto.UserDto
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthToken
import com.aragabz.androidtemplate.feature.auth.domain.model.User

/**
 * Mapper object for converting between DTOs and domain models.
 */
public object AuthMapper {
    
    /**
     * Converts [AuthResponse] DTO to [AuthToken] domain model.
     */
    public fun AuthResponse.toDomain(): AuthToken = AuthToken(
        token = token,
        user = user.toDomain()
    )
    
    /**
     * Converts [UserDto] to [User] domain model.
     */
    public fun UserDto.toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        avatar = avatar
    )
}

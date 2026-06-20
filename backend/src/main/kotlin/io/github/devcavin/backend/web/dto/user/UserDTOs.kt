package io.github.devcavin.backend.web.dto.user

import io.github.devcavin.backend.domain.model.User
import jakarta.annotation.Nonnull
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class CreateUserRequest(
    @field:NotBlank
    val name: String,

    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String,

    @field:NotBlank
    val roleName: String,

    @field:NotBlank
    val siteId: UUID
)

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val roleName: String,
    val siteId: UUID,
    val isActive: Boolean
)

fun User.toResponse() = UserResponse(
    id = id!!,
    name = name,
    email = email,
    roleName = role.name,
    siteId = site.id!!,
    isActive = isActive
)

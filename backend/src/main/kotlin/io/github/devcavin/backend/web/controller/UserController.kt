package io.github.devcavin.backend.web.controller

import io.github.devcavin.backend.domain.model.User
import io.github.devcavin.backend.service.UserService
import io.github.devcavin.backend.web.dto.user.CreateUserRequest
import io.github.devcavin.backend.web.dto.user.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    fun createUser(
        @AuthenticationPrincipal requestedBy: User,
        @Valid @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val createdUser = userService.createUser(request, requestedBy)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUser)
    }
}
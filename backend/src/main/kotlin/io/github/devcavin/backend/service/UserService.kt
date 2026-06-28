package io.github.devcavin.backend.service

import io.github.devcavin.backend.common.exception.ConflictException
import io.github.devcavin.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.backend.domain.model.Role
import io.github.devcavin.backend.domain.model.Site
import io.github.devcavin.backend.domain.model.User
import io.github.devcavin.backend.domain.repository.RoleRepository
import io.github.devcavin.backend.domain.repository.SiteRepository
import io.github.devcavin.backend.domain.repository.UserRepository
import io.github.devcavin.backend.web.dto.user.CreateUserRequest
import io.github.devcavin.backend.web.dto.user.UserResponse
import io.github.devcavin.backend.web.dto.user.toResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val siteRepository: SiteRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun createUser(request: CreateUserRequest, requestedBy: User): UserResponse {
        if (userRepository.existsByEmail(request.email)) throw ConflictException("User already exists")

        val targetRole = roleRepository.findByName(request.roleName) ?: throw ResourceNotFoundException("Role", request.roleName)

        enforceCreationRules(requestedBy, targetRole.name, request.siteId)

        val site = siteRepository.findById(request.siteId).orElseThrow { ResourceNotFoundException("Site", request.siteId) }

        val user = User(
            name = request.name,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = targetRole,
            site = site
        )

        val savedUser = userRepository.save(user)
        return savedUser.toResponse()
    }

    private fun enforceCreationRules(
        requestedBy: User,
        targetRoleName: String,
        targetSiteId: UUID
    ) {
        when (requestedBy.role.name) {
            "SUPER_ADMIN" -> {} // Unrestricted
            "MANAGER" -> {
                if (targetRoleName != "STAFF") throw AccessDeniedException("Managers can only create STAFF accounts")
                if (targetSiteId != requestedBy.site.id) throw AccessDeniedException("Managers can only create users at their own site")
            }
            else -> throw AccessDeniedException("Insufficient privileges to create users")
        }
    }
}
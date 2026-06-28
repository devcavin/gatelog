package io.github.devcavin.backend.service

import io.github.devcavin.backend.common.exception.ConflictException
import io.github.devcavin.backend.common.exception.InvalidCredentialsException
import io.github.devcavin.backend.common.exception.InvalidStateException
import io.github.devcavin.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.backend.domain.model.User
import io.github.devcavin.backend.domain.repository.RoleRepository
import io.github.devcavin.backend.domain.repository.SiteRepository
import io.github.devcavin.backend.domain.repository.UserRepository
import io.github.devcavin.backend.web.dto.user.ChangePasswordRequest
import io.github.devcavin.backend.web.dto.user.CreateUserRequest
import io.github.devcavin.backend.web.dto.user.UpdateUserRequest
import io.github.devcavin.backend.web.dto.user.UserResponse
import io.github.devcavin.backend.web.dto.user.toResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

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

    @Transactional(readOnly = true)
    fun getAll(requestedBy: User): List<UserResponse> {
        return when (requestedBy.role.name) {
            "SUPER_ADMIN" -> userRepository.findAll().map { it.toResponse() }
            else -> userRepository
                .findAllBySiteId(requestedBy.site.id!!)
                .map { it.toResponse() }
        }
    }

    @Transactional(readOnly = true)
    fun getById(requestedBy: User, userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }
        enforceSiteBoundary(requestedBy, user)
        return user.toResponse()
    }

    @Transactional
    fun updateUser(
        requestedBy: User,
        userId: UUID,
        request: UpdateUserRequest
    ): UserResponse {
        val target = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        enforceSiteBoundary(requestedBy, target)
        enforceUpdateRules(requestedBy, target, request.roleName)

        if (request.email != target.email &&
            userRepository.existsByEmail(request.email)
        ) {
            throw ConflictException("Email already in use: ${request.email}")
        }

        val newRole = roleRepository.findByName(request.roleName)
            ?: throw ResourceNotFoundException("Role", request.roleName)

        target.name = request.name
        target.email = request.email
        target.role = newRole

        return userRepository.save(target).toResponse()
    }

    @Transactional
    fun deactivate(requestedBy: User, userId: UUID): UserResponse {
        if (requestedBy.id == userId) {
            throw InvalidStateException("You cannot deactivate your own account")
        }
        val target = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        enforceSiteBoundary(requestedBy, target)
        enforceDeactivationRules(requestedBy, target)

        target.isActive = false
        return userRepository.save(target).toResponse()
    }

    @Transactional
    fun activate(requestedBy: User, userId: UUID): UserResponse {
        val target = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }
        enforceSiteBoundary(requestedBy, target)
        target.isActive = true
        return userRepository.save(target).toResponse()
    }

    @Transactional
    fun changePassword(
        requestedBy: User,
        request: ChangePasswordRequest
    ): UserResponse {
        if (!passwordEncoder.matches(
                request.currentPassword, requestedBy.passwordHash
            )
        ) {
            throw InvalidCredentialsException()
        }
        requestedBy.passwordHash = passwordEncoder.encode(request.newPassword)
        return userRepository.save(requestedBy).toResponse()
    }

    private fun enforceCreationRules(
        requestedBy: User,
        targetRoleName: String,
        targetSiteId: UUID
    ) {
        when (requestedBy.role.name) {
            "SUPER_ADMIN" -> Unit
            "MANAGER" -> {
                if (targetRoleName != "STAFF") throw AccessDeniedException("Managers can only create STAFF accounts")
                if (targetSiteId != requestedBy.site.id) throw AccessDeniedException("Managers can only create users at their own site")
            }
            else -> throw AccessDeniedException("Insufficient privileges to create users")
        }
    }

    private fun enforceSiteBoundary(requestedBy: User, target: User) {
        if (requestedBy.role.name != "SUPER_ADMIN" &&
            requestedBy.site.id != target.site.id
        ) {
            throw AccessDeniedException("User does not belong to your site")
        }
    }

    private fun enforceDeactivationRules(requestedBy: User, target: User) {
        when (requestedBy.role.name) {
            "SUPER_ADMIN" -> Unit
            "MANAGER" -> {
                if (target.role.name != "STAFF")
                    throw AccessDeniedException("Managers can only deactivate Staff accounts")
            }
            else -> throw AccessDeniedException("Insufficient privilege to deactivate users")
        }
    }

    private fun enforceUpdateRules(
        requestedBy: User,
        target: User,
        newRoleName: String
    ) {
        when (requestedBy.role.name) {
            "SUPER_ADMIN" -> Unit
            "MANAGER" -> {
                if (target.role.name != "STAFF")
                    throw AccessDeniedException("Managers can only update Staff accounts")
                if (newRoleName != "STAFF")
                    throw AccessDeniedException("Managers cannot change role beyond Staff")
            }
            else -> throw AccessDeniedException("Insufficient privilege to update users")
        }
    }
}
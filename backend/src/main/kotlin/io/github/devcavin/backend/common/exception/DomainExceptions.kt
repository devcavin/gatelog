package io.github.devcavin.backend.common.exception

// base type
sealed class DomainException(message: String) : RuntimeException(message)

// 401 — bad credentials or invalid/expired tokens
sealed class UnauthorizedException(message: String): DomainException(message)

class InvalidCredentialsException : UnauthorizedException("Invalid username or password")

class InvalidRefreshTokenException : UnauthorizedException("Invalid or expired refresh token")

// 403 - authenticated but !permitted
class AccountDisabledException : DomainException("Account is disabled")

// 404 - resource !found
class ResourceNotFoundException(resource: String, id: Any) : DomainException("Resource $resource not found: $id")

// 409 - conflicts with existing state/resources, etc...
class ConflictException(message: String) : DomainException(message)

// 422 - semantically invalid request (e.g. checking out already checked out visitor)
class InvalidStateException(message: String) : DomainException(message)
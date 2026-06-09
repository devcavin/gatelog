package io.github.devcavin.backend.domain.repository

import io.github.devcavin.backend.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID>{
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun findAllBySiteId(siteId: UUID): List<User>
    fun findAllBySiteIdAndIsActiveTrue(siteId: UUID): List<User>

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    fun findByEmailWithRole(email: String): User?
}
package io.github.devcavin.backend.domain.repository

import io.github.devcavin.backend.domain.model.VisitorProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VisitorProfileRepository : JpaRepository<VisitorProfile, UUID> {
    fun findBySiteIdAndPhoneNumber(siteId: UUID, phoneNumber: String): VisitorProfile?
    fun existsBySiteIdAndPhoneNumber(siteId: UUID, phoneNumber: String): Boolean
}
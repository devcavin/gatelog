package io.github.devcavin.backend.domain.repository

import io.github.devcavin.backend.domain.model.VisitStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VisitStatusRepository : JpaRepository<VisitStatus, UUID> {
    fun findByName(name: String): VisitStatus?
}
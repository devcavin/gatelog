package io.github.devcavin.backend.domain.repository

import io.github.devcavin.backend.domain.model.Site
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SiteRepository : JpaRepository<Site, UUID> {
    fun existsByNameAndLocation(name: String, location: String): Boolean
}
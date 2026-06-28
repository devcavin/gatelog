package io.github.devcavin.backend.service

import io.github.devcavin.backend.common.exception.ConflictException
import io.github.devcavin.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.backend.domain.repository.SiteRepository
import io.github.devcavin.backend.web.dto.site.SiteRequest
import io.github.devcavin.backend.web.dto.site.SiteResponse
import io.github.devcavin.backend.web.dto.site.toEntity
import io.github.devcavin.backend.web.dto.site.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SiteService(
    private val siteRepository: SiteRepository
) {
    @Transactional
    fun create(request: SiteRequest): SiteResponse {
        if (siteRepository.existsByNameAndLocation(request.name, request.location)) {
            throw ConflictException("Site with this name and location already exists")
        }

        val site = siteRepository.save(request.toEntity())

        return site.toResponse()
    }

    @Transactional(readOnly = true)
    fun getAll(): List<SiteResponse> = siteRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(id: UUID): SiteResponse {
        val site = siteRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Site", id) }
        return site.toResponse()
    }

    @Transactional
    fun update(id: UUID, request: SiteRequest): SiteResponse {
        val site = siteRepository.findById(id)
        .orElseThrow { ResourceNotFoundException("Site", id) }

        site.name = request.name
        site.location = request.location

        return siteRepository.save(site).toResponse()
    }

    @Transactional
    fun delete(id: UUID) {
        if (!siteRepository.existsById(id)) throw ResourceNotFoundException("Site", id)

        return siteRepository.deleteById(id)
    }
}
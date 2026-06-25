package io.github.devcavin.backend.service

import io.github.devcavin.backend.common.exception.InvalidStateException
import io.github.devcavin.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.backend.domain.model.User
import io.github.devcavin.backend.domain.model.Visitor
import io.github.devcavin.backend.domain.repository.VisitStatusRepository
import io.github.devcavin.backend.domain.repository.VisitorRepository
import io.github.devcavin.backend.domain.repository.VisitorSpecification
import io.github.devcavin.backend.domain.repository.ZoneRepository
import io.github.devcavin.backend.web.dto.visitor.RegisterVisitorRequest
import io.github.devcavin.backend.web.dto.visitor.ReturningVisitorResponse
import io.github.devcavin.backend.web.dto.visitor.VisitorResponse
import io.github.devcavin.backend.web.dto.visitor.VisitorSearchParams
import io.github.devcavin.backend.web.dto.visitor.toResponse
import io.github.devcavin.backend.web.dto.visitor.toReturningResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class VisitorService(
    private val visitorRepository: VisitorRepository,
    private val visitStatusRepository: VisitStatusRepository,
    private val zoneRepository: ZoneRepository
) {

    @Transactional
    fun register(
        requestedBy: User,
        request: RegisterVisitorRequest
    ): VisitorResponse {
        val checkedInStatus = visitStatusRepository.findByName("CHECKED_IN")
            ?: throw ResourceNotFoundException("VisitStatus", "CHECKED_IN")

        val zone = request.zoneId?.let {
            zoneRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Zone", it) }
                .also { z ->
                    if (z.site.id != requestedBy.site.id) {
                        throw AccessDeniedException("Zone does not belong to your site")
                    }
                }
        }

        val visitor = Visitor(
            name = request.name,
            phone = request.phone,
            site = requestedBy.site,
            zone = zone,
            createdBy = requestedBy,
            visitStatus = checkedInStatus,
            visitorType = request.visitorType,
            purpose = request.purpose
        )

        return visitorRepository.save(visitor).toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(requestedBy: User, visitorId: UUID): VisitorResponse {
        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow { ResourceNotFoundException("Visitor", visitorId) }

        enforcesSiteBoundary(requestedBy, visitor.site.id!!)
        return visitor.toResponse()
    }

    @Transactional
    fun checkOut(requestedBy: User, visitorId: UUID): VisitorResponse {
        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow { ResourceNotFoundException("Visitor", visitorId) }

        enforcesSiteBoundary(requestedBy, visitor.site.id!!)

        if (visitor.visitStatus.name != "CHECKED_IN") {
            throw InvalidStateException(
                "Visitor is already ${visitor.visitStatus.name.lowercase().replace('_', ' ')}"
            )
        }

        val checkedOutStatus = visitStatusRepository.findByName("CHECKED_OUT")
            ?: throw ResourceNotFoundException("VisitStatus", "CHECKED_OUT")

        visitor.visitStatus = checkedOutStatus
        visitor.checkOutTime = OffsetDateTime.now()

        return visitorRepository.save(visitor).toResponse()
    }

    @Transactional(readOnly = true)
    fun search(
        requestedBy: User,
        params: VisitorSearchParams,
        pageable: Pageable
    ): Page<VisitorResponse> {
        val spec = VisitorSpecification.search(requestedBy.site.id!!, params)
        return visitorRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findReturningVisitor(
        requestedBy: User,
        phone: String
    ): ReturningVisitorResponse? {
        return visitorRepository
            .findTopBySiteIdAndPhoneOrderByCheckInTimeDesc(requestedBy.site.id!!, phone)
            ?.toReturningResponse()
    }

    private fun enforcesSiteBoundary(requestedBy: User, visitorSiteId: UUID) {
        if (requestedBy.role.name != "SUPER_ADMIN" &&
            requestedBy.site.id != visitorSiteId
        ) {
            throw AccessDeniedException("Visitor does not belong to your site")
        }
    }
}
package io.github.devcavin.backend.service

import io.github.devcavin.backend.domain.model.User
import io.github.devcavin.backend.domain.repository.VisitStatusRepository
import io.github.devcavin.backend.domain.repository.VisitorRepository
import io.github.devcavin.backend.web.dto.dashboard.DashboardFeed
import io.github.devcavin.backend.web.dto.dashboard.DashboardSummary
import io.github.devcavin.backend.web.dto.visitor.toResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class DashboardService(
    private val visitorRepository: VisitorRepository,
    private val visitorStatusRepository: VisitStatusRepository,

    @Value("\${gatelog.scheduler.overdue-threshold-hours:2}")
    private val overdueThresholdHours: Long,
) {
    @Transactional(readOnly = true)
    fun getFeed(requestedBy: User): DashboardFeed {
        val siteId = requestedBy.site.id!!
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val startOfDay = now.toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC)
        val endOfDay = startOfDay.plusDays(1)
        val overdueThreshold = now.minusHours(overdueThresholdHours)

        val checkedInStatus = visitorStatusRepository.findByName("CHECKED_IN")!!
        val checkedOutStatus = visitorStatusRepository.findByName("CHECKED_OUT")!!
        val overdueStatus = visitorStatusRepository.findByName("OVERDUE")!!

        // summary counts bar
        val currentlyOnPremises = visitorRepository.countBySiteIdAndVisitStatus(
            siteId = siteId,
            visitStatus = checkedInStatus
        )

        val checkedInToday = visitorRepository.findAllCheckedInToday(
            siteId = siteId,
            startOfDay = startOfDay,
            endOfDay = endOfDay,
            pageable = PageRequest.of(0, 1)
        ).totalElements

        val checkedOutToday = visitorRepository.findAllBySiteIdAndVisitStatus(
            siteId = siteId,
            visitStatus = checkedOutStatus,
            pageable = PageRequest.of(0, 1)
        ).totalElements

        val overdueCount = visitorRepository.findAllBySiteIdAndVisitStatus(
            siteId = siteId,
            visitStatus = overdueStatus,
            pageable = PageRequest.of(0, 1)
        ).totalElements

        val activeVisitors = visitorRepository.findAllBySiteIdAndVisitStatus(
            siteId = siteId,
            visitStatus = checkedInStatus,
            pageable = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.DESC, "checkoutTime"))
        ).content.map { it.toResponse() }

        val overdueVisitors = visitorRepository.findAllOverdue(
            siteId = siteId,
            threshold = overdueThreshold
        ).map { it.toResponse() }

        val recentlyCheckedOut = visitorRepository.findAllBySiteIdAndVisitStatus(
            siteId = siteId,
            visitStatus = checkedOutStatus,
            pageable = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.DESC, "checkoutTime"))
        ).content.map { it.toResponse() }

        return DashboardFeed(
            summary = DashboardSummary(
                currentlyOnPremises = currentlyOnPremises,
                checkedInToday = checkedInToday,
                checkedOutToday = checkedOutToday,
                overdueCount = overdueCount
            ),
            activeVisitors = activeVisitors,
            overdueVisitors = overdueVisitors,
            recentlyCheckedOut = recentlyCheckedOut
        )
    }
}
package io.github.devcavin.backend.web.dto.dashboard

import io.github.devcavin.backend.web.dto.visitor.VisitorResponse
import java.time.OffsetDateTime

data class DashboardSummary(
    val currentlyOnPremises: Long,
    val checkedInToday: Long,
    val checkedOutToday: Long,
    val overdueCount: Long,
    val asOf: OffsetDateTime = OffsetDateTime.now()
)

data class DashboardFeed(
    val summary: DashboardSummary,
    val activeVisitors: List<VisitorResponse>,
    val overdueVisitors: List<VisitorResponse>,
    val recentlyCheckedOut: List<VisitorResponse>
)
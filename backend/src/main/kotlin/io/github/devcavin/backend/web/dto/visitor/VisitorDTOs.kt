package io.github.devcavin.backend.web.dto.visitor

import io.github.devcavin.backend.domain.model.Visitor
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.UUID

data class RegisterVisitorRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Pattern(regexp = """^\+?[0-9\s\-]{7,25}$""", message = "Invalid phone number format")
    val phone: String,

    @field:NotBlank
    val visitorType: String,

    val purpose: String = "General visit",
    val zoneId: UUID? = null,
    val hostId: UUID? = null
)

data class VisitorResponse(
    val id: UUID,
    val name: String,
    val phone: String,
    val visitorType: String,
    val purpose: String,
    val status: String,
    val siteId: UUID,
    val zoneId: UUID?,
    val zoneName: String?,
    val createdById: UUID,
    val createdByName: String,
    val checkInTime: OffsetDateTime,
    val checkOutTime: OffsetDateTime?
)

data class ReturningVisitorResponse(
    val name: String,
    val phone: String,
    val visitorType: String,
    val zoneId: UUID?,
    val zoneName: String?
)

data class VisitorSearchParams(
    val name: String? = null,
    val phone: String? = null,
    val visitorType: String? = null,
    val zoneId: UUID? = null,
    val status: String? = null,
    val from: OffsetDateTime? = null,
    val to: OffsetDateTime? = null
)

fun Visitor.toResponse() = VisitorResponse(
    id = id!!,
    name = name,
    phone = phone,
    visitorType = visitorType,
    purpose = purpose,
    status = visitStatus.name,
    siteId = site.id!!,
    zoneId = zone?.id,
    zoneName = zone?.name,
    createdById = createdBy.id!!,
    createdByName = createdBy.name,
    checkInTime = checkInTime,
    checkOutTime = checkOutTime
)

fun Visitor.toReturningResponse() = ReturningVisitorResponse(
    name = name,
    phone = phone,
    visitorType = visitorType,
    zoneId = zone?.id,
    zoneName = zone?.name
)

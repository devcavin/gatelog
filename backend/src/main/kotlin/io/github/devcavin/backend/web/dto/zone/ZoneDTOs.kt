package io.github.devcavin.backend.web.dto.zone

import io.github.devcavin.backend.domain.model.Zone
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class ZoneRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String
)

data class ZoneResponse(
    val id: UUID,
    val name: String,
    val siteId: UUID
)

fun Zone.toResponse() = ZoneResponse(
    id = this.id!!,
    name = this.name,
    siteId = this.site.id!!
)
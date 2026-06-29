package io.github.devcavin.backend.web.dto.site

import io.github.devcavin.backend.domain.model.Site
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class SiteRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Size(max = 255)
    val location: String
)

data class SiteResponse(
    val id: UUID,
    val name: String,
    val location: String
)

fun SiteRequest.toEntity(): Site {
    return Site(
        name = name,
        location = location
    )
}

fun Site.toResponse() = SiteResponse(
    id = this.id!!,
    name = this.name,
    location = this.location
)
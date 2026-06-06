package io.github.devcavin.backend.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sites")
class Site(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    var id: UUID? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, length = 255)
    var location: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Site) return false

        return id != null && id == id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: System.identityHashCode(this)
    }

    override fun toString(): String {
        return "Site(id=$id, name='$name', location='$location')"
    }
}
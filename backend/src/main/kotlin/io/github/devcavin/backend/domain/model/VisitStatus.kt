package io.github.devcavin.backend.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "visit_statuses")
class VisitStatus(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    var id: UUID? = null,

    @Column(nullable = false, length = 100, unique = true)
    var name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Role) return false

        return id != null && id == other.id
    }

    override fun hashCode(): Int =
        id?.hashCode() ?: System.identityHashCode(this)


    override fun toString(): String {
        return "VisitStatus(id=$id, name='$name')"
    }
}
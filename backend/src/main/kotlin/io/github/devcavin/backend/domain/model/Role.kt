package io.github.devcavin.backend.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "roles")
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 100)
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
        return "Role(id=$id, name='$name')"
    }
}
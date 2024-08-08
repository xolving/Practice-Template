package lib.quick.authservice.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID

@Entity(name = "member")
class User (
    @Id
    @GeneratedValue(generator = "uuid2")
    val id: UUID,

    val email: String,

    val password: String,

    val role: Role
)

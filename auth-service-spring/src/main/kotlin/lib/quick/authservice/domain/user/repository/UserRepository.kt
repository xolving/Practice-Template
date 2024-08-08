package lib.quick.authservice.domain.user.repository;

import jakarta.persistence.LockModeType
import lib.quick.authservice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock

import java.util.Optional;
import java.util.UUID;

interface UserRepository: JpaRepository<User, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Optional<User>
}

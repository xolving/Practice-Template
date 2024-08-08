package lib.quick.authservice.global.util

import lib.quick.authservice.domain.user.entity.User
import lib.quick.authservice.domain.user.repository.UserRepository
import lib.quick.authservice.global.exception.HttpException
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class MemberUtil (
    private val userRepository: UserRepository
) {
    fun getCurrentMember(): User {
        return userRepository.findByEmail(email = SecurityContextHolder.getContext().authentication.name)
            .orElseThrow { HttpException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.") }
    }
}

package lib.quick.authservice.global.security.details;

import lib.quick.authservice.domain.user.repository.UserRepository
import lib.quick.authservice.global.exception.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class MemberDetailsService(
    private val userRepository: UserRepository
): UserDetailsService {
    @Override
    override fun loadUserByUsername(id: String): UserDetails {
        return MemberDetails(
            userRepository.findById(UUID.fromString(id)).orElseThrow {
                HttpException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.")
            }
        )
    }
}

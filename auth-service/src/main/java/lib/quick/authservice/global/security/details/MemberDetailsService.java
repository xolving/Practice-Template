package lib.quick.authservice.global.security.details;

import lib.quick.authservice.domain.member.repository.MemberRepository;
import lib.quick.authservice.global.exception.HttpException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return new MemberDetails(
            memberRepository.findById(UUID.fromString(id)).orElseThrow(() ->
                new HttpException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.")
            )
        );
    }
}

package lib.quick.authservice.global.security.details;

import lib.quick.authservice.domain.member.repository.MemberRepository;
import lib.quick.authservice.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return new MemberDetails(
            memberRepository.findById(Long.parseLong(id)).orElseThrow(MemberNotFoundException::new)
        );
    }
}

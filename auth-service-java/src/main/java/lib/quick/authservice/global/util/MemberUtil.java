package lib.quick.authservice.global.util;

import lib.quick.authservice.domain.member.entity.Member;
import lib.quick.authservice.domain.member.repository.MemberRepository;
import lib.quick.authservice.global.exception.HttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberUtil {
    private final MemberRepository memberRepository;

    public Member getCurrentMember(){
        return memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
            .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."));
    }
}

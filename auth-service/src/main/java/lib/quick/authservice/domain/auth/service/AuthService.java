package lib.quick.authservice.domain.auth.service;

import lib.quick.authservice.domain.auth.controller.dto.request.UserJoinRequest;
import lib.quick.authservice.domain.auth.controller.dto.request.UserLoginRequest;
import lib.quick.authservice.domain.auth.controller.dto.response.UserLoginResponse;
import lib.quick.authservice.domain.member.entity.Member;
import lib.quick.authservice.domain.member.entity.Role;
import lib.quick.authservice.domain.member.repository.MemberRepository;
import lib.quick.authservice.global.exception.HttpException;
import lib.quick.authservice.global.exception.MemberNotFoundException;
import lib.quick.authservice.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public void joinMember(UserJoinRequest userJoinRequest){
        if(memberRepository.existsByEmail(userJoinRequest.getEmail())){
            throw new HttpException(HttpStatus.BAD_REQUEST, "이미 해당 이메일을 사용하는 멤버가 존재합니다.");
        }

        String encodedPassword = passwordEncoder.encode(userJoinRequest.getPassword());
        Member member = Member.builder()
            .id(null)
            .email(userJoinRequest.getEmail())
            .password(encodedPassword)
            .roles(List.of(Role.ROLE_MEMBER))
            .build();

        memberRepository.save(member);
    }

    public UserLoginResponse loginMember(UserLoginRequest userLoginRequest){
        Member member = memberRepository.findByEmail(userLoginRequest.getEmail())
            .orElseThrow(MemberNotFoundException::new);

        if(!passwordEncoder.matches(userLoginRequest.getPassword(), member.getPassword())){
            throw new HttpException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.generateTokenSet(member.getId());
    }
}

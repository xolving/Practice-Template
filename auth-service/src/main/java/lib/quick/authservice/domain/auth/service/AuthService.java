package lib.quick.authservice.domain.auth.service;

import lib.quick.authservice.domain.auth.controller.dto.request.UserJoinRequest;
import lib.quick.authservice.domain.auth.controller.dto.request.UserLoginRequest;
import lib.quick.authservice.domain.auth.controller.dto.response.RefreshTokenResponse;
import lib.quick.authservice.domain.auth.controller.dto.response.UserLoginResponse;
import lib.quick.authservice.domain.member.entity.Member;
import lib.quick.authservice.domain.member.entity.Role;
import lib.quick.authservice.domain.member.repository.MemberRepository;
import lib.quick.authservice.global.exception.HttpException;
import lib.quick.authservice.global.security.dto.TokenType;
import lib.quick.authservice.global.security.jwt.JwtProvider;
import lib.quick.authservice.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberUtil memberUtil;

    public void joinMember(UserJoinRequest userJoinRequest){
        if(memberRepository.existsByEmail(userJoinRequest.getEmail())){
            throw new HttpException(HttpStatus.BAD_REQUEST, "이미 해당 이메일을 사용하는 멤버가 존재합니다.");
        }

        String encodedPassword = passwordEncoder.encode(userJoinRequest.getPassword());
        Member member = Member.builder()
            .id(UUID.randomUUID())
            .email(userJoinRequest.getEmail())
            .password(encodedPassword)
            .role(Role.ROLE_USER)
            .build();

        memberRepository.save(member);
    }

    public UserLoginResponse loginMember(UserLoginRequest userLoginRequest){
        Member member = memberRepository.findByEmail(userLoginRequest.getEmail())
            .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."));

        if(!passwordEncoder.matches(userLoginRequest.getPassword(), member.getPassword())){
            throw new HttpException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.generateTokenSet(member.getId());
    }

    public RefreshTokenResponse refreshToken(String accessToken, String refreshToken){
        accessToken = accessToken.substring(7);
        refreshToken = refreshToken.substring(7);

        Boolean validateAccess = jwtProvider.validateToken(accessToken);
        Boolean validateRefresh = jwtProvider.validateToken(refreshToken);

        Member member = memberRepository.findById(
            UUID.fromString(jwtProvider.getClaims(accessToken).getSubject())
        ).orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다."));

        if(validateAccess && validateRefresh) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "엑세스 토큰과 리프레스 토큰이 모두 만료되었습니다.");
        } else if(validateAccess) {
            return new RefreshTokenResponse(
                jwtProvider.generateToken(member.getId(), TokenType.ACCESS_TOKEN),
                refreshToken
            );
        } else if(validateRefresh) {
            return new RefreshTokenResponse(
                accessToken,
                jwtProvider.generateToken(member.getId(), TokenType.REFRESH_TOKEN)
            );
        } else {
            throw new HttpException(HttpStatus.BAD_REQUEST, "만료된 토큰이 없습니다");
        }
    }
}

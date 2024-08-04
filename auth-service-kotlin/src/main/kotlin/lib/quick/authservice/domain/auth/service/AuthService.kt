package lib.quick.authservice.domain.auth.service;

import lib.quick.authservice.domain.auth.controller.dto.request.UserJoinRequest
import lib.quick.authservice.domain.auth.controller.dto.request.UserLoginRequest
import lib.quick.authservice.domain.auth.controller.dto.response.RefreshTokenResponse;
import lib.quick.authservice.domain.auth.controller.dto.response.UserLoginResponse
import lib.quick.authservice.domain.user.entity.User;
import lib.quick.authservice.domain.user.entity.Role;
import lib.quick.authservice.global.exception.HttpException;
import lib.quick.authservice.global.security.dto.TokenType;
import lib.quick.authservice.global.security.jwt.JwtProvider
import lib.quick.authservice.domain.user.repository.UserRepository
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class AuthService (
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
    private val passwordEncoder: PasswordEncoder,
){
    fun joinMember(userJoinRequest: UserJoinRequest) {
        if(userRepository.existsByEmail(userJoinRequest.email)){
            throw HttpException(HttpStatus.BAD_REQUEST, "이미 해당 이메일을 사용하는 멤버가 존재합니다.")
        }

        val encodedPassword = passwordEncoder.encode(userJoinRequest.password);
        val user = User(
            id = UUID.randomUUID(),
            email = userJoinRequest.email,
            password = encodedPassword,
            role = Role.ROLE_USER
        )

        userRepository.save(user);
    }

    fun loginMember(userLoginRequest: UserLoginRequest): UserLoginResponse {
        val member = userRepository.findByEmail(userLoginRequest.email)
            .orElseThrow { HttpException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.") }

        if(!passwordEncoder.matches(userLoginRequest.password, member.password)){
            throw HttpException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.generateTokenSet(member.id);
    }

    fun refreshToken(accessToken: String, refreshToken: String): RefreshTokenResponse {
        val validateAccess: Boolean = jwtProvider.validateToken(accessToken.substring(7));
        val validateRefresh: Boolean = jwtProvider.validateToken(refreshToken.substring(7));

        val member = userRepository.findById(
            UUID.fromString(jwtProvider.getClaims(accessToken).subject)
        ).orElseThrow { HttpException(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다.") }

        return if(validateAccess && validateRefresh) {
            throw HttpException(HttpStatus.BAD_REQUEST, "엑세스 토큰과 리프레스 토큰이 모두 만료되었습니다.");
        } else if(validateAccess) {
            RefreshTokenResponse(
                jwtProvider.generateToken(member.id, TokenType.ACCESS_TOKEN),
                refreshToken
            );
        } else if(validateRefresh) {
            RefreshTokenResponse(
                accessToken,
                jwtProvider.generateToken(member.id, TokenType.REFRESH_TOKEN)
            );
        } else {
            throw HttpException(HttpStatus.BAD_REQUEST, "만료된 토큰이 없습니다");
        }
    }
}

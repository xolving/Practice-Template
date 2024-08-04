package lib.quick.authservice.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid;
import lib.quick.authservice.domain.auth.controller.dto.request.UserJoinRequest
import lib.quick.authservice.domain.auth.controller.dto.request.UserLoginRequest
import lib.quick.authservice.domain.auth.controller.dto.response.RefreshTokenResponse
import lib.quick.authservice.domain.auth.controller.dto.response.UserLoginResponse
import lib.quick.authservice.domain.auth.service.AuthService
import lib.quick.authservice.global.exception.HttpException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
class AuthController (
    private val authService: AuthService
){
    @PostMapping("/join")
    fun joinMember(@RequestBody @Valid userJoinRequest: UserJoinRequest): ResponseEntity<Void> {
        authService.joinMember(userJoinRequest)
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    fun loginMember(@RequestBody @Valid userLoginRequest: UserLoginRequest): ResponseEntity<UserLoginResponse> {
        return ResponseEntity.ok(authService.loginMember(userLoginRequest));
    }

    @PostMapping("/refresh")
    fun refreshToken(request: HttpServletRequest): ResponseEntity<RefreshTokenResponse> {
        val accessToken = request.getHeader("Authorization");
        val refreshToken = request.getHeader("Refresh-Token");

        if(accessToken == null && refreshToken == null){
            throw HttpException(HttpStatus.UNAUTHORIZED, "엑세스 토큰 또는 리프레시 토큰을 보내지 않았습니다.");
        }

        return ResponseEntity.ok(authService.refreshToken(accessToken, refreshToken));
    }
}

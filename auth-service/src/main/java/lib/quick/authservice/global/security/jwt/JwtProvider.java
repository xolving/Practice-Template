package lib.quick.authservice.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lib.quick.authservice.domain.auth.controller.dto.response.UserLoginResponse;
import lib.quick.authservice.global.exception.HttpException;
import lib.quick.authservice.global.security.dto.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final UserDetailsService userDetailsService;

    @Value("${spring.jwt.access-key}")
    private String accessKey;

    @Value("${spring.jwt.refresh-key}")
    private String refreshKey;

    @Value("${spring.jwt.access-expired}")
    public Long accessExp;

    @Value("${spring.jwt.refresh-expired}")
    public Long refreshExp;

    public UserLoginResponse generateTokenSet(UUID id){
        return new UserLoginResponse(
            generateToken(id, TokenType.ACCESS_TOKEN),
            generateToken(id, TokenType.REFRESH_TOKEN)
        );
    }

    public String generateToken(UUID id, TokenType tokenType) {
        Long expired = tokenType == TokenType.ACCESS_TOKEN ? accessExp : refreshExp;

        byte[] keyBytes = Base64.getEncoder().encode(accessKey.getBytes());
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
            .signWith(signingKey)
            .subject(String.valueOf(id))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expired))
            .compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getTokenSubject(token));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String getTokenSubject(String subject) {
        return getClaims(subject).getSubject();
    }

    public Claims getClaims(String token) {
        byte[] keyBytes = Base64.getEncoder().encode(accessKey.getBytes());
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);

        try {
            return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            throw new HttpException(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new HttpException(HttpStatus.FORBIDDEN, "형식이 일치하지 않는 토큰입니다.");
        } catch (MalformedJwtException e) {
            throw new HttpException(HttpStatus.FORBIDDEN, "올바르지 않은 구성의 토큰입니다.");
        } catch (SignatureException e) {
            throw new HttpException(HttpStatus.FORBIDDEN, "서명을 확인할 수 없는 토큰입니다.");
        } catch (RuntimeException e) {
            throw new HttpException(HttpStatus.FORBIDDEN, "알 수 없는 토큰입니다.");
        }
    }

    public Boolean validateToken(String token){
        byte[] keyBytes = Base64.getEncoder().encode(accessKey.getBytes());
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration()
            .before(new Date());
    }
}
package lib.quick.authservice.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lib.quick.authservice.global.security.jwt.JwtProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
class JwtFilter(
    private val jwtProvider: JwtProvider
): OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        var token = request.getHeader("Authorization");

        if(token != null){
            token = resolveToken(token);
            val authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().authentication = authentication;
        }

        filterChain.doFilter(request, response);
    }

    private fun resolveToken(authorization: String): String? {
        return if(authorization.startsWith("Bearer ")){
            authorization.substring(7);
        } else {
            null
        };
    }
}

package lib.quick.authservice.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lib.quick.authservice.global.exception.HttpException;
import lib.quick.authservice.global.security.dto.FilterExceptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch(HttpException e){
            httpServletResponse.setStatus(e.getStatusCode().value());
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setCharacterEncoding("UTF-8");

            FilterExceptionResponse response = new FilterExceptionResponse(e.getStatusCode().value(), e.getMessage());
            log.error("{}", objectMapper.writeValueAsString(response));

            objectMapper.writeValue(httpServletResponse.getWriter(), response);
        }
    }
}

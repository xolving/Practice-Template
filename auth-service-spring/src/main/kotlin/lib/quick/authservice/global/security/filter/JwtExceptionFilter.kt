package lib.quick.authservice.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lib.quick.authservice.global.exception.HttpException;
import lib.quick.authservice.global.security.dto.FilterExceptionResponse;
import lib.quick.authservice.global.util.getLogger
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
class JwtExceptionFilter(
    private val objectMapper: ObjectMapper
): OncePerRequestFilter() {
    private val log = getLogger()

    override fun doFilterInternal(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse, filterChain: FilterChain) {
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch(e: HttpException){
            httpServletResponse.status = e.statusCode.value();
            httpServletResponse.contentType = MediaType.APPLICATION_JSON_VALUE;
            httpServletResponse.characterEncoding = "UTF-8";

            val response = FilterExceptionResponse(e.statusCode.value(), e.message);
            log.error(objectMapper.writeValueAsString(response));

            objectMapper.writeValue(httpServletResponse.writer, response);
        }
    }
}

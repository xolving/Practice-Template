package lib.quick.authservice.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lib.quick.authservice.global.security.filter.JwtExceptionFilter;
import lib.quick.authservice.global.security.filter.JwtFilter;
import lib.quick.authservice.global.security.handler.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
class SecurityConfig (
    private val jwtFilter: JwtFilter,
    private val jwtExceptionFilter: JwtExceptionFilter,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
        .cors { it.configurationSource(corsConfigurationSource()) }
        .csrf { it.disable() }
        .formLogin { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        .addFilterBefore(jwtExceptionFilter, JwtFilter::class.java)
        .authorizeHttpRequests {
            it.requestMatchers("/auth/**").permitAll()
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
        }
        .exceptionHandling { it.accessDeniedHandler(CustomAccessDeniedHandler(objectMapper)) }

        return http.build();
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration();

        config.allowCredentials = true;
        config.allowedOrigins = listOf("http://localhost:3000");
        config.allowedMethods = listOf("*");
        config.allowedHeaders = listOf("*");
        config.exposedHeaders = listOf("*");
        config.maxAge = 86400L;

        val source = UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder();
    }
}


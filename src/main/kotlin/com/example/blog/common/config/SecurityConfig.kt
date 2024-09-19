package com.example.blog.common.config

import com.example.blog.common.exception.ErrorResponseHandler
import com.example.blog.common.jwt.JwtAccessDeniedHandler
import com.example.blog.common.jwt.JwtAuthenticationEntryPoint
import com.example.blog.common.jwt.TokenAuthenticationFilter
import com.example.blog.common.jwt.TokenProvider
import kotlin.jvm.Throws;
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenProvider: TokenProvider,
    private val errorResponseHandler: ErrorResponseHandler
) {

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * 특정 HTTP 요청에 대한 웹 기반 보안 구성
     */
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // CSRF 비활성화
            .formLogin { it.disable() } // Form 로그인 방식 비활성화
            .httpBasic { it.disable() } // HTTP Basic 인증 방식 비활성화

            // 토큰 기반 인증을 사용하기 때문에 세션 기능 비활성화
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            // 인증, 인가 설정
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/users/signup", "/users/login").permitAll()
                    .anyRequest().authenticated()
            }

            // 헤더를 확인할 커스텀 필터 추가
            .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

            // 인증 및 인가 예외 처리
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint(JwtAuthenticationEntryPoint(errorResponseHandler))
                    .accessDeniedHandler(JwtAccessDeniedHandler(errorResponseHandler))
            }

        return http.build()
    }

    @Bean
    fun tokenAuthenticationFilter(): TokenAuthenticationFilter {
        return TokenAuthenticationFilter(tokenProvider, errorResponseHandler)
    }
}

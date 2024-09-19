package com.example.blog.common.jwt

import com.example.blog.common.exception.CustomException
import com.example.blog.common.exception.ErrorResponseHandler
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.LinkedHashMap

class TokenAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val errorResponseHandler: ErrorResponseHandler
) : OncePerRequestFilter() {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 요청 헤더의 Authorization 키의 값 조회
        val authorizationHeader = request.getHeader(HEADER_AUTHORIZATION)

        // 가져온 값에서 접두사 제거
        val token = getAccessToken(authorizationHeader)

        // 토큰이 없다면 다음 필터로 넘김(권한 없는 요청일 수도 있으니)
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // 유효성 검증
            tokenProvider.validateToken(token)

            // 유효한 토큰인 경우 인증 정보 설정
            val authentication: Authentication = tokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication

        } catch (e: CustomException) {
            errorResponseHandler.sendErrorResponse(request, response, e.errorCode)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun getAccessToken(authorizationHeader: String?): String? {
        return if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            authorizationHeader.substring(TOKEN_PREFIX.length)
        } else {
            null
        }
    }
}

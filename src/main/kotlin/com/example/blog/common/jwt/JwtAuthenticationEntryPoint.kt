package com.example.blog.common.jwt

import com.example.blog.common.exception.ErrorCode
import com.example.blog.common.exception.ErrorResponseHandler
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin.jvm.Throws;
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class JwtAuthenticationEntryPoint(
    private val errorResponseHandler: ErrorResponseHandler
) : AuthenticationEntryPoint {
    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        // 유효한 자격증명을 제공하지 않고 접근하려 할 때 401 에러 반환
        errorResponseHandler.sendErrorResponse(request, response, ErrorCode.TOKEN_UNAUTHORIZED)
    }
}

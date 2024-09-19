package com.example.blog.common.jwt

import com.example.blog.common.exception.ErrorCode
import com.example.blog.common.exception.ErrorResponseHandler
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin.jvm.Throws;
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class JwtAccessDeniedHandler(
    private val errorResponseHandler: ErrorResponseHandler
): AccessDeniedHandler {
    @Throws(IOException::class, ServletException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        // 필요한 권한없이 할 때 403 에러한 없이 접근 반환
        errorResponseHandler.sendErrorResponse(request, response, ErrorCode.ACCESS_DENIED)
    }
}

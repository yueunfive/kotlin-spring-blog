package com.example.blog.service

import com.example.blog.common.exception.CustomException
import com.example.blog.common.jwt.TokenProvider
import com.example.blog.domain.User
import com.example.blog.repository.ArticleRepository
import com.example.blog.repository.CommentRepository
import com.example.blog.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

import com.example.blog.common.exception.ErrorCode.*
import com.example.blog.dto.request.LoginRequest
import com.example.blog.dto.request.SignupRequest
import com.example.blog.dto.response.SignupResponse
import org.springframework.data.repository.findByIdOrNull

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository
) {

    companion object {
        private val ACCESS_TOKEN_DURATION = Duration.ofMinutes(30)
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }

    /**
     * 회원 가입
     */
    @Transactional
    fun signup(request: SignupRequest): SignupResponse {
        // 중복 확인(이메일)
        if (userRepository.existsByEmail(request.email)) throw CustomException(EMAIL_ALREADY_EXISTS)

        val encodedPassword = bCryptPasswordEncoder.encode(request.password)

        val newUser = User(
            email = request.email,
            username = request.username,
            password = encodedPassword
        )

        userRepository.save(newUser)

        return SignupResponse(
            email = newUser.email,
            username = newUser.username
        )
    }

    /**
     * 로그인
     */
    @Transactional
    fun login(request: LoginRequest, response: HttpServletResponse) {
        val user = userRepository.findByEmail(request.email) ?: throw CustomException(USER_NOT_FOUND)

        if (!bCryptPasswordEncoder.matches(request.password, user.password)) {
            throw CustomException(INVALID_PASSWORD)
        }

        val accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION)
        log.info("access_token: $accessToken")

        // 응답 설정
        response.setHeader("access_token", accessToken)
        response.status = HttpStatus.OK.value()
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    fun unlink(userId: Long) {
        val user = userRepository.findByIdOrNull(userId) ?: throw CustomException(USER_NOT_FOUND)

        val articlesToDelete = articleRepository.findAllByUser(user)

        for (article in articlesToDelete) {
            val commentsToDelete = commentRepository.findAllByArticle(article)
            commentRepository.deleteAll(commentsToDelete)
        }
        articleRepository.deleteAll(articlesToDelete)

        val commentsToDelete = commentRepository.findAllByUser(user)
        commentRepository.deleteAll(commentsToDelete)

        userRepository.delete(user)
    }
}

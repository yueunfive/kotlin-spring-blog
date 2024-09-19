package com.example.blog.service

import com.example.blog.common.exception.CustomException
import com.example.blog.domain.Article
import com.example.blog.domain.Comment
import com.example.blog.dto.request.LoginRequest
import com.example.blog.dto.request.SignupRequest
import com.example.blog.repository.ArticleRepository
import com.example.blog.repository.CommentRepository
import com.example.blog.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val articleRepository: ArticleRepository
) {

    @Test
    @DisplayName("회원 가입 성공")
    fun signupTest() {
        // given
        val request = SignupRequest(email = "test@example.com", username = "username1", password = "password1")

        // when
        val response = userService.signup(request)

        // then
        assertEquals("test@example.com", response.email)
        assertEquals("username1", response.username)
    }

    @Test
    @DisplayName("회원 가입 실패 - 중복 이메일 방지")
    fun signupTest_duplicateEmail() {
        // given
        val request = SignupRequest(email = "test@example.com", username = "username1", password = "password1")
        userService.signup(request)

        // when & then
        val duplicateRequest = SignupRequest(email = "test@example.com", username = "username2", password = "password2")
        assertThrows<CustomException> { userService.signup(duplicateRequest) }
    }

    @Test
    @DisplayName("로그인 성공")
    fun loginTest() {
        // given
        val signupRequest = SignupRequest(email = "test@example.com", username = "username", password = "password")
        userService.signup(signupRequest)

        val loginRequest = LoginRequest(email = "test@example.com", password = "password")
        val response = mock(HttpServletResponse::class.java)

        // when
        userService.login(loginRequest, response)

        // then
        verify(response).setHeader(eq("access_token"), any())
        verify(response).status = HttpStatus.OK.value()
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 오류")
    fun loginTest_invalidPassword() {
        // given
        val signupRequest = SignupRequest(email = "test@example.com", username = "username", password = "password")
        userService.signup(signupRequest)

        val invalidLoginRequest = LoginRequest(email = "test@example.com", password = "hello")
        val response = mock(HttpServletResponse::class.java)

        // when & then
        assertThrows<CustomException> { userService.login(invalidLoginRequest, response) }
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    fun loginTest_userNotFound() {
        // given
        val nonExistentLoginRequest = LoginRequest(email = "test@example.com", password = "password")
        val response = mock(HttpServletResponse::class.java)

        // when & then
        assertThrows<CustomException> { userService.login(nonExistentLoginRequest, response) }
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    fun unlinkTest() {
        // given
        val signupRequest = SignupRequest(email = "test@example.com", username = "username", password = "password")
        userService.signup(signupRequest)

        val user = userRepository.findByEmail("test@example.com")!!
        val userId = user.id

        val article = Article(user = user, title = "title", content = "content")
        articleRepository.save(article)

        val comment = Comment(user = user, article = article, content = "content")
        commentRepository.save(comment)

        // when
        userService.unlink(userId!!)

        // then
        assertNull(userRepository.findByIdOrNull(userId))
        assertEquals(0, commentRepository.count())
        assertEquals(0, articleRepository.count())
    }
}
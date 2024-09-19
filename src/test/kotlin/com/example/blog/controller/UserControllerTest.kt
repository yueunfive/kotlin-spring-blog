package com.example.blog.controller

import com.example.blog.dto.request.LoginRequest
import com.example.blog.dto.request.SignupRequest
import com.example.blog.dto.response.SignupResponse
import com.example.blog.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.security.Principal

@ActiveProfiles("test")
@WebMvcTest(UserController::class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockBean private val userService: UserService
) {
    @Test
    @DisplayName("회원가입 성공")
    fun signupTest() {
        // given
        val request = SignupRequest(email = "test@example.com", username = "username", password = "password")
        val response = SignupResponse(email = "test@example.com", username = "username")

        `when`(userService.signup(request)).thenReturn(response)

        // when & then
        mockMvc.perform(
            post("/users/signup")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("로그인 성공")
    fun loginTest() {
        // given
        val request = LoginRequest(email = "test@example.com", password = "password")

        // when & then
        mockMvc.perform(
            post("/users/login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    fun unlinkTest() {
        // given
        val principal = Mockito.mock(Principal::class.java)
        `when`(principal.name).thenReturn("1")

        // when & then
        mockMvc.perform(
            delete("/users/unlink")
                .principal(principal)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }
}

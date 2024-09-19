package com.example.blog.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
class UserTest {

    @Test
    @DisplayName("사용자 생성")
    fun userTest() {
        // given
        val email = "test@example.com"
        val username = "username"
        val password = "password"

        // when
        val user = User(email = email, username = username, password = password)

        // then
        assertEquals(email, user.email)
        assertEquals(username, user.username)
        assertEquals(password, user.password)
    }
}
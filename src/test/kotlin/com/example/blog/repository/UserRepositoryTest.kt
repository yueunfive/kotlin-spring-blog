package com.example.blog.repository

import com.example.blog.domain.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository
){
    @Test
    @DisplayName("이메일로 유저 가입 유무 확인")
    fun existsByEmailTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        // when
        val exists = userRepository.existsByEmail("test@example.com")

        // then
        assert(exists)
    }

    @Test
    @DisplayName("이메일로 유저 조회")
    fun findByEmailTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        // when
        val retrievedUser = userRepository.findByEmail("test@example.com")

        // then
        assert(retrievedUser != null)
        assert(retrievedUser?.email == user.email)
        assert(retrievedUser?.username == user.username)
    }
}
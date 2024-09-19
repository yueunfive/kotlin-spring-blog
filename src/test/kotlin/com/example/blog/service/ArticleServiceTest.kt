package com.example.blog.service

import com.example.blog.common.exception.CustomException
import com.example.blog.domain.Article
import com.example.blog.domain.User
import com.example.blog.dto.request.EditArticleRequest
import com.example.blog.dto.request.PostArticleRequest
import com.example.blog.repository.ArticleRepository
import com.example.blog.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ArticleServiceTest @Autowired constructor(
    private val articleService: ArticleService,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository
) {

    @Test
    @DisplayName("게시글 작성 성공")
    fun postArticleTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val request = PostArticleRequest(title = "title", content = "content")

        // when
        val response = articleService.post(user.id!!, request)

        // then
        assertEquals("title", response.title)
        assertEquals("content", response.content)
        assertEquals("test@example.com", response.email)
    }

    @Test
    @DisplayName("게시글 작성 실패 - 존재하지 않는 사용자")
    fun postArticleTest_userNotFound() {
        // given
        val request = PostArticleRequest(title = "title", content = "content")

        // when & then
        assertThrows<CustomException> { articleService.post(999L, request) }
    }

    @Test
    @DisplayName("게시글 수정 성공")
    fun editArticleTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article = Article(title = "title", content = "content", user = user)
        articleRepository.save(article)

        val request = EditArticleRequest(title = "updated title", content = "updated content")

        // when
        val response = articleService.edit(user.id!!, article.id!!, request)

        // then
        assertEquals("updated title", response.title)
        assertEquals("updated content", response.content)
        assertEquals("test@example.com", response.email)
    }

    @Test
    @DisplayName("게시글 수정 실패 - 존재하지 않는 게시글")
    fun editArticleTest_articleNotFound() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val request = EditArticleRequest(title = "updated title", content = "updated content")

        // when & then
        assertThrows<CustomException> { articleService.edit(user.id!!, 999L, request) }
    }

    @Test
    @DisplayName("게시글 수정 실패 - 권한 없음")
    fun editArticleTest_noPermission() {
        // given
        val user1 = User(email = "test1@example.com", username = "username1", password = "password1")
        userRepository.save(user1)

        val user2 = User(email = "test2@example.com", username = "username2", password = "password2")
        userRepository.save(user2)

        val article = Article(title = "title", content = "content", user = user1)
        articleRepository.save(article)

        val request = EditArticleRequest(title = "updated title", content = "updated content")

        // when & then
        assertThrows<CustomException> { articleService.edit(user2.id!!, article.id!!, request) }
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    fun deleteArticleTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article = Article(title = "title", content = "content", user = user)
        articleRepository.save(article)

        // when
        articleService.delete(user.id!!, article.id!!)

        // then
        assertNull(articleRepository.findByIdOrNull(article.id))
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시글")
    fun deleteArticleTest_articleNotFound() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        // when & then
        assertThrows<CustomException> { articleService.delete(user.id!!, 999L) }
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 권한 없음")
    fun deleteArticleTest_noPermission() {
        // given
        val user1 = User(email = "test1@example.com", username = "username1", password = "password1")
        userRepository.save(user1)

        val user2 = User(email = "test2@example.com", username = "username2", password = "password2")
        userRepository.save(user2)

        val article = Article(title = "title", content = "content", user = user1)
        articleRepository.save(article)

        // when & then
        assertThrows<CustomException> { articleService.delete(user2.id!!, article.id!!) }
    }
}

package com.example.blog.service

import com.example.blog.common.exception.CustomException
import com.example.blog.domain.Article
import com.example.blog.domain.Comment
import com.example.blog.domain.User
import com.example.blog.dto.request.EditCommentRequest
import com.example.blog.dto.request.PostCommentRequest
import com.example.blog.repository.ArticleRepository
import com.example.blog.repository.CommentRepository
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
class CommentServiceTest @Autowired constructor(
    private val commentService: CommentService,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository
) {

    @Test
    @DisplayName("댓글 작성 성공")
    fun postCommentTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article = Article(title = "title", content = "content", user = user)
        articleRepository.save(article)

        val request = PostCommentRequest(articleId = article.id!!, content = "comment")

        // when
        val response = commentService.post(user.id!!, request)

        // then
        assertEquals("comment", response.content)
        assertEquals("test@example.com", response.email)
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 사용자")
    fun postCommentTest_userNotFound() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article = Article(title = "title", content = "content", user = user)
        articleRepository.save(article)

        val request = PostCommentRequest(articleId = article.id!!, content = "comment")

        // when & then
        assertThrows<CustomException> { commentService.post(999L, request) }
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 게시글")
    fun postCommentTest_articleNotFound() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val request = PostCommentRequest(articleId = 999L, content = "comment")

        // when & then
        assertThrows<CustomException> { commentService.post(user.id!!, request) }
    }

    @Test
    @DisplayName("댓글 수정 성공")
    fun editCommentTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article = Article(title = "title", content = "content", user = user)
        articleRepository.save(article)

        val comment = Comment(content = "comment", user = user, article = article)
        commentRepository.save(comment)

        val request = EditCommentRequest(content = "updated comment")

        // when
        val response = commentService.edit(user.id!!, comment.id!!, request)

        // then
        assertEquals("updated comment", response.content)
        assertEquals("test@example.com", response.email)
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    fun editCommentTest_commentNotFound() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val request = EditCommentRequest(content = "updated comment")

        // when & then
        assertThrows<CustomException> { commentService.edit(user.id!!, 999L, request) }
    }

    @Test
    @DisplayName("댓글 수정 실패 - 권한 없음")
    fun editCommentTest_noPermission() {
        // given
        val user1 = User(email = "test1@example.com", username = "username1", password = "password1")
        userRepository.save(user1)

        val user2 = User(email = "test2@example.com", username = "username2", password = "password2")
        userRepository.save(user2)

        val article = Article(title = "title", content = "content", user = user1)
        articleRepository.save(article)

        val comment = Comment(content = "comment", user = user1, article = article)
        commentRepository.save(comment)

        val request = EditCommentRequest(content = "updated comment")

        // when & then
        assertThrows<CustomException> { commentService.edit(user2.id!!, comment.id!!, request) }
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    fun deleteCommentTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article = Article(title = "title", content = "content", user = user)
        articleRepository.save(article)

        val comment = Comment(content = "comment", user = user, article = article)
        commentRepository.save(comment)

        // when
        commentService.delete(user.id!!, comment.id!!)

        // then
        assertNull(commentRepository.findByIdOrNull(comment.id))
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
    fun deleteCommentTest_commentNotFound() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        // when & then
        assertThrows<CustomException> { commentService.delete(user.id!!, 999L) }
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    fun deleteCommentTest_noPermission() {
        // given
        val user1 = User(email = "test1@example.com", username = "username1", password = "password1")
        userRepository.save(user1)

        val user2 = User(email = "test2@example.com", username = "username2", password = "password2")
        userRepository.save(user2)

        val article = Article(title = "title", content = "content", user = user1)
        articleRepository.save(article)

        val comment = Comment(content = "comment", user = user1, article = article)
        commentRepository.save(comment)

        // when & then
        assertThrows<CustomException> { commentService.delete(user2.id!!, comment.id!!) }
    }
}

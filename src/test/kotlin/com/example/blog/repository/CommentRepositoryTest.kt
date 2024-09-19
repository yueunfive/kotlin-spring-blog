package com.example.blog.repository

import com.example.blog.domain.Article
import com.example.blog.domain.Comment
import com.example.blog.domain.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest @Autowired constructor(
    private val commentRepository: CommentRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
){
    @Test
    @DisplayName("게시물에 달린 모든 댓글 조회")
    fun findAllByArticleTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article = Article(title = "title", content = "content", user = user)
        articleRepository.save(article)

        val comment1 = Comment(content = "content1", user = user, article = article)
        val comment2 = Comment(content = "content2", user = user, article = article)
        commentRepository.saveAll(listOf(comment1, comment2))

        // when
        val comments = commentRepository.findAllByArticle(article)

        // then
        assert(comments.size == 2)
        assert(comments.contains(comment1))
        assert(comments.contains(comment2))
    }

    @Test
    @DisplayName("유저가 작성한 모든 댓글 조회")
    fun findAllByUserTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article = Article(title = "title", content = "content", user = user)
        articleRepository.save(article)

        val comment1 = Comment(content = "content1", user = user, article = article)
        val comment2 = Comment(content = "content2", user = user, article = article)
        commentRepository.saveAll(listOf(comment1, comment2))

        // when
        val comments = commentRepository.findAllByUser(user)

        // then
        assert(comments.size == 2)
        assert(comments.contains(comment1))
        assert(comments.contains(comment2))
    }
}
package com.example.blog.repository;

import com.example.blog.domain.Article
import com.example.blog.domain.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class ArticleRepositoryTest @Autowired constructor(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository

){
    @Test
    @DisplayName("유저가 작성한 모든 게시물 조회")
    fun findAllByUserTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        userRepository.save(user)

        val article1 = Article(title = "title1", content = "content1", user = user)
        val article2 = Article(title = "title2", content = "content2", user = user)
        articleRepository.saveAll(listOf(article1, article2))

        // when
        val articles = articleRepository.findAllByUser(user)

        // then
        assert(articles.size == 2)
        assert(articles.contains(article1))
        assert(articles.contains(article2))
    }
}

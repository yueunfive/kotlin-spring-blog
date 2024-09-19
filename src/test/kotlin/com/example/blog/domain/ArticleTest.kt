package com.example.blog.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class ArticleTest {

    @Test
    @DisplayName("게시글 생성")
    fun articleTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")

        // when
        val article = Article(title = "title", content = "content", user = user)

        // then
        assertEquals("title", article.title)
        assertEquals("content", article.content)
        assertEquals(user, article.user)
    }

    @Test
    @DisplayName("게시글 수정")
    fun editArticleTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        val article = Article(title = "title", content = "content", user = user)

        // when
        article.edit("updated title", "updated content")

        // then
        assertEquals("updated title", article.title)
        assertEquals("updated content", article.content)
    }
}
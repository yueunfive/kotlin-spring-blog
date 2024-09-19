package com.example.blog.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class CommentTest {

    @Test
    @DisplayName("댓글 생성")
    fun commentTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        val article = Article(title = "title", content = "content", user = user)

        // when
        val comment = Comment(content = "comment", user = user, article = article)

        // then
        assertEquals("comment", comment.content)
        assertEquals(user, comment.user)
        assertEquals(article, comment.article)
    }

    @Test
    @DisplayName("댓글 수정")
    fun editCommentTest() {
        // given
        val user = User(email = "test@example.com", username = "username", password = "password")
        val article = Article(title = "title", content = "content", user = user)
        val comment = Comment(content = "comment", user = user, article = article)

        // when
        comment.editContent("updated comment")

        // then
        assertEquals("updated comment", comment.content)
    }
}

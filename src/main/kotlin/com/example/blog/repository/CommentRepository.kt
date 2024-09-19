package com.example.blog.repository

import com.example.blog.domain.Article
import com.example.blog.domain.Comment
import com.example.blog.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByArticle(article: Article): List<Comment>

    fun findAllByUser(user: User): List<Comment>
}

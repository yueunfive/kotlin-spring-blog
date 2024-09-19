package com.example.blog.repository

import com.example.blog.domain.Article
import com.example.blog.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Long> {
    fun findAllByUser(user: User): List<Article>
}

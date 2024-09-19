package com.example.blog.dto.response

data class PostArticleResponse(
    val articleId: Long,
    val email: String,
    val title: String,
    val content: String
)
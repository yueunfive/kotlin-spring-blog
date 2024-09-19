package com.example.blog.dto.response

data class EditArticleResponse(
    val articleId: Long,
    val email: String,
    val title: String,
    val content: String
)
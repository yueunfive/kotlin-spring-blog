package com.example.blog.dto.response

data class PostCommentResponse(
    val commentId: Long,
    val email: String,
    val content: String
)
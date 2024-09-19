package com.example.blog.dto.response

data class EditCommentResponse(
    val commentId: Long,
    val email: String,
    val content: String
)

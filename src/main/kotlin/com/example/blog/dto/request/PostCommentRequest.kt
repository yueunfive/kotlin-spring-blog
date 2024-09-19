package com.example.blog.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PostCommentRequest(
    @field:NotNull
    val articleId: Long,

    @field:NotBlank
    val content: String
)
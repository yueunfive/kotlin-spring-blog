package com.example.blog.dto.request

import jakarta.validation.constraints.NotBlank

data class EditCommentRequest(
    @field:NotBlank
    val content: String
)


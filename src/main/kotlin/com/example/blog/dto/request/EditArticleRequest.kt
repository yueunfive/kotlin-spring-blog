package com.example.blog.dto.request

import jakarta.validation.constraints.NotBlank

data class EditArticleRequest(
    @field:NotBlank
    val title: String,

    @field:NotBlank
    val content: String
)
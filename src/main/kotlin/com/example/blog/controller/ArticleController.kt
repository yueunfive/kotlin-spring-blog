package com.example.blog.controller

import com.example.blog.dto.request.EditArticleRequest
import com.example.blog.dto.request.PostArticleRequest
import com.example.blog.dto.response.EditArticleResponse
import com.example.blog.dto.response.PostArticleResponse
import com.example.blog.service.ArticleService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/articles")
@Validated
class ArticleController(
    private val articleService: ArticleService
) {

    // 게시글 작성
    @PostMapping
    fun post(
        principal: Principal,
        @RequestBody @Valid request: PostArticleRequest
    ): ResponseEntity<PostArticleResponse> {
        val userId = principal.name.toLong()
        val response = articleService.post(userId, request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    // 게시글 수정
    @PatchMapping("/{articleId}")
    fun edit(
        principal: Principal,
        @PathVariable articleId: Long,
        @RequestBody @Valid request: EditArticleRequest
    ): ResponseEntity<EditArticleResponse> {
        val userId = principal.name.toLong()
        val response = articleService.edit(userId, articleId, request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    // 게시글 삭제
    @DeleteMapping("/{articleId}")
    fun delete(principal: Principal, @PathVariable articleId: Long): ResponseEntity<Void> {
        val userId = principal.name.toLong()
        articleService.delete(userId, articleId)
        return ResponseEntity(HttpStatus.OK)
    }
}

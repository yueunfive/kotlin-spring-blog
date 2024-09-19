package com.example.blog.controller

import com.example.blog.dto.request.EditCommentRequest
import com.example.blog.dto.request.PostCommentRequest
import com.example.blog.dto.response.EditCommentResponse
import com.example.blog.dto.response.PostCommentResponse
import com.example.blog.service.CommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/comments")
@Validated
class CommentController(
    private val commentService: CommentService
) {

    // 댓글 작성
    @PostMapping
    fun post(
        principal: Principal,
        @RequestBody @Valid request: PostCommentRequest
    ): ResponseEntity<PostCommentResponse> {
        val userId = principal.name.toLong()
        val response = commentService.post(userId, request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    fun edit(
        principal: Principal,
        @PathVariable commentId: Long,
        @RequestBody @Valid request: EditCommentRequest
    ): ResponseEntity<EditCommentResponse> {
        val userId = principal.name.toLong()
        val response = commentService.edit(userId, commentId, request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    fun delete(principal: Principal, @PathVariable commentId: Long): ResponseEntity<Void> {
        val userId = principal.name.toLong()
        commentService.delete(userId, commentId)
        return ResponseEntity(HttpStatus.OK)
    }
}

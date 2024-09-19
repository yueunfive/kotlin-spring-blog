package com.example.blog.service

import com.example.blog.common.exception.CustomException
import com.example.blog.domain.Comment
import com.example.blog.repository.ArticleRepository
import com.example.blog.repository.CommentRepository
import com.example.blog.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.blog.common.exception.ErrorCode.*
import com.example.blog.dto.request.EditCommentRequest
import com.example.blog.dto.request.PostCommentRequest
import com.example.blog.dto.response.EditCommentResponse
import com.example.blog.dto.response.PostCommentResponse
import org.springframework.data.repository.findByIdOrNull

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
) {
    /**
     * 댓글 작성
     */
    @Transactional
    fun post(userId: Long, request: PostCommentRequest): PostCommentResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw CustomException(USER_NOT_FOUND)
        val article = articleRepository.findByIdOrNull(request.articleId) ?: throw CustomException(ARTICLE_NOT_FOUND)

        val newComment = Comment(
            content = request.content,
            user = user,
            article = article
        )

        commentRepository.save(newComment)

        return PostCommentResponse(
            commentId = newComment.id!!,
            email = user.email,
            content = newComment.content
        )
    }

    /**
     * 댓글 수정
     */
    @Transactional
    fun edit(userId: Long, commentId: Long, request: EditCommentRequest): EditCommentResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw CustomException(USER_NOT_FOUND)
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw CustomException(COMMENT_NOT_FOUND)

        if (comment.user != user) throw CustomException(NO_PERMISSION_FOR_COMMENT)

        comment.editContent(request.content)

        return EditCommentResponse(
            commentId = commentId,
            email = user.email,
            content = comment.content
        )
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    fun delete(userId: Long, commentId: Long) {
        val user = userRepository.findByIdOrNull(userId) ?: throw CustomException(USER_NOT_FOUND)
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw CustomException(COMMENT_NOT_FOUND)

        if (comment.user != user) throw CustomException(NO_PERMISSION_FOR_COMMENT)

        commentRepository.delete(comment)
    }
}

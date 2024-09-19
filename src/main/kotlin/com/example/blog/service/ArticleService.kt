package com.example.blog.service

import com.example.blog.common.exception.CustomException
import com.example.blog.common.exception.ErrorCode.*
import com.example.blog.domain.Article
import com.example.blog.dto.request.EditArticleRequest
import com.example.blog.dto.request.PostArticleRequest
import com.example.blog.dto.response.EditArticleResponse
import com.example.blog.dto.response.PostArticleResponse
import com.example.blog.repository.ArticleRepository
import com.example.blog.repository.CommentRepository
import com.example.blog.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) {

    /**
     * 게시글 작성
     */
    @Transactional
    fun post(userId: Long, request: PostArticleRequest): PostArticleResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw CustomException(USER_NOT_FOUND)

        val newArticle = Article(
            title = request.title,
            content = request.content,
            user = user
        )

        articleRepository.save(newArticle)

        return PostArticleResponse(
            articleId = newArticle.id!!,
            email = user.email,
            title = newArticle.title,
            content = newArticle.content
        )
    }

    /**
     * 게시글 수정
     */
    @Transactional
    fun edit(userId: Long, articleId: Long, request: EditArticleRequest): EditArticleResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw CustomException(USER_NOT_FOUND)
        val article = articleRepository.findByIdOrNull(articleId) ?: throw CustomException(ARTICLE_NOT_FOUND)

        if (article.user != user) throw CustomException(NO_PERMISSION_FOR_ARTICLE)

        article.edit(request.title, request.content)

        return EditArticleResponse(
            articleId = articleId,
            email = user.email,
            title = article.title,
            content = article.content
        )
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    fun delete(userId: Long, articleId: Long) {
        val user = userRepository.findByIdOrNull(userId) ?: throw CustomException(USER_NOT_FOUND)
        val article = articleRepository.findByIdOrNull(articleId) ?: throw CustomException(ARTICLE_NOT_FOUND)

        if (article.user != user) throw CustomException(NO_PERMISSION_FOR_ARTICLE)

        val commentsToDelete = commentRepository.findAllByArticle(article)

        commentRepository.deleteAll(commentsToDelete)

        articleRepository.delete(article)
    }
}

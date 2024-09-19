package com.example.blog.controller

import com.example.blog.dto.request.EditArticleRequest
import com.example.blog.dto.request.PostArticleRequest
import com.example.blog.dto.response.EditArticleResponse
import com.example.blog.dto.response.PostArticleResponse
import com.example.blog.service.ArticleService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.security.Principal

@ActiveProfiles("test")
@WebMvcTest(ArticleController::class)
@AutoConfigureMockMvc(addFilters = false)
class ArticleControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockBean private val articleService: ArticleService
) {
    @Test
    @DisplayName("게시글 작성 성공")
    fun postArticleTest() {
        // given
        val request = PostArticleRequest(title = "title", content = "content")
        val response = PostArticleResponse(articleId = 1L, email = "test@example.com", title = "title", content = "content")

        `when`(articleService.post(1L, request)).thenReturn(response)

        val principal = Mockito.mock(Principal::class.java)
        `when`(principal.name).thenReturn("1")

        // when & then
        mockMvc.perform(
            post("/articles")
                .principal(principal)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.articleId").value(1L))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.title").value("title"))
            .andExpect(jsonPath("$.content").value("content"))
    }

    @Test
    @DisplayName("게시글 수정 성공")
    fun editArticleTest() {
        // given
        val articleId = 1L
        val request = EditArticleRequest(title = "updated title", content = "updated content")
        val response = EditArticleResponse(articleId = articleId, email = "test@example.com", title = "updated title", content = "updated content")

        `when`(articleService.edit(1L, articleId, request)).thenReturn(response)

        val principal = mock(Principal::class.java)
        `when`(principal.name).thenReturn("1")

        // when & then
        mockMvc.perform(
            patch("/articles/$articleId")
                .principal(principal)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.articleId").value(articleId))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.title").value("updated title"))
            .andExpect(jsonPath("$.content").value("updated content"))
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    fun deleteArticleTest() {
        // given
        val articleId = 1L

        val principal = mock(Principal::class.java)
        `when`(principal.name).thenReturn("1")

        // when & then
        mockMvc.perform(
            delete("/articles/$articleId")
                .principal(principal)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }
}
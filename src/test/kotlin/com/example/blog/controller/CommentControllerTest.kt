package com.example.blog.controller

import com.example.blog.dto.request.EditCommentRequest
import com.example.blog.dto.request.PostCommentRequest
import com.example.blog.dto.response.EditCommentResponse
import com.example.blog.dto.response.PostCommentResponse
import com.example.blog.service.CommentService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
@WebMvcTest(CommentController::class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockBean private val commentService: CommentService
) {
    @Test
    @DisplayName("댓글 작성 성공")
    fun postCommentTest() {
        // given
        val request = PostCommentRequest(articleId = 1L, content = "content")
        val response = PostCommentResponse(commentId = 1L, email = "test@example.com", content ="content")

        `when`(commentService.post(1L, request)).thenReturn(response)

        val principal = mock(Principal::class.java)
        `when`(principal.name).thenReturn("1")

        // when & then
        mockMvc.perform(
            post("/comments")
                .principal(principal)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.commentId").value(1L))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.content").value("content"))
    }

    @Test
    @DisplayName("댓글 수정 성공")
    fun editCommentTest() {
        // given
        val commentId = 1L
        val request = EditCommentRequest(content = "updated content")
        val response = EditCommentResponse(commentId = commentId, email = "test@example.com", content = "updated content")

        `when`(commentService.edit(1L, commentId, request)).thenReturn(response)

        val principal = mock(Principal::class.java)
        `when`(principal.name).thenReturn("1")

        // when & then
        mockMvc.perform(
            patch("/comments/$commentId")
                .principal(principal)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.commentId").value(commentId))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.content").value("updated content"))
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    fun deleteCommentTest() {
        // given
        val commentId = 1L

        val principal = mock(Principal::class.java)
        `when`(principal.name).thenReturn("1")

        // when & then
        mockMvc.perform(
            delete("/comments/$commentId")
                .principal(principal)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }
}

package com.example.blog.controller

import com.example.blog.dto.request.LoginRequest
import com.example.blog.dto.request.SignupRequest
import com.example.blog.dto.response.SignupResponse
import com.example.blog.service.UserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/users")
@Validated
class UserController(
    private val userService: UserService
) {

    // 회원가입
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: SignupRequest): ResponseEntity<SignupResponse> {
        val response = userService.signup(request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    // 로그인
    @PostMapping("/login")
    fun login(@RequestBody @Valid request: LoginRequest, response: HttpServletResponse): ResponseEntity<Void> {
        userService.login(request, response)
        return ResponseEntity(HttpStatus.OK)
    }

    // 회원탈퇴
    @DeleteMapping("/unlink")
    fun unlink(principal: Principal): ResponseEntity<Void> {
        val userId = principal.name.toLong()
        userService.unlink(userId)
        return ResponseEntity(HttpStatus.OK)
    }
}

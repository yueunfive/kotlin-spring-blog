package com.example.blog.common.jwt

import com.example.blog.common.exception.CustomException
import com.example.blog.common.exception.ErrorCode
import com.example.blog.domain.User
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Duration
import java.util.*

@Service
class TokenProvider(
    @Value("\${spring.jwt.secret}")
    private val secretKeyString: String
) {
    private val log = LoggerFactory.getLogger(TokenProvider::class.java)

    private fun getSecretKey(): Key {
        return Keys.hmacShaKeyFor(secretKeyString.toByteArray())
    }

    fun generateToken(user: User, expiredAt: Duration): String {
        val now = Date()
        return makeToken(Date(now.time + expiredAt.toMillis()), user)
    }

    /**
     * JWT 토큰 생성
     *
     * @param expiry 토큰의 만료 시간
     * @param user 회원 정보
     * @return 생성된 토큰
     */
    private fun makeToken(expiry: Date, user: User): String {
        val now = Date()
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setSubject(user.id.toString())
            .claim("id", user.id)
            .signWith(getSecretKey(), SignatureAlgorithm.HS256) // HS256 방식으로 암호화
            .compact()
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param token 검증할 JWT 토큰
     */
    fun validateToken(token: String) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
        } catch (e: ExpiredJwtException) {
            log.info("만료된 토큰: {}", e.message)
            throw CustomException(ErrorCode.TOKEN_EXPIRED)
        } catch (e: Exception) {
            log.info("유효하지 않은 토큰: {}", e.message)
            throw CustomException(ErrorCode.TOKEN_INVALID)
        }
    }

    /**
     * 토큰 기반 인증 정보 조회
     *
     * @param token 인증된 회원의 토큰
     * @return 인증 정보를 담은 Authentication 객체
     */
    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val authorities = setOf(SimpleGrantedAuthority("ROLE_USER"))

        return UsernamePasswordAuthenticationToken(
            org.springframework.security.core.userdetails.User(claims.subject, "", authorities),
            token,
            authorities
        )
    }

    /**
     * 토큰 기반 클레임 조회
     *
     * @param token JWT 토큰
     * @return 클레임 객체
     */
    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSecretKey())
            .build()
            .parseClaimsJws(token)
            .body
    }
}

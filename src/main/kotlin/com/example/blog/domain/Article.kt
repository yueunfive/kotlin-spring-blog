package com.example.blog.domain

import com.example.blog.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User
) : BaseEntity() {

    fun edit(title: String, content: String) {
        this.title = title
        this.content = content
    }
}

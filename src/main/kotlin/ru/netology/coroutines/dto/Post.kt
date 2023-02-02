package ru.netology.coroutines.dto

import ru.netology.coroutines.dto.PostWithComments.Companion.actualTime

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0
) {
    override fun toString(): String {
        return "Post #$id: $author, ${actualTime(published)}\n   $content\n"
    }
}
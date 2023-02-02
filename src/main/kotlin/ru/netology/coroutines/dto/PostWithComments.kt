package ru.netology.coroutines.dto

import java.text.SimpleDateFormat
import java.util.*

data class PostWithComments(
    val post: Post,
    val comments: List<Comment>
) {
    companion object {
        val actualTime = { now: Long ->
            SimpleDateFormat("dd MMMM, H:mm", Locale.US).format(Date(now))
        }
    }
    override fun toString(): String {
        return "\n$post$comments"
    }
}
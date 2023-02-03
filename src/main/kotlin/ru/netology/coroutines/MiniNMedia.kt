package ru.netology.coroutines

import kotlinx.coroutines.*
import okio.IOException
import ru.netology.coroutines.dto.PostWithComments
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

private val actualTime = { now: Long ->
    SimpleDateFormat("dd MMMM, H:mm", Locale.US).format(Date(now))
}

fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            val posts = HandMadeSuspend.getPosts()
            val postsWithComments = posts.map {
                async {
                    PostWithComments(it, HandMadeSuspend.getComments(it.id))
                }
            }
                .awaitAll()
            val postsToString = postsWithComments.map {
                async {
                        showPosts(it)
                }
            }
                .awaitAll()
            println(postsToString)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    Thread.sleep(1_000)
}

private suspend fun showPosts(item: PostWithComments) =
    "\nPost #${item.post.id}: ${HandMadeSuspend.getAuthorById(item.post.authorId).name}, ${actualTime(item.post.published)}\n" +
            "${item.comments.map {
                CoroutineScope(EmptyCoroutineContext).async {
                    "\n  Comment #${it.id}: ${HandMadeSuspend.getAuthorById(it.authorId).name}, ${actualTime(it.published)}"
                }
            }
                .awaitAll()
                .ifEmpty { "<no comments>" }}"
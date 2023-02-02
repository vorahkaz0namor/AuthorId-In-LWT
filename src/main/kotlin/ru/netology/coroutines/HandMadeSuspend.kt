package ru.netology.coroutines

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.coroutines.dto.Comment
import ru.netology.coroutines.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object HandMadeSuspend {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private const val BASE_URL = "http://192.168.31.16:9999"
    private const val API_SUFFIX = "/api"
    private const val POSTS_PATH = "${API_SUFFIX}/posts"
    private val commentsPath = { postId: Long -> "${POSTS_PATH}/$postId/comments" }
    private val gson = Gson()
    private val postsTypeToken = object : TypeToken<List<Post>>() {}
    private val commentsTypeToken = object : TypeToken<List<Comment>>() {}

    private suspend fun makeRequest(url: String): Response =
        suspendCoroutine { continuation ->
            client.newCall(
                Request.Builder()
                    .url(url)
                    .build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
        }

    // Организуем generic, который будет парсить ответ
    private suspend fun <T> parseResponse(url: String, typeToken: TypeToken<T>): T {
        val response = makeRequest(url)
        val unparsedBody: T = gson.fromJson(requireNotNull(response.body).string(), typeToken.type)
        // Поскольку парсинг может быть достаточно ресурсоемким, то неплохо бы
        // его обернуть следующим образом:
        return withContext(Dispatchers.Default) {
            // т.е. переключить CoroutineContext на другой Dispatcher
            unparsedBody
        }
    }

    suspend fun getPosts(): List<Post> =
        parseResponse("${BASE_URL}${POSTS_PATH}", postsTypeToken)

    suspend fun getComments(postId: Long): List<Comment> =
        parseResponse("${BASE_URL}${commentsPath(postId)}", commentsTypeToken)
}
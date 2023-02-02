package ru.netology.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okio.IOException
import ru.netology.coroutines.dto.PostWithComments
import kotlin.coroutines.EmptyCoroutineContext

fun main() {
    val postsHolder = HandMadeSuspend

    CoroutineScope(EmptyCoroutineContext).launch {
        // Если есть вероятность, что один из запросов может завершиться
        // ошибкой, тогда можно использовать конструкцию try-catch
        try {
            val posts = postsHolder.getPosts()
            // Чтобы запараллелить запросы комментариев, можно использовать
            // функцию async, которая возвращает объекты типа Deferred<T>,
            // являющиеся чем-то наподобие отложенного вызова.
            // В итоге результаты приходят от всех Deferred-объектов практически
            // одновременно.
            val result = posts.map {
                async {
                    PostWithComments(it, postsHolder.getComments(it.id))
                }
            }
                // Чтобы дождаться результатов от всех async, нужно указать await
                .awaitAll()
            println(result)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    Thread.sleep(3_000)
}
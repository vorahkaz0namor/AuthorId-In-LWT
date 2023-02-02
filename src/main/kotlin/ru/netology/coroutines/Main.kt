package ru.netology.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

fun main() {
    // Для создания корутин используются Coroutine Builders
    // Вот одна из них
    runBlocking {
        printString("runBlocking")
    }
    // Ключевое слово suspend помечает функцию (лямбду), выполнение которой может быть
    // приостановлено без блокирования текущего потока путем вызова других suspend-функций (-лямбд).
    // Suspension points (точки подвеса) - это точки, в которых может быть приостановлено выполнение
    // корутины. Kotlin не сам каким-то магическим способом ставит на паузу выполнение корутины,
    // а делает это в специально определенных точках.
    // Например, это может быть вызов suspend-функции.
    // Другой способ создания корутин - создание области действия корутин
    // в виде CoroutineScope(), и уже из этой области запускать корутину
    // При этом текущий поток не блокируется
    // Для scope'а обязательно надо указать контекст CoroutineContext
    // (это по сути набор различных настроек работы корутины)
    CoroutineScope(EmptyCoroutineContext + Dispatchers.Default).launch {
        printString("Default")
    }
    // Dispatcher - это специальный класс, который отвечает за распределение корутин
    // между потоками
    // Dispatcher.Default содержит пул потоков, который равен количеству ядер процессора
    CoroutineScope(EmptyCoroutineContext + Dispatchers.IO).launch {
        printString("IO")
    }
    CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined).launch {
        printString("Unconfined")
    }
    // Существует возможность в рамках одного scope'а запустить несколько корутин
    // Т.о. по сути внутри одной корутины может запускаться другая корутина
    CoroutineScope(EmptyCoroutineContext).apply {
        launch(Dispatchers.Default) {
            printString("Default-apply")
            launch(Dispatchers.IO) { printString("IO-in-Default-apply") }
        }
        launch(Dispatchers.IO) { printString("IO-apply") }
        launch(Dispatchers.Unconfined) { printString("Unconfined-apply") }
    }
    // Continuation определяет состояние приостановленной suspend-функции в точке подвеса,
    // т.е. по сути содержит информацию о том, что еще надо сделать.
    // За исполнение отвечает CoroutineScheduler, который и реализует интерфейс Executor.
    CoroutineScope(EmptyCoroutineContext).async {
        printString("Default-async")
    }
    Thread.sleep(500)
}

private fun printString(str: String) {
    println("$str => ${Thread.currentThread().name} | ${System.currentTimeMillis()}")
}
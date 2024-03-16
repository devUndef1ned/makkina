package io.github.devundef1ned.makkina.example

import java.util.concurrent.Executors

fun main() {
    val door = Door()

    door.open()

    // If we do door close it won't happen cause opening is in process
    door.close()

    // But if we wait until operation done and state comes to open we can do it
    Thread.sleep(1100)
    door.close()
}
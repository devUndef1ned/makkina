package io.github.devundef1ned.makkina

typealias SideEffect<STATE> = (STATE, STATE) -> Unit
typealias StateFunction<STATE, EVENT> = (STATE, EVENT) -> STATE
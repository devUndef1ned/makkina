package com.devundef1ned

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FiniteStateMachine<STATE : Any, EVENT : Any> internal constructor(
    initialState: STATE,
    private val defaultStateHandler: (STATE, EVENT) -> STATE,
    ) {

    private val mutex = Mutex()

    private var _state: STATE = initialState

    /**
     * Handles event which can cause transition if this state machine has described event handling for this case.
     * Logic is executed in blocking way.
     */
    fun sendEvent(event: EVENT) = runBlocking {
        mutex.withLockIfNot {
            val currentState = state()
            val newState = defaultStateHandler(currentState, event)

            _state = newState
        }
    }

    /**
     * Returns current state of the machine. The invocation can block a thread.
     */
    fun state(): STATE = runBlocking { mutex.withLockIfNot { _state } }

    companion object {
        /**
         * Runs [lambda] if mutex is locked, otherwise it locks and runs.
         */
        private suspend inline fun <T> Mutex.withLockIfNot(lambda: () -> T): T = if (isLocked) {
            lambda()
        } else {
            withLock { lambda() }
        }
    }
}
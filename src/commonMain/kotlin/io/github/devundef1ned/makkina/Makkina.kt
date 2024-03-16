package io.github.devundef1ned.makkina

import io.github.devundef1ned.makkina.dsl.MakkinaBuilder
import io.github.devundef1ned.makkina.dsl.TransitionKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

class Makkina<STATE : Any, EVENT : Any> internal constructor(
    initialState: STATE,
    private val defaultStateHandler: (STATE, EVENT) -> STATE,
    private val stateHandlers: Map<KClass<out STATE>, Map<KClass<out EVENT>, (STATE, EVENT) -> (STATE)>>,
    private val sideEffects: Map<TransitionKey<STATE>, SideEffect<STATE>>,
) {

    private val mutex = Mutex()

    private var _state: STATE = initialState

    /**
     * Handles event which can cause transition if this state machine has described event handling for this case.
     * Side effects are also being run here. Logic is executed in blocking way.
     */
    fun sendEvent(event: EVENT) = runBlocking {
        mutex.withLockIfNot {
            val currentState = state()

            val newState = stateHandlers[currentState::class]?.get(event::class)?.let { eventHandler ->
                eventHandler(_state, event)
            } ?: defaultStateHandler(currentState, event)

            sideEffects.forEach { (key, sideEffect) ->
                if (key.isApplicable(currentState, newState)) {
                    sideEffect(currentState, newState)
                }
            }

            _state = newState
        }
    }

    /**
     * Keeps FSM in the current state by returning its value.
     */
    fun stay(): STATE = state()

    /**
     * Returns the current state of the machine. The invocation can block a thread.
     */
    fun state(): STATE = runBlocking { mutex.withLockIfNot { _state } }

    /**
     * Forces FSM to go to the [state]. Dangerous method. Consider avoid this method outside the machine.
     * It can be used to force transition to the [state] as result of some asynchronous computation when current state
     * isn't important. E.g. you submit a network request as side effect and in the outcome of the request you might
     * want to make a transition to a new specific state.
     */
    fun transitionTo(state: STATE) = runBlocking {
        mutex.withLockIfNot {
            val currentState = _state
            sideEffects.forEach { (key, sideEffect) ->
                if (key.isApplicable(currentState, state)) {
                    sideEffect(currentState, state)
                }
            }

            _state = state
        }
    }

    companion object {
        /**
         * Runs [lambda] if mutex is locked, otherwise it locks and runs.
         */
        private suspend inline fun <T> Mutex.withLockIfNot(lambda: () -> T): T = if (isLocked) {
            lambda()
        } else {
            withLock { lambda() }
        }

        /**
         * Entry-point for DSL scheme description.
         *
         * @param builder High-level builder lambda that describes the FSM.
         */
        operator fun <STATE : Any, EVENT : Any> invoke(
            builder: MakkinaBuilder<STATE, EVENT>.() -> Unit
        ): Makkina<STATE, EVENT> {
            return MakkinaBuilder<STATE, EVENT>().apply(builder).build()
        }
    }
}
package com.devundef1ned

import kotlin.reflect.KClass

/**
 * Sealed interface hierarchy marking possible transitions.
 */
@PublishedApi
internal sealed interface TransitionKey<STATE : Any> {

    /**
     * Returns true if the current transition is applicable for the transition from [fromState] to [toState].
     */
    fun isApplicable(fromState: STATE, toState: STATE): Boolean

    /**
     * Any transition from [fromState].
     */
    data class FromState<STATE : Any>(val fromState: KClass<out STATE>) : TransitionKey<STATE> {
        override fun isApplicable(fromState: STATE, toState: STATE): Boolean {
            return fromState::class == this.fromState
        }
    }

    /**
     * Any transition to [toState].
     */
    data class ToState<STATE : Any>(val toState: KClass<out STATE>) : TransitionKey<STATE> {
        override fun isApplicable(fromState: STATE, toState: STATE): Boolean {
            return toState::class == this.toState
        }
    }

    /**
     * Any transition from [fromState] to [toState].
     */
    data class FromStateToState<STATE : Any>(
        val fromState: KClass<out STATE>,
        val toState: KClass<out STATE>,
    ) : TransitionKey<STATE> {
        override fun isApplicable(fromState: STATE, toState: STATE): Boolean {
            return fromState::class == this.fromState && toState::class == this.toState
        }
    }

    /**
     * Just any transition.
     */
    class AnyTransition<STATE : Any> : TransitionKey<STATE> {
        override fun isApplicable(fromState: STATE, toState: STATE): Boolean = true
    }
}
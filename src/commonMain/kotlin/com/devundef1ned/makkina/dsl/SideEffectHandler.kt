package com.devundef1ned.makkina.dsl

import com.devundef1ned.makkina.SideEffect

/**
 * Builder class for Makkina DSL to describe side effects.
 */
@Suppress("UNCHECKED_CAST")
class SideEffectHandler<STATE : Any> {
    @PublishedApi
    internal val sideEffectMap: MutableMap<TransitionKey<STATE>, SideEffect<STATE>> = mutableMapOf()

    /**
     * Describes a side effect on a transition from the [FROM_STATE] state to the [TO_STATE] state.
     *
     * @param sideEffect Lambda of the side effect which consumes previous and next states.
     */
    inline fun <reified FROM_STATE : STATE, reified TO_STATE : STATE> fromTo(
        noinline sideEffect: (FROM_STATE, TO_STATE) -> Unit
    ) {
        val key = TransitionKey.FromStateToState(FROM_STATE::class, TO_STATE::class)
        sideEffectMap[key] = sideEffect as SideEffect<STATE>
    }

    /**
     * Describes a side effect on any transition from the [FROM_STATE] state to any state.
     *
     * @param sideEffect Lambda of the side effect which consumes previous and next states.
     */
    inline fun <reified FROM_STATE : STATE> from(noinline sideEffect: (FROM_STATE, STATE) -> Unit) {
        val key = TransitionKey.FromState(FROM_STATE::class) as TransitionKey<STATE>
        sideEffectMap[key] = sideEffect as SideEffect<STATE>
    }

    /**
     * Describes a side effect on any transition from any state to the [TO_STATE] state.
     *
     * @param sideEffect Lambda of the side effect which consumes previous and next states.
     */
    inline fun <reified TO_STATE : STATE> to(noinline sideEffect: (STATE, TO_STATE) -> Unit) {
        val key = TransitionKey.ToState(TO_STATE::class) as TransitionKey<STATE>
        sideEffectMap[key] = sideEffect as SideEffect<STATE>
    }

    /**
     * Describes a side effect on any transition.
     *
     * @param sideEffect Lambda of the side effect which consumes previous and next states.
     */
    fun onAny(sideEffect: SideEffect<STATE>) {
        val key = TransitionKey.AnyTransition<STATE>()
        sideEffectMap[key] = sideEffect
    }
}
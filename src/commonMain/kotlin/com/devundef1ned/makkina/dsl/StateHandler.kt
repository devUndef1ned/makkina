package com.devundef1ned.makkina.dsl

import com.devundef1ned.makkina.StateFunction
import kotlin.reflect.KClass

/**
 * Represents a set of event handlers for some particular state [S].
 */
class StateHandler<STATE : Any, EVENT : Any, S : STATE> {

    @PublishedApi
    internal val eventHandlers: MutableMap<KClass<out EVENT>, StateFunction<STATE, EVENT>> = mutableMapOf()

    /**
     * DSL builder method to describe handling of an event of the [E] type.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified E: EVENT> onEvent(noinline eventHandler: (S, E) -> STATE) {
        eventHandlers += E::class to eventHandler as StateFunction<STATE, EVENT>
    }

    @PublishedApi
    internal fun get(): Map<KClass<out EVENT>, StateFunction<STATE, EVENT>> = eventHandlers
}
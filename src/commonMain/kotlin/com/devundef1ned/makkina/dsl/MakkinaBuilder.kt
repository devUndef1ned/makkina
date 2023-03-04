package com.devundef1ned.makkina.dsl

import kotlin.reflect.KClass
import com.devundef1ned.makkina.Makkina
import com.devundef1ned.makkina.StateFunction

/**
 * DSL builder for [Makkina].
 */
class MakkinaBuilder<STATE : Any, EVENT : Any>  {

    private lateinit var _initialState: STATE
    // By default, state handler return the current value.
    private var defaultStateHandler: StateFunction<STATE, EVENT> = { state, _ -> state }
    @PublishedApi
    internal val stateHandlerMap: MutableMap<KClass<out STATE>, Map<KClass<out EVENT>, StateFunction<STATE, EVENT>>> =
        mutableMapOf()
    @PublishedApi
    internal var sideEffectHandler: SideEffectHandler<STATE> = SideEffectHandler()

    /**
     * Defines the initial state for the machine.
     */
    fun initialState(state: STATE) {
        _initialState = state
    }

    /**
     * Defines a default handler in the case if for such event this state there is no handler. E.g. the machine is in
     * the S1 state, it receives an event of the E1 type and there is no description how to handle this case, then
     * default handler does its job. You can throw an exception in this case or log it and keep current state.
     */
    fun defaultStateHandler(stateHandler: StateFunction<STATE, EVENT>) {
        this.defaultStateHandler = stateHandler
    }

    /**
    * DSL builder method to describe the [S] state behavior.
    */
    inline fun <reified S : STATE> state(stateHandlerBuilder: StateHandler<STATE, EVENT, S>.() -> Unit) {
        this.stateHandlerMap[S::class] = StateHandler<STATE, EVENT, S>().apply(stateHandlerBuilder).get()
    }

    /**
     * DSL builder method to describe the set of SideEffects.
     */
    fun transitions(transitionBuilder: SideEffectHandler<STATE>.() -> Unit) {
        sideEffectHandler = sideEffectHandler.apply(transitionBuilder)
    }
}
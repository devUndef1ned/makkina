package com.devundef1ned.makkina

import com.devundef1ned.makkina.dsl.SideEffectHandler
import kotlin.test.Test
import kotlin.test.assertEquals

class MakkinaTest {

    @Test
    fun `Given Idle state When handle Put Data event Should move to Data state`() {
        val makkina = CreateMakkina().create()
        
        makkina.sendEvent(Event.PutData("Some value"))
        
        assertEquals(makkina.state(), State.Data("Some value"))
    }


    @Test
    fun `onAny side effect should work correctly`() {
        var previousState: State? = null
        var currentState: State? = null
        val makkina = CreateMakkina()
            .sideEffect {
                onAny { fromState, toState ->
                    previousState = fromState
                    currentState = toState
                }
            }
            .create()

        makkina.sendEvent(Event.PutData("First value"))
        makkina.sendEvent(Event.PutData("Second value"))

        assertEquals(previousState, State.Data("First value"))
        assertEquals(currentState, State.Data("Second value"))
    }

    @Test
    fun `from side effect should work correctly`() {
        var stateVar: State? = null
        val makkina = CreateMakkina()
            .sideEffect {
                from<State.Initial> { _, newState ->
                    stateVar = newState
                }
            }.create()

        makkina.sendEvent(Event.PutData("Some value"))

        assertEquals(stateVar, State.Data("Some value"))
    }

    @Test
    fun `to side effect should work correctly`() {
        var stateVar: State? = null
        val makkina = CreateMakkina()
            .sideEffect {
                to<State.Initial> { fromState, _ ->
                    stateVar = fromState
                }
            }.create()
        makkina.sendEvent(Event.PutData("Some value"))

        makkina.sendEvent(Event.Clear)

        assertEquals(stateVar, State.Data("Some value"))
    }

    @Test
    fun `From to side effect should work correctly`() {
        var stateVar: State? = null
        val makkina = CreateMakkina()
            .sideEffect {
                fromTo<State.Data, State.Initial> { fromState, _ ->
                    stateVar = fromState
                }
            }.create()
        makkina.sendEvent(Event.PutData("Some value"))

        makkina.sendEvent(Event.Clear)

        assertEquals(stateVar, State.Data("Some value"))
    }

    @Test
    fun `Two side effects should work`() {
        var firstValue = 0
        var secondValue = 0
        val makkina = CreateMakkina()
            .sideEffect {
                onAny { _, _ ->
                    firstValue = 1
                }
                onAny { _, _ ->
                    secondValue = 2
                }
            }.create()

        makkina.sendEvent(Event.PutData("Some value"))

        assertEquals(1, firstValue)
        assertEquals(2, secondValue)
    }

    @Test
    fun `Transition should work`() {
        val makkina = CreateMakkina().create()

        makkina.transitionTo(State.Data("Some data"))

        assertEquals(State.Data("Some data"), makkina.state())
    }

    @Test
    fun `Given side effect When forcing transition Should handle side effects`() {
        var someValue = 0
        val makkina = CreateMakkina()
            .sideEffect {
                onAny { _, _ ->
                    someValue = 1
                }
            }.create()

        makkina.transitionTo(State.Data("Some value"))

        assertEquals(1, someValue)
    }
}

private class CreateMakkina {
    private var sideEffect: SideEffectHandler<State>.() -> Unit = {  }

    fun sideEffect(sideEffect: SideEffectHandler<State>.() -> Unit) = apply {
        this.sideEffect = sideEffect
    }

    fun create(): Makkina<State, Event> = Makkina {
        initialState(State.Initial)

        state<State.Initial> {
            onEvent<Event.PutData> { _, putData -> State.Data(putData.data) }
        }

        state<State.Data> {
            onEvent<Event.PutData> { _, putData -> State.Data(putData.data) }
            onEvent<Event.Clear> { _, _ -> State.Initial }
        }

        transitions(sideEffect)
    }
}

sealed interface State {
    object Initial : State
    data class Data(val data: String) : State
}

sealed interface Event {
    class PutData(val data: String) : Event
    object Clear : Event
}
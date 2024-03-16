package io.github.devundef1ned.makkina.example

import io.github.devundef1ned.makkina.Makkina
import java.util.concurrent.Executors

class Door {

    private val executor = Executors.newSingleThreadExecutor()

    private val makkina = Makkina {
        initialState(DoorState.Closed)

        state<DoorState.Closed> {
            onEvent<DoorEvent.OpenEvent> { _, _ -> doOpenDoor() }
        }

        state<DoorState.Open> {
            onEvent<DoorEvent.CloseEvent> { _, _ -> doCloseDoor() }
        }

        transitions {
            onAny { fromState, toState ->
                println("Door is coming from ${fromState::class.simpleName} to ${toState::class.simpleName}")
            }
        }
    }

    private fun doCloseDoor(): DoorState.Closing {
        executor.execute {
            // Emulate long running task
            Thread.sleep(1000)
            makkina.transitionTo(DoorState.Closed)
        }

        return DoorState.Closing
    }

    private fun doOpenDoor(): DoorState {
        executor.execute {
            // Emulate long running task
            Thread.sleep(1000)
            makkina.transitionTo(DoorState.Open)
        }

        return DoorState.Opening
    }

    fun open() {
        makkina.sendEvent(DoorEvent.OpenEvent)
    }

    fun close() {
        makkina.sendEvent(DoorEvent.CloseEvent)
    }
}
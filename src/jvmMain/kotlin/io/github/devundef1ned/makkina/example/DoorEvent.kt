package io.github.devundef1ned.makkina.example

sealed interface DoorEvent {
    object OpenEvent : DoorEvent
    object CloseEvent : DoorEvent
}
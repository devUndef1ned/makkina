package com.devundef1ned.makkina.example

sealed interface DoorState {
    object Open : DoorState
    object Closed : DoorState
    // intermediate state when a process is in progress
    object Opening : DoorState
    // intermediate state when a process is in progress
    object Closing : DoorState
}
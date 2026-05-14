// File: app/src/main/java/com/samsung/innovatex/buffermind/feature/player/PlayerState.kt

package com.samsung.innovatex.buffermind.feature.player

sealed class PlayerState {
    object Idle : PlayerState()
    object Loading : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Error : PlayerState()
}
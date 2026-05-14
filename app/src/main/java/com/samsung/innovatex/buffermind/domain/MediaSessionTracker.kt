// File: app/src/main/java/com/samsung/innovatex/buffermind/domain/MediaSessionTracker.kt

package com.samsung.innovatex.buffermind.domain

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import com.samsung.innovatex.buffermind.util.Logger

sealed class PlaybackContext {
    object Playing : PlaybackContext()
    object Paused : PlaybackContext()
    object Stopped : PlaybackContext()
    object Looping : PlaybackContext()  // detected repeat
}

class MediaSessionTracker(
    private val mediaSession: MediaSession
) {

    private var playCount = 0
    private var currentPosition = 0L

    fun trackPlayback(): PlaybackContext {
        val state = mediaSession.player.playbackState
        val position = mediaSession.player.currentPosition
        val duration = mediaSession.player.duration

        // Detect looping (position reset to 0 after ~duration)
        if (position < 1000 && currentPosition > duration * 0.9) {
            playCount++
            Logger.d("MediaSession", "Loop detected: $playCount")
            return PlaybackContext.Looping
        }
        currentPosition = position

        return when {
            mediaSession.player.isPlaying -> PlaybackContext.Playing
            else -> PlaybackContext.Paused
        }
    }
}
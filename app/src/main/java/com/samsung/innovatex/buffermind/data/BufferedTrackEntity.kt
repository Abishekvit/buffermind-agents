// File: app/src/main/java/com/samsung/innovatex/buffermind/data/BufferedTrackEntity.kt

package com.samsung.innovatex.buffermind.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "buffered_tracks",
    indices = [Index(value = ["trackId"], unique = true)]
)
data class BufferedTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trackId: String,
    val title: String,
    val bufferedAtMs: Long,        // timestamp when buffered
    val lastUsedAtMs: Long,        // last playback seek / use
    val durationSec: Int,          // buffer length in seconds
    val isPlayingRepeated: Boolean, // repeat / loop flag
    val signalWhenBuffered: String? // "Weak", "Fair", "Strong"
)
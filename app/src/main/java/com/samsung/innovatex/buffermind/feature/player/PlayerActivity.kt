// File: app/src/main/java/com/samsung/innovatex/buffermind/feature/player/PlayerActivity.kt

package com.samsung.innovatex.buffermind.feature.player

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.samsung.innovatex.buffermind.R
import android.util.Log
//import com.samsung.innovatex.buffermind.util.Logger

sealed class PlayerState {
    object Idle : PlayerState()
    object Loading : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Error : PlayerState()
}
class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var tvStatus: TextView
    private lateinit var tvSensorStatus: TextView
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        tvStatus = findViewById(R.id.tvStatus)
        tvSensorStatus = findViewById(R.id.tvSensorStatus)

        setupPlayer()
    }

    private fun setupPlayer() {
        val streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3" // replace with real test URL
        val mediaItem = MediaItem.fromUri(streamUrl)

        val p = ExoPlayer.Builder(this).build()
        player = p

        playerView.player = p

        p.setMediaItem(mediaItem)
        p.prepare()
        p.play()

        tvStatus.text = "Status: Playing stream"
        Log.d("Player", "Player started with $streamUrl")
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
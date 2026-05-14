// File: app/src/main/java/com/samsung/innovatex/buffermind/feature/player/PlayerActivity.kt

package com.samsung.innovatex.buffermind.feature.player

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.samsung.innovatex.buffermind.R
import com.samsung.innovatex.buffermind.domain.BufferContext
import com.samsung.innovatex.buffermind.domain.BufferTriggerManager
import com.samsung.innovatex.buffermind.domain.BufferTriggerListener
import com.samsung.innovatex.buffermind.domain.ContextManager
import com.samsung.innovatex.buffermind.sensors.SensorManagerWrapper
import com.samsung.innovatex.buffermind.sensors.WalkingDetector
import com.samsung.innovatex.buffermind.sensors.SignalStrengthDetector
import com.samsung.innovatex.buffermind.util.Logger

class PlayerActivity :
    AppCompatActivity(),
    SensorEventListener,
    BufferTriggerListener {

    private lateinit var playerView: PlayerView
    private lateinit var tvStatus: TextView
    private lateinit var tvSensorStatus: TextView
    private var player: ExoPlayer? = null

    private lateinit var sensorManager: SensorManagerWrapper
    private val walkingDetector = WalkingDetector()
    private lateinit var signalStrengthDetector: SignalStrengthDetector
    private lateinit var contextManager: ContextManager
    private val bufferTriggerManager = BufferTriggerManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        tvStatus = findViewById(R.id.tvStatus)
        tvSensorStatus = findViewById(R.id.tvSensorStatus)

        sensorManager = SensorManagerWrapper(this)
        signalStrengthDetector = SignalStrengthDetector(this) { }
        contextManager = ContextManager(walkingDetector, signalStrengthDetector)

        setupPlayer()
        setupSensorListeners()
    }

    private fun setupPlayer() {
        val streamUrl = "https://example.com/audio/stream.m3u8" // replace
        val mediaItem = MediaItem.fromUri(streamUrl)

        val p = ExoPlayer.Builder(this).build()
        player = p

        playerView.player = p

        p.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                val playerState = when (state) {
                    Player.STATE_BUFFERING -> PlayerState.Loading
                    Player.STATE_READY -> {
                        if (p.isPlaying) PlayerState.Playing else PlayerState.Paused
                    }
                    Player.STATE_ENDED -> PlayerState.Idle
                    else -> PlayerState.Idle
                }
                contextManager.updatePlaybackState(playerState)
                updateUiStatus(playerState)
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException){
                Logger.e("Player", "Error: ${error.message}")
                updateUiStatus(PlayerState.Error)
            }
        })

        p.setMediaItem(mediaItem)
        p.prepare()
        p.play()

        contextManager.updatePlaybackState(PlayerState.Playing)
    }

    private fun setupSensorListeners() {
        sensorManager = SensorManagerWrapper(this)
        signalStrengthDetector.startListening()
    }

    private fun onSensorChanged(event: SensorEvent, type: Int?) {
        when (type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val context = contextManager.updateWalking(event)
                bufferTriggerManager.considerBuffer(context)
                updateSensorStatus(context)
            }
        }
    }

    private fun updateSensorStatus(context: BufferContext) {
        val walkingText = if (context.isWalking) "WALKING" else "STILL"
        val signalText = "Signal: ${context.signalLevel}"
        tvSensorStatus.text = "Status: $walkingText, $signalText"
    }

    private fun updateUiStatus(state: PlayerState) {
        tvStatus.text = "Status: $state"
    }

    override fun onSensorChanged(event: SensorEvent) {
        onSensorChanged(event, event.sensor.type)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        sensorManager.startListening()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        signalStrengthDetector.stopListening()
        player?.release()
        player = null
    }

    override fun onPredictiveBufferNeeded() {
        Logger.d("BufferTrigger", "ON BUFFERING AHEAD (FAKE)")
        tvStatus.append(" - BUFFERING AHEAD (FAKE)")
    }
}
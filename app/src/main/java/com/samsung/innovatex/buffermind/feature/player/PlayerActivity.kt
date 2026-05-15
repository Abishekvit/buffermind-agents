// File: app/src/main/java/com/samsung/innovatex/buffermind/feature/player/PlayerActivity.kt

package com.samsung.innovatex.buffermind.feature.player

import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import com.samsung.innovatex.buffermind.R
import com.samsung.innovatex.buffermind.data.FakeBufferCache
import com.samsung.innovatex.buffermind.domain.*
import com.samsung.innovatex.buffermind.sensors.*
import com.samsung.innovatex.buffermind.util.Logger

data class PredictionResult(
    val confidence: Float,
    val disconnectProbability: Float
)

class PlayerActivity : AppCompatActivity(), PredictionListener {

    private lateinit var playerView: PlayerView
    private lateinit var tvStatus: TextView
    private lateinit var tvSensorStatus: TextView

    private var player: ExoPlayer? = null
    private lateinit var mediaSession: MediaSession

    // Day 3 sensors + managers
    private lateinit var gpsDetector: GpsDetector
    private lateinit var wifiSignalDetector: WifiSignalDetector
    private lateinit var sensorFusionManager: SensorFusionManager
    private lateinit var predictiveEngine: PredictiveContextEngine
    private lateinit var fakeCache: FakeBufferCache
    private lateinit var mediaSessionTracker: MediaSessionTracker
    private val walkingDetector = WalkingDetector()

    private var isAirplaneMode = false
    private var gpsMoving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        tvStatus = findViewById(R.id.tvStatus)
        tvSensorStatus = findViewById(R.id.tvSensorStatus)

        setupPlayerAndSession()
        setupSensors()
        fakeCache = FakeBufferCache()
    }
    private fun setupPlayerAndSession() {
        val streamUrl = "https://ice.somafm.com/groovesalad-128-mp3" // WORKING TEST URL
        val mediaItem = MediaItem.fromUri(streamUrl)

        val p = ExoPlayer.Builder(this).build()
        player = p
        mediaSession = MediaSession.Builder(this, p).build()

        playerView.player = p
        mediaSessionTracker = MediaSessionTracker(mediaSession)

        p.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val playerState = when (playbackState) {
                    Player.STATE_READY -> if (p.isPlaying) PlayerState.Playing else PlayerState.Paused
                    Player.STATE_BUFFERING -> PlayerState.Loading
                    else -> PlayerState.Idle
                }
                updateStatus(playerState)
                processAllSensors() // Trigger sensor processing
            }
        })

        p.setMediaItem(mediaItem)
        p.prepare()
        p.play()
    }

    private fun setupSensors() {
        gpsDetector = GpsDetector(this) { moving ->
            gpsMoving = moving
            processAllSensors()
        }
        wifiSignalDetector = WifiSignalDetector(this) { signal ->
            processAllSensors()
        }
        sensorFusionManager = SensorFusionManager()
        predictiveEngine = PredictiveContextEngine(sensorFusionManager, this)

        gpsDetector.startListening()
        wifiSignalDetector.startMonitoring()
    }

    private fun processAllSensors() {
        val accelWalking = walkingDetector.isWalking()
        val gpsMovement = gpsMoving
        val signal = wifiSignalDetector.getCurrentSignal()
        val playback = mediaSessionTracker.trackPlayback()

        val fusedContext = sensorFusionManager.fuseContext(
            gpsMovement, accelWalking, signal, playback
        )
        predictiveEngine.processContext(fusedContext)
        updateContextUI(fusedContext)
    }

    override fun onBufferPredicted(risk: Float) {
        runOnUiThread {
            fakeCache.preloadSegment("current_track", 30)
            tvStatus.text = "BUFFERING 30 MIN AHEAD (risk: ${String.format("%.1f", risk)})"
            Toast.makeText(this, "AI PREDICTED DISCONNECT - BUFFERING", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBufferStarted() {
        tvStatus.text = "BUFFERING ACTIVE - 30min cached"
    }

    override fun onSeamlessPlayback() {
        tvStatus.text = "SEAMLESS PLAYBACK ACTIVE (offline)"
    }

    private fun updateStatus(state: PlayerState) {
        tvStatus.text = "Player: $state"
    }

    private fun updateContextUI(context: FusedContext) {
        tvSensorStatus.text = "Moving: ${context.isMoving}, Signal: ${context.signalQuality}, Risk: ${String.format("%.2f", context.riskScore)}"
    }

    // Simulate airplane mode disconnect (call from button or debug)
    fun simulateAirplaneMode() {
        isAirplaneMode = true
        if (fakeCache.hasBuffer("current_track")) {
            onSeamlessPlayback()
        } else {
            tvStatus.text = "DISCONNECT - No buffer available"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gpsDetector.stopListening()
        player?.release()
        mediaSession.release()
    }
}
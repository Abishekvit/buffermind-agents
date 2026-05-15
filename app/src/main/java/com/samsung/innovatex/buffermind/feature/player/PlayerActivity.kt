// File: app/src/main/java/com/samsung/innovatex/buffermind/feature/player/PlayerActivity.kt

package com.samsung.innovatex.buffermind.feature.player
import com.samsung.innovatex.buffermind.util.BufferNotificationManager
import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.cancel
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
import com.samsung.innovatex.buffermind.data.*
import com.samsung.innovatex.buffermind.domain.*
import com.samsung.innovatex.buffermind.sensors.*
import com.samsung.innovatex.buffermind.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity(), BufferTriggerListener {

    private val database: BufferDatabase by lazy {
        BufferDatabase.getDatabase(this)
    }
    private val dao = database.bufferDao()
    private val adaptiveMemory = AdaptiveMemoryManager(
        dao = dao,
        context = this,
        onCacheStatsChanged = ::updateCacheStatsUi,
    )

    private var tvStatus: TextView? = null
    private lateinit var tvPredictionConfidence: TextView
    private lateinit var tvPredictionRisk: TextView
    private lateinit var tvBufferStatus: TextView
    private lateinit var tvCacheStats: TextView
    private lateinit var tvSignalStatus: TextView
    private lateinit var tvSensorStatus: TextView
    private lateinit var btnSimulateAirplane: Button

    private lateinit var notificationManager: BufferNotificationManager

    private var playerView: PlayerView? = null
    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

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

    private val scope = MainScope()  // for lifecycleScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        tvPredictionConfidence =
            findViewById(R.id.tv_prediction_confidence)

        tvPredictionRisk =
            findViewById(R.id.tv_prediction_risk)

        tvBufferStatus =
            findViewById(R.id.tv_buffer_status)

        tvCacheStats =
            findViewById(R.id.tv_cache_stats)

        tvSignalStatus =
            findViewById(R.id.tv_signal_status)

        tvSensorStatus =
            findViewById(R.id.tv_sensor_status)

        btnSimulateAirplane =
            findViewById(R.id.btn_simulate_airplane)

        notificationManager =
            BufferNotificationManager(this)

        btnSimulateAirplane.setOnClickListener {
            simulateAirplaneMode()
        }

        setupPlayerAndSession()
        setupSensors()
        fakeCache = FakeBufferCache()
    }

    private fun setupPlayerAndSession() {
        val streamUrl = "https://ice.somafm.com/groovesalad-128-mp3" // WORKING TEST URL
        val mediaItem = MediaItem.fromUri(streamUrl)

        val p = ExoPlayer.Builder(this).build()
        player = p

        val ms = MediaSession.Builder(this, p).build()
        mediaSession = ms

        playerView?.player = p
        mediaSessionTracker = MediaSessionTracker(ms)

        p.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val playerState = when (playbackState) {
                    Player.STATE_READY -> {
                        if (p.isPlaying) PlayerState.Playing else PlayerState.Paused
                    }
                    Player.STATE_BUFFERING -> PlayerState.Loading
                    else -> PlayerState.Idle
                }
                updateStatus(playerState)
                processAllSensors()
            }
        })

        p.setMediaItem(mediaItem)
        p.prepare()
        p.play()

        // Start with Playing state
        predictiveEngine.processContext(
            sensorFusionManager.fuseContext(
                gpsMoving = false,
                accelWalking = false,
                signal = SignalQuality.Strong,
                playback = PlaybackContext.Playing,
            )
        )
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

        fakeCache.preloadSegment("current_track", 30)

        val conf =
            "${String.format("%.0f", risk * 100)}%"

        val prob =
            "${String.format("%.0f", risk * 100)}%"

        tvPredictionConfidence.text =
            "Confidence: $conf"

        tvPredictionRisk.text =
            "Risk: $prob"

        tvBufferStatus.text =
            "Buffering 30min ahead!"

        notificationManager.showBasicNotification(
            title = "BufferMind Agent",
            text = "AI predicted disconnect – 30min ahead!"
        )

        Toast.makeText(
            this,
            "Predictive buffering activated",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBufferStarted() {

        tvBufferStatus.text =
            "Adaptive memory optimization active"
    }

    override fun onSeamlessPlayback() {

        tvBufferStatus.text =
            "SEAMLESS PLAYBACK ACTIVE (offline)"
    }

    private fun updateStatus(state: PlayerState) {
        tvStatus?.text = "Player: $state"
    }

    private fun updateContextUI(context: FusedContext) {
        tvSensorStatus?.text = "Moving: ${context.isMoving}, Signal: ${context.signalQuality}, Risk: ${String.format("%.2f", context.riskScore)}"
    }

    private fun updateCacheStatsUi(stats: CacheStats) {
        tvCacheStats?.let { tv ->
            tv.text =
                "Cache: ${stats.bufferedTrackCount} tracks, " +
                        "Hit rate: ${String.format("%.1f%%", stats.cacheHitRate * 100)}\n" +
                        "Buffer mem: ~${stats.cacheSizeBytes / 1024 / 1024} MB"
        }
    }

    // Simulate airplane mode disconnect
    fun simulateAirplaneMode() {
        isAirplaneMode = true
        if (fakeCache.hasBuffer("current_track")) {
            tvStatus?.text = "SEAMLESS PLAYBACK ACTIVE (offline)"
        } else {
            tvStatus?.text = "DISCONNECT - No buffer available"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        gpsDetector.stopListening()
        player?.release()
        mediaSession?.release()
    }
}
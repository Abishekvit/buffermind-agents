// File: ai/BufferMindTfLite.kt

package com.samsung.innovatex.buffermind.ai
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import com.samsung.innovatex.buffermind.sensors.SignalLevel
import android.content.Context
import android.content.res.AssetManager
import com.samsung.innovatex.buffermind.domain.BufferContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import kotlin.math.round

class BufferMindTfLite(private val context: Context) {

    private val interpreter: Interpreter
    init {
        val assetManager = context.assets
        val modelFile = loadModelFile("buffermind_lstm.tflite", assetManager)
        interpreter = Interpreter(modelFile)
    }

    data class PredictionResult(
        val disconnectProbability: Float,
        val confidence: Float,
        val shouldBuffer: Boolean,
        val bufferMinutes: Int,
    )

    fun predict(context: BufferContext): PredictionResult {

        val input = floatArrayOf(
            if (context.isWalking) 1f else 0f,
            if (context.signalLevel == SignalLevel.Weak) 1f else 0f,
            if (context.isPlayingRepeated) 1f else 0f,
            1f,
            ((System.currentTimeMillis() / 1000 / 3600) % 24).toFloat(),
            10f,
            1f, 1f, 1f, 1f, 1f
        )

        val buffer = ByteBuffer.allocateDirect(4 * input.size)
            .order(java.nio.ByteOrder.nativeOrder())

        for (value in input) {
            buffer.putFloat(value)
        }

        val output = Array(1) { FloatArray(1) }

        interpreter.run(buffer, output)

        val prob = output[0][0]

        return PredictionResult(
            disconnectProbability = prob,
            confidence = 0.9f,
            shouldBuffer = prob > 0.7f,
            bufferMinutes = 30
        )
    }

    private fun loadModelFile(filename: String, assets: AssetManager): ByteBuffer {
        val fileDescriptor = assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val byteBuffer = ByteBuffer.allocateDirect(
            fileDescriptor.declaredLength.toInt()
        ).order(java.nio.ByteOrder.nativeOrder())
        val channel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        channel.position(startOffset)
        channel.read(byteBuffer)
        return byteBuffer
    }
}
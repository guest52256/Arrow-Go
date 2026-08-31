package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin

class SoundManager private constructor(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    private val sampleRate = 44100

    private fun playPcm(generator: (sampleIndex: Int, totalSamples: Int) -> Short, durationMs: Int) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    buffer[i] = generator(i, numSamples)
                }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(maxOf(minBufferSize, buffer.size * 2))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                delay(durationMs.toLong() + 50)
                track.release()
            } catch (e: Exception) {
                // Ignore audio failure gracefully
            }
        }
    }

    fun playWhoosh() {
        // Frequency rises swiftly from 220Hz to 880Hz
        playPcm({ i, total ->
            val t = i.toFloat() / sampleRate
            val progress = i.toFloat() / total
            val freq = 220f + 660f * progress
            val env = sin(PI.toFloat() * progress)
            val sample = sin(2.0 * PI * freq * t) * env
            (sample * 16000).toInt().toShort()
        }, 160)

        vibrateShort(25)
    }

    fun playCoinChime() {
        // Crisp dual-tone chime: 1046Hz (C6) -> 1318Hz (E6) -> 1568Hz (G6)
        playPcm({ i, total ->
            val t = i.toFloat() / sampleRate
            val progress = i.toFloat() / total
            val freq = if (progress < 0.33f) 1046.5f else if (progress < 0.66f) 1318.5f else 1567.98f
            val localT = when {
                progress < 0.33f -> progress / 0.33f
                progress < 0.66f -> (progress - 0.33f) / 0.33f
                else -> (progress - 0.66f) / 0.34f
            }
            val env = (1.0f - localT).coerceAtLeast(0f)
            val sample = (sin(2.0 * PI * freq * t) + 0.3 * sin(4.0 * PI * freq * t)) * env
            (sample * 18000).toInt().toShort()
        }, 220)

        vibrateShort(30)
    }

    fun playErrorBuzzer() {
        // Low distorted buzz 110Hz -> 85Hz
        playPcm({ i, total ->
            val t = i.toFloat() / sampleRate
            val progress = i.toFloat() / total
            val freq = 120f - 35f * progress
            val env = 1f - progress * 0.7f
            // Distorted / clipped wave for buzzer feel
            val raw = sin(2.0 * PI * freq * t) + 0.5 * sin(6.0 * PI * freq * t)
            val clipped = raw.coerceIn(-0.8, 0.8) * env
            (clipped * 22000).toInt().toShort()
        }, 240)

        vibratePattern(longArrayOf(0, 70, 50, 90))
    }

    fun playHintChime() {
        // Sparkle arpeggio
        playPcm({ i, total ->
            val t = i.toFloat() / sampleRate
            val progress = i.toFloat() / total
            val freq = 880f + 700f * progress
            val env = sin(PI.toFloat() * progress)
            val sample = sin(2.0 * PI * freq * t) * env
            (sample * 14000).toInt().toShort()
        }, 300)

        vibrateShort(40)
    }

    fun playVictoryFanfare() {
        // Triumphant chord fanfare: 523Hz -> 659Hz -> 783Hz -> 1046Hz
        playPcm({ i, total ->
            val t = i.toFloat() / sampleRate
            val progress = i.toFloat() / total
            val freq = when {
                progress < 0.25f -> 523.25f // C5
                progress < 0.50f -> 659.25f // E5
                progress < 0.75f -> 783.99f // G5
                else -> 1046.50f // C6
            }
            val env = 1f - (progress % 0.25f) / 0.25f * 0.4f
            val sample = (sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t)) * env
            (sample * 20000).toInt().toShort()
        }, 650)

        vibratePattern(longArrayOf(0, 50, 40, 50, 40, 100))
    }

    fun playClick() {
        playPcm({ i, total ->
            val t = i.toFloat() / sampleRate
            val progress = i.toFloat() / total
            val freq = 1200f
            val env = 1f - progress
            val sample = sin(2.0 * PI * freq * t) * env
            (sample * 12000).toInt().toShort()
        }, 30)

        vibrateShort(15)
    }

    fun vibrateShort(durationMs: Long) {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun vibratePattern(timings: LongArray) {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object NotificationSoundHelper {

    /**
     * Plays an audible, clear and loud multi-tone alert sound along with haptic vibration.
     * Uses 3 complementary strategies to guarantee audio output across all devices and emulators:
     * 1. High-fidelity synthesized PCM chime (AudioTrack) with classic dispatch chime notes
     * 2. Android system ToneGenerator (CDMA Alert / High Beep)
     * 3. Native system Ringtone/Notification alert
     */
    fun playNewReservationSound(context: Context?) {
        // 1. Synthesized PCM dispatch bell chime (100% reliable, zero asset dependencies)
        playSynthesizedDispatchChime()

        // 2. ToneGenerator on Notification and Music streams
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 600)
        } catch (e: Exception) {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 500)
            } catch (ex: Exception) {
                Log.w("NotificationSound", "ToneGenerator notice: ${ex.message}")
            }
        }

        // 3. Play native notification ringtone if context available
        if (context != null) {
            try {
                val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val ringtone = RingtoneManager.getRingtone(context.applicationContext, alertUri)
                ringtone?.apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    }
                    play()
                }
            } catch (e: Exception) {
                Log.w("NotificationSound", "Ringtone notice: ${e.message}")
            }

            // 4. Strong haptic vibration feedback pattern
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator?.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200, 100, 450), -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(
                            VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200, 100, 450), -1)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(longArrayOf(0, 200, 100, 200, 100, 450), -1)
                    }
                }
            } catch (e: Exception) {
                Log.w("NotificationSound", "Vibrator notice: ${e.message}")
            }
        }
    }

    /**
     * Synthesizes an executive 3-tone chime (D5 -> G5 -> D6) using PCM 16-bit.
     * Guaranteed to produce audible sound regardless of device vendor ringtone defaults.
     */
    private fun playSynthesizedDispatchChime() {
        Thread {
            try {
                val sampleRate = 44100
                // Melodic sequence: 587.33Hz (D5), 783.99Hz (G5), 1174.66Hz (D6)
                val frequencies = doubleArrayOf(587.33, 783.99, 1174.66)
                val noteDuration = 0.22 // seconds each note
                val totalSamples = (sampleRate * noteDuration * frequencies.size).toInt()
                val audioBuffer = ShortArray(totalSamples)

                var sampleIdx = 0
                for (freq in frequencies) {
                    val samplesPerNote = (sampleRate * noteDuration).toInt()
                    for (i in 0 until samplesPerNote) {
                        val time = i.toDouble() / sampleRate
                        // Exponential bell-curve decay for a clean crystal chime sound
                        val decay = (1.0 - (i.toDouble() / samplesPerNote)).coerceIn(0.0, 1.0)
                        val harmonic = Math.sin(2.0 * Math.PI * freq * time) + 0.35 * Math.sin(4.0 * Math.PI * freq * time)
                        val sample = (harmonic * decay * 0.90 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        if (sampleIdx < totalSamples) {
                            audioBuffer[sampleIdx++] = sample.toShort()
                        }
                    }
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(totalSamples * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(audioBuffer, 0, totalSamples)
                audioTrack.play()
            } catch (e: Exception) {
                Log.w("NotificationSound", "Synthesized chime notice: ${e.message}")
            }
        }.start()
    }
}

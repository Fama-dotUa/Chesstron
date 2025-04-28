package com.example.chesstron

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

object SoundManager {

    private lateinit var soundPool: SoundPool
    private val soundMap = mutableMapOf<String, Int>()
    private val soundLoaded = mutableMapOf<String, Boolean>()
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10) // більше паралельних потоків
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                soundMap.entries.find { it.value == sampleId }?.key?.let { key ->
                    soundLoaded[key] = true
                }
            }
        }

        loadSound(context, "move", R.raw.move_sound)
        loadSound(context, "capture", R.raw.capture_sound)
        loadSound(context, "check", R.raw.check_sound)
        loadSound(context, "checkmate", R.raw.checkmate_sound)
        loadSound(context, "stalemate", R.raw.stalemate_sound)

        isInitialized = true
    }

    private fun loadSound(context: Context, name: String, resId: Int) {
        val soundId = soundPool.load(context, resId, 1)
        soundMap[name] = soundId
        soundLoaded[name] = false
    }

    fun playSound(name: String) {
        if (!isInitialized) return

        if (soundLoaded[name] == true) {
            soundMap[name]?.let { soundId ->
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }
        }
    }

    fun release() {
        if (isInitialized) {
            soundPool.release()
            isInitialized = false
        }
    }
}
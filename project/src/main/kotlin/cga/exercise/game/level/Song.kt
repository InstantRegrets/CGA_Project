package cga.exercise.game.level

import cga.exercise.components.sound.SoundBuffer
import cga.exercise.components.sound.SoundSource
import java.io.File

class Song(path: File) {
    private val musicBuffer: SoundBuffer
    private val musicSource: SoundSource

    init {
        musicBuffer = SoundBuffer(path)
        musicSource = SoundSource(loop = true, relative = false, buffer = musicBuffer)
        musicSource.setGain(0.1f)
    }

    fun play() {
        musicSource.play()
    }

    fun pause() {
        musicSource.pause()
    }

    fun unpause() {
        musicSource.play()
    }

    fun cleanup(){
        musicBuffer.cleanup()
        musicSource.cleanup()
    }
}
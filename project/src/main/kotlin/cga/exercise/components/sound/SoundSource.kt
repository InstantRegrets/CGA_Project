package cga.exercise.components.sound

import org.joml.Vector3f
import org.lwjgl.openal.AL10.*
import java.lang.Exception


class SoundSource(loop: Boolean, relative: Boolean, buffer: SoundBuffer) {
    private final val sourceId: Int
    init {
        sourceId = alGenSources()
        if (loop)
            alSourcei(sourceId, AL_LOOPING, AL_TRUE)
        if (relative)
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE)
        // alSourcef(sourceId, AL_ROLLOFF_FACTOR, 100f)
        // alSourcef(sourceId, AL_REFERENCE_DISTANCE, 1.0f)
        // alSource3f(sourceId, AL_POSITION, -10000f,-10000f,-10000f)
        setBuffer(buffer)
    }

    fun setBuffer(buffer: SoundBuffer){
        stop()
        alSourcei(sourceId, AL_BUFFER, buffer.bufferId)
    }

    fun setPosition(position: Vector3f){
        alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
    }

    fun setSpeed(speed: Vector3f) {
        alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z)
    }

    fun setGain(gain: Float){
        alSourcef(sourceId, AL_GAIN, gain * masterVolume)
    }

    fun setProperty(param: Int, value: Float) {
        alSourcef(sourceId, param, value)
    }

    fun play() {
        alSourcePlay(sourceId)
    }

    fun state(): Int {
        return alGetSourcei(sourceId, AL_SOURCE_STATE)
    }

    fun isPlaying(): Boolean {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING
    }

    fun pause() {
        alSourcePause(sourceId)
    }

    fun stop() {
        alSourceStop(sourceId)
    }

    fun cleanup() {
        stop()
        alDeleteSources(sourceId)
    }

    companion object {
        var masterVolume: Float = 1f;
    }
}
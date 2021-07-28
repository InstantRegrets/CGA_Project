package cga.exercise.components.sound

import cga.exercise.components.camera.ICamera
import cga.exercise.components.camera.TronCamera
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL11
import org.lwjgl.openal.AL11.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC11.*
import java.nio.ByteBuffer
import java.nio.IntBuffer


object SoundContext {
    var device: Long = 0
    var context: Long = 0
    var soundSources: ArrayList<SoundSource> = arrayListOf()

    fun setup() {
        device = alcOpenDevice(null as ByteBuffer?)
        val deviceCaps = ALC.createCapabilities(device)
        context = alcCreateContext(device, null as IntBuffer?)
        alcMakeContextCurrent(context)
        AL.createCapabilities(deviceCaps)
        setAttenuationModel(AL_INVERSE_DISTANCE_CLAMPED)
        SoundListener
    }

    fun playSound(){
        for (s in soundSources)
            s.play()
    }

    fun setAttenuationModel(model: Int) {
        alDistanceModel(model)
    }

    fun cleanup() {
        alcCloseDevice(device)
    }
}


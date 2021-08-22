package cga.exercise.components.sound

import cga.exercise.components.camera.DuckCamera
import org.joml.Vector3f
import org.lwjgl.openal.AL10.*


// only one listener exist at any given time
object SoundListener {
    fun setSpeed(speed: Vector3f) {
        alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z)
    }

    fun setPosition(camera: DuckCamera) {
        setPosition(camera.getWorldPosition())
        val at = camera.getWorldZAxis().negate()
        val up = camera.getWorldYAxis()
        setOrientation(at, up)
    }

    fun setPosition(position: Vector3f) {
        alListener3f(AL_POSITION, position.x, position.y, position.z)
    }

    fun setOrientation(at: Vector3f, up: Vector3f) {
        val data = FloatArray(6)
        data[0] = at.x
        data[1] = at.y
        data[2] = at.z
        data[3] = up.x
        data[4] = up.y
        data[5] = up.z
        alListenerfv(AL_ORIENTATION, data)
    }

}

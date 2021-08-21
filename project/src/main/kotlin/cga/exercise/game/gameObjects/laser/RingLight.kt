package cga.exercise.game.gameObjects.laser

import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.sin

class RingLight(left: Boolean) : Laser(){
    val site = if (left) -1.0f else 1.0f
    var rotation = 0f
    init {
        translateLocal(Vector3f(0f, 700f,-1028f))
        rotateLocalAxis(0.25f*PI.toFloat()*site, Vector3f(0f,0f,1f))
    }

    override fun updatePos(dt: Float, beat: Float) {
        rotateLocalAxis(rotation*dt, Vector3f(0f,0f,1f))
        rotation = sin(beat/64 *2f*PI.toFloat())*0.8f
    }
}
package cga.exercise.game.gameObjects.laser

import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.sin

class RotatingLaser(left: Boolean, private val offset: Float) : Laser(){
    private val site = if (left) -1.0f else 1.0f
    private var rotation = 0f
    init {
        translateLocal(Vector3f(site*400,200f,-1028f))
        rotateLocalAxis(3.1f*site + offset, Vector3f(0f,0f,1f))
    }

    override fun updatePos(dt: Float, beat: Float) {
        rotateLocalAxis(rotation*dt, Vector3f(0f,0f,1f))
        rotation = sin(offset + beat/32 *2f*PI.toFloat())*1.0f * site
    }
}
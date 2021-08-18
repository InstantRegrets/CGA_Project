package cga.exercise.game.gameObjects.laser

import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.sin

class CenterLight(left: Boolean) : Laser(){
    val site = if (left) -1.0f else 1.0f
    var rotation = 0f
    init {
        translateLocal(Vector3f(site*400,1000f,-1028f))
    }

    override fun updatePos(dt: Float, beat: Float) {

    }
}
package cga.exercise.game.gameObjects.street

import cga.exercise.game.gameObjects.Model
import org.joml.Vector3f

class StreetModel: Model("assets/models/Street/street.obj") {
    init {
        renderable.scaleLocal(Vector3f(0.08f))
        renderable.rotateLocal(0f,Math.toRadians(90.0).toFloat(),0f)
    }
}

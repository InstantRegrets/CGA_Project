package cga.exercise.game.gameObjects.player
import cga.exercise.game.gameObjects.Model
import org.joml.Vector3f

class PlayerModel: Model(
    "assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",
    pitch = Math.toRadians(-90.0).toFloat(),
    yaw = Math.toRadians(90.0).toFloat()
    ){
    init {
        renderable.pulseStrength = 0.5f
        renderable.emitColor = Vector3f(0.5f)
        renderable.translateLocal(Vector3f(0f,-5f,0f))
    }
}

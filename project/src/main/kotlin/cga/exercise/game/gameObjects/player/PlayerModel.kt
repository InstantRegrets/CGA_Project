package cga.exercise.game.gameObjects.player
import cga.exercise.game.gameObjects.Model

class PlayerModel: Model(
    "assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",
    pitch = Math.toRadians(-90.0).toFloat(),
    yaw = Math.toRadians(90.0).toFloat()
    )
{
}

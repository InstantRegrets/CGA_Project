package cga.exercise.game.gameObjects.player
import cga.exercise.game.gameObjects.Model

class PlayerModel(pObjPath: String): Model(pObjPath){
    init {
        renderable.rotateLocal(Math.toRadians(-90.0).toFloat(), 0f,Math.toRadians(90.0).toFloat())
    }
}

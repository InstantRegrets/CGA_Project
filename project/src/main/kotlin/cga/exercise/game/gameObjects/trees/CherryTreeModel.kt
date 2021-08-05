package cga.exercise.game.gameObjects.trees

import cga.exercise.game.gameObjects.Model
import org.joml.Vector3f

class CherryTreeModel(objPath: String) : Model(objPath) {
    init {
        renderable.translateGlobal(Vector3f(5f,0f,-10f))
        renderable.scaleLocal(Vector3f(0.5f))
    }
}

package cga.exercise.game.gameObjects.trees

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Model
import cga.framework.GameWindow
import org.joml.Matrix4f
import org.joml.Vector3f

class CherryTree : GameObject {
    val model: Model = CherryTreeModel("assets/models/cherryTree/cherryTree.obj")
    override fun draw(shaderProgram: ShaderProgram) {
        model.draw(shaderProgram)
    }

    override fun update(dt: Float, beat: Float) {

    }

    override fun processInput(window: GameWindow, dt: Float) {

    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {

    }
}

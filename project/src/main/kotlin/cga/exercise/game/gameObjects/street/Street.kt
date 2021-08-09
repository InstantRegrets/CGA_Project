package cga.exercise.game.gameObjects.street

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Model
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.Matrix4f
import org.joml.Vector3f

class Street: GameObject {
    val model = StreetModel()

    override fun draw(shaderProgram: ShaderProgram) {
        model.draw(shaderProgram)
    }

    override fun update(dt: Float, t: Float) {

    }

    override fun processInput(window: GameWindow, dt: Float) {
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {

    }

    override fun getPosition(): Vector3f {
        return model.renderable.getWorldPosition()
    }

}

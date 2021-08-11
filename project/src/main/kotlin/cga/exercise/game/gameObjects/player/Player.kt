package cga.exercise.game.gameObjects.player

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Model
import cga.framework.GameWindow
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class Player(parent: Transformable? = null): GameObject {
    val model: Model = PlayerModel()
    private val lighting = PlayerLighting(model.renderable)
    var movementSpeed: Float = 8f
    var rotationSpeed: Float = 6f

    init {
        model.renderable.parent = Transformable()
    }

    override fun draw(shaderProgram: ShaderProgram) {
        model.draw(shaderProgram)
    }

    override fun update(dt: Float, beat: Float) {
        //doesnt need anything rn
    }

    override fun processInput(window: GameWindow, dt: Float) {
        if (window.getKeyState(GLFW_KEY_W)) {
            model.renderable.translateLocal(Vector3f(0f, 0f, -movementSpeed * dt))
            if (window.getKeyState(GLFW_KEY_A)) model.renderable.rotateLocal(0f, rotationSpeed * dt, 0f)
            if (window.getKeyState(GLFW_KEY_D)) model.renderable.rotateLocal(0f, -rotationSpeed * dt, 0f)
        }
        if (window.getKeyState(GLFW_KEY_S)) {
            model.renderable.translateLocal(Vector3f(0f, 0f, movementSpeed * dt))
            if (window.getKeyState(GLFW_KEY_A)) model.renderable.rotateLocal(0f, rotationSpeed * dt, 0f)
            if (window.getKeyState(GLFW_KEY_D)) model.renderable.rotateLocal(0f, -rotationSpeed * dt, 0f)
        }
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        lighting.update(shaderProgram, viewMatrix4f)
    }
}


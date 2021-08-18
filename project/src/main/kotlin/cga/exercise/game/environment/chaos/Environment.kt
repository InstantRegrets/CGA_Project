package cga.exercise.game.environment.chaos

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.Matrix4f
import org.joml.Vector3f

class Environment : GameObject {
    val model = EnvironmentModel()
    override fun draw(shaderProgram: ShaderProgram) {
        model.draw(shaderProgram)
    }

    override fun update(dt: Float, t: Float) {
    }

    override fun processInput(window: GameWindow, dt: Float) {
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
    }

    override fun switchPhase(phase: Phase) {
        when(phase){
            Phase.Day -> { model.renderable.vibeStrength = 1f}
            Phase.Night -> { model.renderable.vibeStrength = 0.1f}
            Phase.Chaos -> { model.renderable.vibeStrength = 5f}
        }
    }

    fun getPosition(): Vector3f {
        return Vector3f(0f)
    }

}

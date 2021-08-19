package cga.exercise.game.environment.chaos

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.framework.GameWindow
import org.joml.Matrix4f
import org.joml.Vector3f

class Environment : GameObject {
    val wobbleModel = EnvironmentWobbleModel()
    val staticModel = EnvironmentStaticModel()
    init {
        staticModel.renderable.vibeStrength=0f
    }
    override fun draw(shaderProgram: ShaderProgram) {
        staticModel.draw(shaderProgram)
        wobbleModel.draw(shaderProgram)
    }

    override fun update(dt: Float, t: Float) {
    }

    override fun processInput(window: GameWindow, dt: Float) {
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
    }

    override fun switchPhase(phase: Phase) {
        when(phase){
            Phase.Day -> { wobbleModel.renderable.vibeStrength = 1f}
            Phase.Night -> { wobbleModel.renderable.vibeStrength = 0.1f}
            Phase.Chaos -> { wobbleModel.renderable.vibeStrength = 1f}
        }
    }

    fun getPosition(): Vector3f {
        return Vector3f(0f)
    }

}

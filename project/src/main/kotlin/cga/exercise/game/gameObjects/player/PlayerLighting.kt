package cga.exercise.game.gameObjects.player

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.light.Lighting
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI

class PlayerLighting(parent: Transformable): Lighting {
    val pointLight = PointLight(
        Vector3f(1f, 0f, 0f),
        Vector3f(1f, 0.15f, 0.12f)
    )

    val spotLight = SpotLight(
        Vector3f(1f, 1f, 1f),
        Vector3f(0.5f, 0.05f, 0.01f)
    )
    init {
        pointLight.parent = parent
        pointLight.translateLocal(Vector3f(0f,1f,0f))
        spotLight.translateLocal( Vector3f(0f, 1f, -2f))
        spotLight.rotateLocal(0f, 1f * PI.toFloat(), 0f)
        spotLight.rotateLocal(1f, 0f * PI.toFloat(), 0f)
        spotLight.parent = parent
    }

    override fun update(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        pointLight.bind(shaderProgram, viewMatrix)
        spotLight.bind(shaderProgram, viewMatrix)
    }
}

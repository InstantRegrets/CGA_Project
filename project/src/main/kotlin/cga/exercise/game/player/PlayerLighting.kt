package cga.exercise.game.player

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.Lighting
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI

class PlayerLighting(parent: Transformable): Lighting {
    val pointLight = PointLight(
        0,
        Vector3f(0f, 1f, 0f),
        Vector3f(1f, 0f, 0f),
        Vector3f(1f, 0.5f, 0.1f)
    )

    val spotLight = SpotLight(
        0,
        Vector3f(0f, 1f, -2f),
        Vector3f(1f, 1f, 1f),
        Vector3f(0.5f, 0.05f, 0.01f)
    )
    init {
        pointLight.parent = parent
        spotLight.rotateLocal(0f, 1f * PI.toFloat(), 0f)
        spotLight.rotateLocal(1f, 0f * PI.toFloat(), 0f)
        spotLight.parent = parent
    }

    override fun update(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        pointLight.bind(shaderProgram)
        spotLight.bind(shaderProgram, "bikeSpotLight", viewMatrix)
    }
}
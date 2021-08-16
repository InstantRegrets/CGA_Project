package cga.exercise.game.gameObjects.orb

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.framework.GameWindow
import cga.framework.Random
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class Orb:Transformable(), GameObject {
    private val color = Random.nextColor()
    val model: OrbModel = OrbModel(color)
    val light = PointLight(color, Vector3f(1f, 0.15f, 0.12f))
    val direction = Random.nextVec3(-2f*PI.toFloat(), 2f* PI.toFloat())

    init {
        model.renderable.scaleLocal(Vector3f(0.2f))
        model.renderable.parent = this
        light.parent = this
        translateLocal(Vector3f(0f,Random.nextFloat(1f,10f),0f))
    }

    override fun draw(shaderProgram: ShaderProgram) {
        model.draw(shaderProgram)
    }

    override fun update(dt: Float, beat: Float) {
        val speed = 16f * (beat%1 - 0.5f).pow(4) + 0.2f
        rotateAroundPoint(
            direction.x * dt * speed,
            direction.y * dt * speed,
            direction.z * dt * speed,
            Vector3f(0f)
        )
        direction.x = dir(direction.x)
        direction.y = dir(direction.y)
        direction.z = dir(direction.z)
        model.update(dt, beat)
    }

    private fun dir(float: Float): Float {
        return max(
            min(float + Random.nextFloat(-0.1f,0.1f), 2f* PI.toFloat()),
            -2f* PI.toFloat()
        )
    }

    override fun processInput(window: GameWindow, dt: Float) {
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        light.bind(shaderProgram,viewMatrix4f)
    }
}

package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.sqrt

/**
 * @param attenuation is a vector describing the attenuation in the form of:
 * vec3(constant,linear,quadratic)
 */
class PointLight(
    val color: Vector3f,
    val attenuation: Vector3f
): Light() {
    val intensity: Float = 1f
    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        val structName = "plData."
        plAmount++;

        val pos = Vector4f(super.getWorldPosition(), 1f).mul(viewMatrix).toVector3f()
        shaderProgram.setUniform("${structName}lightPos", pos)
        shaderProgram.setUniform("${structName}color", color)
        shaderProgram.setUniform("${structName}attenuation", attenuation)
    }
    fun calcBoundingSphere(): Float{
        val constant = attenuation.x
        val linear = attenuation.y
        val exp = attenuation.z
        val maxChannel = maxOf(color.x, color.y, color.z)
        val ret = (-linear + sqrt(linear*linear - 4 * exp * (constant - 256/5 * maxChannel * intensity)))/
                2 * exp
        return ret*30
    }
}

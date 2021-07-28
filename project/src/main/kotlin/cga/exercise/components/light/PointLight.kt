package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f

/**
 * @param attenuation is a vector describing the attenuation in the form of:
 * vec3(constant,linear,quadratic)
 */
open class PointLight(
    val index: Int,
    position: Vector3f,
    val color: Vector3f,
    val attenuation: Vector3f,
): Transformable(), IPointLight {
    init { translateGlobal(position) }

    override fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("plPosition[$index]",super.getWorldPosition())
        val struct = "plStaticData[$index]."
        shaderProgram.setUniform("${struct}color",color)
        shaderProgram.setUniform("${struct}attenuation", attenuation)
    }
}
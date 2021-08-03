package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * @param attenuation is a vector describing the attenuation in the form of:
 * vec3(constant,linear,quadratic)
 */
open class PointLight(
    val index: Int,
    position: Vector3f,
    val color: Vector3f,
    val attenuation: Vector3f
): Transformable(), IPointLight {
    init { translateGlobal(position) }
    private val structName = "plData[$index]."

    override fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("toLight[$index]",super.getWorldPosition())
        shaderProgram.setUniform("${structName}color",color)
        shaderProgram.setUniform("${structName}attenuation", attenuation)
    }

    open fun bindCamSpace(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {//todo maybe just give view matrix instead of cam
        //Calculate view*model matrix
        val mm = getWorldModelMatrix()
        val viewModel = viewMatrix.mul(mm)
        val posInCamSpace = Vector3f(
            viewModel.m30(),
            viewModel.m31(),
            viewModel.m32()
        )
        shaderProgram.setUniform("${structName}toLight", posInCamSpace)
        shaderProgram.setUniform("${structName}color", color)
        shaderProgram.setUniform("${structName}attenuation", attenuation)
    }
}
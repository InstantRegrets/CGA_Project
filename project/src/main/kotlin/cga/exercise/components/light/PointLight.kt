package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

/**
 * @param attenuation is a vector describing the attenuation in the form of:
 * vec3(constant,linear,quadratic)
 */
class PointLight(
    val color: Vector3f,
    val attenuation: Vector3f
): Light() {
    private var structName: String = "plData[-1]."
    init { register(this) }

    public override fun setIndex(index: Int) { structName = "plData[$index]." }

    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        val pos = Vector4f(super.getWorldPosition(), 1f).mul(viewMatrix).toVector3f()
        shaderProgram.setUniform("${structName}lightPos", pos)
        shaderProgram.setUniform("${structName}color", color)
        shaderProgram.setUniform("${structName}attenuation", attenuation)
    }
}

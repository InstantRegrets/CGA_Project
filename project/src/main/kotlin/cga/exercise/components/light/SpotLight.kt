package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.PI
import kotlin.math.cos

/**
 * @param attenuation is a vector describing the attenuation in the form of:
 * vec3(constant,linear,quadratic)
 */
class SpotLight(
    index: Int,
    position: Vector3f,
    color: Vector3f,
    attenuation: Vector3f,
    private val outerCone: Float = (0.4* PI).toFloat(), // Outer Cone in radians
    private val innerCone: Float = (0.2* PI).toFloat(), // Inner Cone in radians
) : PointLight(index, position, color, attenuation), ISpotLight {
    override fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
        shaderProgram.setUniform("slPosition[$index]",super.getWorldPosition())

        val struct = "slStaticData[$index]."
        val dir = (Vector4f(super.getWorldZAxis(), 0f)).mul(viewMatrix).toVector3f()
        shaderProgram.setUniform("${struct}color",color)
        shaderProgram.setUniform("${struct}direction",dir)
        shaderProgram.setUniform("${struct}innerCone",cos(innerCone))
        shaderProgram.setUniform("${struct}outerCone",cos(outerCone))
        shaderProgram.setUniform("${struct}attenuation", attenuation)
    }
}

private fun Vector4f.toVector3f(): Vector3f = Vector3f(x,y,z)

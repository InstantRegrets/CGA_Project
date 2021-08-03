package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
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
    private val structName = "slData[$index]." //how the struct for Spotlights is called in the FragmentShader

    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        shaderProgram.setUniform("${structName}toLight",super.getWorldPosition())
        setParametersAsUniform(shaderProgram,viewMatrix)
    }

    override fun bindCamSpace(shaderProgram: ShaderProgram, viewMatrix: Matrix4f){
        setCamSpacePos(shaderProgram, viewMatrix)
        setParametersAsUniform(shaderProgram,viewMatrix)
    }
    private fun setCamSpacePos(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        val mm = getWorldModelMatrix()
        val viewModel = viewMatrix.mul(mm)
        val camSpacePos = Vector3f(
            viewModel.m30(),
            viewModel.m31(),
            viewModel.m32()
        )
        shaderProgram.setUniform("${structName}toLight",camSpacePos)
    }

    private fun setParametersAsUniform(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        val dir = (Vector4f(super.getWorldZAxis(), 0f)).mul(viewMatrix).toVector3f()
        shaderProgram.setUniform("${structName}color",color)
        shaderProgram.setUniform("${structName}direction",dir)
        shaderProgram.setUniform("${structName}innerCone",cos(innerCone))
        shaderProgram.setUniform("${structName}outerCone",cos(outerCone))
        shaderProgram.setUniform("${structName}attenuation", attenuation)
    }
}

private fun Vector4f.toVector3f(): Vector3f = Vector3f(x,y,z)

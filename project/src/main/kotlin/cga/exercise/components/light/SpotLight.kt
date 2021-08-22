package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
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
    val color: Vector3f,
    val attenuation: Vector3f,
    val outerCone: Float = (0.4* PI).toFloat(), // Outer Cone in radians
    val innerCone: Float = (0.2* PI).toFloat(),
    var near_plane: Float = 250f,
    var far_plane: Float = 450f,
    var shadowQuadSize: Float = 80f, // Inner Cone in radians
) : Light() {

    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        val structName = "slData."
        val dir = (Vector4f(super.getWorldZAxis(), 0f)).mul(viewMatrix).toVector3f()
        val pos = Vector4f(super.getWorldPosition(), 1f).mul(viewMatrix).toVector3f()
        shaderProgram.setUniform("${structName}lightPos",pos)
        shaderProgram.setUniform("${structName}color",color)
        shaderProgram.setUniform("${structName}direction",dir)
        shaderProgram.setUniform("${structName}innerCone",cos(innerCone))
        shaderProgram.setUniform("${structName}outerCone",cos(outerCone))
        shaderProgram.setUniform("${structName}attenuation", attenuation)
    }

    fun calcViewMatrix(): Matrix4f{
        val up = getWorldYAxis()
        val position = getWorldPosition()
        return Matrix4f().lookAt(position, Vector3f(0f), up)
    }

    fun calcPVMatrix(): Matrix4f {
        val projectionMatrix =  Matrix4f().ortho(-shadowQuadSize,shadowQuadSize,-shadowQuadSize,shadowQuadSize, near_plane, far_plane)
        projectionMatrix.mul(calcViewMatrix())
        return projectionMatrix
    }
}


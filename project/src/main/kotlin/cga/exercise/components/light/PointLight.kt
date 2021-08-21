package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.PI
import kotlin.math.sqrt

/**
 * @param attenuation is a vector describing the attenuation in the form of:
 * vec3(constant,linear,quadratic)
 */
class PointLight(
    val color: Vector3f,
    private val attenuation: Vector3f,
    val nearPlane: Float = 0f,
): Light() {
    val intensity: Float = 1f
    val farPlane: Float = calcBoundingSphere()
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

    private val cameraViewMatrix4f = Matrix4f()
    private val viewMatrix = Matrix4f()
    fun calcViewMatrix(side: Vector3f, up: Vector3f): Matrix4f{
        val position = getWorldPosition4f().mul(cameraViewMatrix4f).toVector3f()
        val target = Vector3f(position).add(side)
        viewMatrix.identity()
        viewMatrix.lookAt(position, target, up)
        return viewMatrix
    }

    var projectionMatrix = Matrix4f()
    fun calcProjectionMatrix(aspect: Float): Matrix4f {
        projectionMatrix.identity()
        projectionMatrix = projectionMatrix.perspective(0.5f*PI.toFloat(),aspect,nearPlane,farPlane)
        return  projectionMatrix
    }

    fun calcPVMatrixArray(aspect: Float, cameraViewMatrix4f: Matrix4f): Array<Matrix4f> {
        cameraViewMatrix4f.set(cameraViewMatrix4f)
        calcProjectionMatrix(aspect)
        val shadowTransform = arrayOf(
            Matrix4f(projectionMatrix).mul(calcViewMatrix(Vector3f(1f,0f,0f),Vector3f(0f,-1f,0f))),
            Matrix4f(projectionMatrix).mul(calcViewMatrix(Vector3f(-1f,0f,0f),Vector3f(0f,-1f,0f))),
            Matrix4f(projectionMatrix).mul(calcViewMatrix(Vector3f(0f,1f,0f),Vector3f(0f,0f,1f))),
            Matrix4f(projectionMatrix).mul(calcViewMatrix(Vector3f(0f,-1f,0f),Vector3f(0f,0f,-1f))),
            Matrix4f(projectionMatrix).mul(calcViewMatrix(Vector3f(0f,0f,1f),Vector3f(0f,-1f,0f))),
            Matrix4f(projectionMatrix).mul(calcViewMatrix(Vector3f(0f,0f,-1f),Vector3f(0f,-1f,0f))),
        )
        return shadowTransform
    }
}

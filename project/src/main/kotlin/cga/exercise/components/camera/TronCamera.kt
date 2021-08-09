package cga.exercise.components.camera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI

class TronCamera(
    private val fieldOfView: Float = 0.5f * PI.toFloat(),
    private val aspectRatio: Float = 16f / 9f,
    private val nearPlane: Float = 0.1f,
    private val farPlane: Float = 100f,
    parent: Transformable = Transformable()
): Transformable(parent = parent), ICamera {
    var viewMatrix: Matrix4f = Matrix4f()
    var i = 0

    override fun getCalculateViewMatrix(): Matrix4f {
        val up = getWorldYAxis()
        val cameraPosition = getWorldPosition()
        val cameraTarget = Vector3f(cameraPosition).sub(getWorldZAxis())
        viewMatrix = Matrix4f().lookAt(cameraPosition, cameraTarget, up)
        return viewMatrix
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fieldOfView, aspectRatio, nearPlane, farPlane)
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniform("view_matrix", getCalculateViewMatrix(),false)
        shader.setUniform("projection_matrix", getCalculateProjectionMatrix(), false)
    }
}

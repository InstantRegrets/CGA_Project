package cga.exercise.components.camera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.Phase
import cga.framework.Random
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.*

class TronCamera(
    private val fieldOfView: Float = 0.5f * PI.toFloat(),
    private val aspectRatio: Float = 16f / 9f,
    private val nearPlane: Float = 0.1f,
    private val farPlane: Float = 3000f,
    parent: Transformable = Transformable(),
    val camTarget: Transformable = Transformable()
): Transformable(parent = parent), ICamera {
    var viewMatrix: Matrix4f = Matrix4f()
    var i = 0
    private var shake = 0f
    private var transitionDuration = 0f
    private var transitionStartTime = 0f
    private val transitionPos = Vector3f(0f)
    init {
        translateLocal(Vector3f(0f, 50f, -350f)) // since the ground is a bit below 0
        transitionPos.set(-2f,4f,8f).sub(this.getPosition())
    }

    override fun getCalculateViewMatrix(): Matrix4f {
        val up = getWorldYAxis()
        val cameraPosition = getWorldPosition()
        val cameraTarget = camTarget.getWorldPosition()
        // cameraTarget.normalize()
        cameraTarget.add(0f,4.0f,0f)
        cameraTarget.add(Random.nextVec3(-0.1f,0.1f).mul(shake))
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

    fun ease(x:Float): Float {
        val c = (x*x)*(3-2*x)
        return (max(0f,min(1f,1-abs(2*c-1))))
    }
    fun gausEase(x:Float): Float {
        val exponent = -0.5f*(((x-8f)/2f).pow(2f))
        val mul = (1f/(2f*sqrt(2f*PI.toFloat())))
        return mul * exp(exponent)
    }

    fun update(dt: Float, time: Float){
        shake = max(0f,shake-0.1f*dt)
        val e = gausEase((time - transitionStartTime))
        translateLocal(Vector3f(transitionPos).mul(dt * e))
    }


    fun switchPhase(phase: Phase, beat: Float){
        when(phase){
            Phase.Day -> {
                transitionPos.set(Vector3f(-2f,4f,6f).sub(this.getPosition()))
                transitionStartTime = beat
                transitionDuration = 4f
                shake = 0.3f
            }
            Phase.Night -> {
                transitionPos.set(Vector3f(2f,4f,6f).sub(this.getPosition()))
                transitionStartTime = beat
                transitionDuration = 8f

            }
            Phase.Chaos -> {
                transitionPos.set(Vector3f(0f,4f,7f).sub(this.getPosition()))
                transitionStartTime = beat
                transitionDuration = 8f
                shake = 1.0f
            }
        }

    }
}

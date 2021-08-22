package cga.exercise.game

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.DepthShader
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.CustomModel
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.exercise.game.gameObjects.phaseAmount
import cga.framework.GameWindow
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI

class Sun(
    private val songLength: Float
): GameObject, Transformable() {

    val model = CustomModel("orb")
    val light = SpotLight(
        color = Vector3f(1f, 1f, 1f),
        attenuation = Vector3f(1f, 0f, 0f),
        outerCone = 0.6f * PI.toFloat(),
        innerCone = 0.4f *PI.toFloat(),
        near_plane = 0f,
        far_plane = 1200f,
        shadowQuadSize = 320f,
    )

    init {
        model.renderable.parent = this
        light.parent = this
        light.shadowQuadSize = 320f
        model.renderable.pulseStrength = 10f
        translateLocal(Vector3f(50f,400f,0f))
        scaleLocal(Vector3f(35f))
        light.rotateLocal(-1.5f * PI.toFloat(),0f,0f)
        rotateAroundPoint(0f,0f,(-0.5* PI).toFloat(), Vector3f())
        rotateAroundPoint(0f,0.4f,0f,Vector3f())
    }

    override fun draw(shaderProgram: ShaderProgram) {
        if (shaderProgram !is DepthShader)
            model.renderable.render(shaderProgram)
    }

    private val rotPerSecond = (2f* PI.toFloat()) / (songLength / phaseAmount)
    override fun update(dt: Float, beat: Float) {
        rotateAroundPoint(0f, 0f, rotPerSecond*dt,  Vector3f(0f))
    }

    override fun processInput(window: GameWindow, dt: Float) {

    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        light.bind(shaderProgram, viewMatrix4f)
    }


    override fun switchPhase(phase: Phase) {
        when(phase){
            Phase.Day -> {
                light.color.set(0.5f,0.25f,0f)
                model.renderable.emitColor.set(1f,1f,0f)
                model.renderable.pulseStrength = 80f
            }
            Phase.Night -> {
                light.color.set(0.1f,0.1f,0.1f)
                model.renderable.emitColor.set(0.3f,0.3f,0.3f)
                model.renderable.pulseStrength = 0f
            }
            Phase.Chaos -> {
                light.color.set(0.8f,0f,0f)
                model.renderable.emitColor.set(0.8f,0f,0f)
                model.renderable.pulseStrength = 400f
            }
        }
    }
}

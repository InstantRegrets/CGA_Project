package cga.exercise.game

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.CustomModel
import cga.exercise.game.gameObjects.GameObject
import cga.framework.GameWindow
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI

class Sun(
    val near_plane: Float = 10.0f,
    val far_plane: Float = 180f
): GameObject, Transformable() {
    private val target = Transformable()

    var lightView = Matrix4f()
    var projectionMatrix = Matrix4f()
    val model = CustomModel("orb")
    val light = SpotLight(
        color = Vector3f(1f, 1f, 1f),
        attenuation = Vector3f(1f, 0f, 0f),
        outerCone = 0.6f * PI.toFloat(),
        innerCone = 0.6f*PI.toFloat(),
    )

    init {
        target.translateLocal(getWorldZAxis().negate())
        target.parent = this
        model.renderable.parent = this
        light.parent = this
        translateLocal(Vector3f(0f,50f,0f))
        scaleLocal(Vector3f(10f))
        rotateLocal(-1* PI.toFloat(),0f,0f)
    }

    fun bindShadowViewMatrix(shaderProgram: ShaderProgram){
        val up = getWorldYAxis()
        val position = getWorldPosition()
        lightView = Matrix4f().lookAt(position, Vector3f(0f), up)
        // // todo debug only
        // lightView = Matrix4f().lookAt(
        //     Vector3f(0f,50f, -10f),
        //     Vector3f(0f),
        //     Vector3f(0f,1f,0f)
        // )

        // todo understand
        // todo if this is n, the resolution may must be 2^n?
        projectionMatrix =  Matrix4f().ortho(-10f,10f,-10f,10f, near_plane, far_plane)
        shaderProgram.setUniform("lightProjection", projectionMatrix)
        shaderProgram.setUniform("lightView", lightView)
    }

    override fun draw(shaderProgram: ShaderProgram) {
        model.renderable.render(shaderProgram)
    }

    override fun update(dt: Float, beat: Float) {
        // todo rotate around world
    }

    override fun processInput(window: GameWindow, dt: Float) {

    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        light.bind(shaderProgram, viewMatrix4f)
    }
}
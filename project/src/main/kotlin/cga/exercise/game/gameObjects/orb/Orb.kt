package cga.exercise.game.gameObjects.orb

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.CustomModel
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.framework.GameWindow
import cga.framework.Random
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import kotlin.math.*

class Orb(pParent: Transformable?):Transformable(parent = pParent), GameObject {
    companion object OrbFactory{
        val meshes = CustomModel("orb").renderable.meshes
        val encMeshes = CustomModel("orb_enc").renderable.meshes
        fun createOrbs(n: Int, pParent: Transformable?): Collection<GameObject>{
            val result = mutableListOf<GameObject>()
            for (i in 1 until n){
                result.add(Orb(pParent))
            }
            return result
        }
    }
    private val baseColor = Random.nextColor()
    private val color = Vector3f(baseColor)
    private val wireFrameRend = Renderable(encMeshes,parent = this, emitColor = Vector3f(baseColor).mul(0.4f), pulseStrength = 0.0f)
    private val rend1 = Renderable(meshes,parent = this, emitColor = color, pulseStrength = 0.2f)
    private val rend2 = Renderable(meshes,parent = this, emitColor = color, pulseStrength = 0.2f)
    private val rend3 = Renderable(meshes,parent = this, emitColor = color, pulseStrength = 0.2f)
    private val renderables = arrayOf(rend1,rend2,rend3)
    private val rend1Axis = Vector3f(0f,0f,-1f)
    private val rend2Axis = Vector3f(1f,0f,0f)
    private val rend3Axis = Vector3f(0f,-1f,0f)


    val light = PointLight(color, Vector3f(1f, 0.8f, 1.0f))
    val direction = Random.nextVec3(-2f*PI.toFloat(), 2f* PI.toFloat())
    var basespeed = 0.5f
    var jumpSpeed = 0.0f

    init {
        wireFrameRend.scaleLocal(Vector3f(0.8f))
        renderables.forEach { it.scaleLocal(Vector3f(0.2f)) }
        rend1.translateLocal(Vector3f(1f,0f,0f))
        rend2.translateLocal(Vector3f(0f,2f,0f))
        rend3.translateLocal(Vector3f(0f,0f,3f))
        light.parent = this
        translateLocal(Vector3f(0f,Random.nextFloat(8f,15f),0f)) // minimum camera offset
    }

    override fun draw(shaderProgram: ShaderProgram) {
        wireFrameRend.render(shaderProgram)
        for (r in renderables){
            r.render(shaderProgram)
        }
    }

    override fun update(dt: Float, beat: Float) {
        val speed = basespeed + jumpSpeed* (beat%1 - 0.5f).pow(2)
        rotateAroundPoint(
             direction.x * dt * speed,
             direction.y * dt * speed,
             direction.z * dt * speed,
             Vector3f(0f)
         )
        direction.x = dir(direction.x)
        direction.y = dir(direction.y)
        direction.z = dir(direction.z)
        rend1.rotateGlobalAxis(8*dt + 8f * basespeed*dt, rend1Axis)
        rend2.rotateGlobalAxis(8*dt + 6f* basespeed*dt, rend2Axis)
        rend3.rotateGlobalAxis(8*dt + 4f* basespeed*dt, rend3Axis)

        val colorStrength = 0.4f*sin((2f*PI.toFloat()*(beat-0.5f)-0.5f* PI.toFloat()))+0.6f
        baseColor.mul(colorStrength,color)
    }

    private fun dir(float: Float): Float {
        return max(
            min(float + Random.nextFloat(-0.1f,0.1f), 2f* PI.toFloat()),
            -2f* PI.toFloat()
        )
    }

    override fun processInput(window: GameWindow, dt: Float) {
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        light.bind(shaderProgram,viewMatrix4f)
    }
    override fun switchPhase(phase: Phase) {
        when(phase){
            Phase.Day -> {
                basespeed = 0.4f
                jumpSpeed = 0f
                renderables.forEach { it.pulseStrength = Random.nextFloat(0.02f) }
            }
            Phase.Night -> {
                basespeed = 0.3f
                jumpSpeed = 0f
                renderables.forEach { it.pulseStrength = Random.nextFloat(0.05f) }
            }
            Phase.Chaos -> {
                renderables.forEach { it.pulseStrength = Random.nextFloat(0.05f,0.06f) }
                basespeed = 1f
                jumpSpeed = 0.5f
            }
        }
    }
}

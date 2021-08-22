package cga.exercise.game.gameObjects.laser

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.exercise.game.level.Event
import cga.framework.Colors
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.Matrix4f
import org.joml.Vector3f

abstract class Laser : Transformable(), GameObject  {
    val color: Vector3f = Vector3f(0f)
    val renderable: Renderable = getRenderable(color).also { it.parent = this }
    private var fade = false

    companion object{
        val mesh = ModelLoader.loadModel("assets/models/Environment/Laser/laser.obj",0f,0f,0f)!!.meshes
        fun getRenderable(color: Vector3f): Renderable {
            return Renderable(mesh, emitColor = color)
        }
    }

    override fun draw(shaderProgram: ShaderProgram) {
         if(color != Vector3f())
            renderable.render(shaderProgram)
    }

    private val fadeVec = Vector3f()
    fun fade(dt: Float){
        if (fade) {
            fadeVec.set(1.1f*dt)
            color.sub(fadeVec).max(Vector3f())
        }
    }

    abstract fun updatePos(dt: Float, beat: Float)
    override fun update(dt: Float, beat: Float) {
        fade(dt)
        updatePos(dt,beat)
    }


    override fun processInput(window: GameWindow, dt: Float) { }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) { }

    override fun switchPhase(phase: Phase) {
        when (phase){
            Phase.Day -> { renderable.vibeStrength = 0f; renderable.pulseStrength = 0.01f }
            Phase.Night -> { renderable.vibeStrength = 0f; renderable.pulseStrength = 0f }
            Phase.Chaos -> { renderable.vibeStrength = 2f; renderable.pulseStrength = 0.1f }
        }
    }

    fun fire(effect: Event.Effect){
        when(effect){
            Event.Effect.LightOff -> { color.set(0f,0f,0f); fade = false }
            Event.Effect.LightOnBlue -> { color.set(Colors.blue); fade = false }
            Event.Effect.LightFlashBlue -> { color.set(Colors.blueFlash); fade = true }
            Event.Effect.LightFadeBlue -> { color.set(Colors.blue); fade = true }
            Event.Effect.LightOnRed -> { color.set(Colors.red); fade = false }
            Event.Effect.LightFlashRed -> { color.set(Colors.redFlash); fade = true }
            Event.Effect.LightFadeRed -> { color.set(Colors.red); fade = true }
        }
    }
}

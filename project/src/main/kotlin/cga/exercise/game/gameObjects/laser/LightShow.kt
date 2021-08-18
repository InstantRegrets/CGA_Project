package cga.exercise.game.gameObjects.laser

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.exercise.game.level.Event
import cga.framework.GameWindow
import org.joml.Matrix4f
import org.joml.Vector3f

class LightShow: GameObject {
    val backlight = arrayOf(BackLaser(true), BackLaser(false))
    val ringLight = arrayOf(RingLight(true), RingLight(false))
    val leftRotatingLaser = arrayOf(RotatingLaser(true,0f), RotatingLaser(true,1f))
    val rightRotatingLaser = arrayOf(RotatingLaser(false,0f), RotatingLaser(false,-1f))
    val centerLight = arrayOf(CenterLight(false), CenterLight(true))
    val lights = arrayOf(backlight, ringLight, leftRotatingLaser, rightRotatingLaser, centerLight)

    fun fire(event: Event) {
        val firingLights = when(event.light){
            Event.Type.BackLasers -> backlight
            Event.Type.RingLights -> ringLight
            Event.Type.LeftRotatingLasers -> leftRotatingLaser
            Event.Type.RightRotationgLasers -> rightRotatingLaser
            Event.Type.CenterLights -> centerLight
        }
        firingLights.forEach{ it.fire(event.effect)}
    }

    fun avgColor(): Vector3f {
        val colors = lights.flatMap { it.map { laser -> laser.color  } }
        val size = colors.size.toFloat()
        val o = Vector3f()
        for(l in colors){
            o.add(l)
        }
        o.div(size)
        println("size: $size, o: $o")
        return o.div(size)
    }


    override fun draw(shaderProgram: ShaderProgram) {
        lights.forEach { it.forEach { laser -> laser.draw(shaderProgram) } }
    }

    override fun update(dt: Float, beat: Float) {
        lights.forEach { it.forEach { laser -> laser.update(dt, beat) } }
    }

    override fun processInput(window: GameWindow, dt: Float) {
        lights.forEach { it.forEach { laser -> laser.processInput(window, dt) } }
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        lights.forEach { it.forEach { laser -> laser.processLighting(shaderProgram, viewMatrix4f) } }
    }

    override fun switchPhase(phase: Phase) {
        lights.forEach { it.forEach { laser -> laser.switchPhase(phase) } }
    }


}

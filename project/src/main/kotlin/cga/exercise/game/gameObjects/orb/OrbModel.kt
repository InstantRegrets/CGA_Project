package cga.exercise.game.gameObjects.orb

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.CustomModel
import cga.framework.Random
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.pow

class OrbModel(val color: Vector3f): CustomModel(
    "orb"
) {

    init{
        renderable.pulseStrength = Random.nextFloat(0.02f)
    }
    fun draw(shaderProgram: ShaderProgram) {
        renderable.render(shaderProgram)

        // glitchRenderable.translateLocal(translation)
        // glitchRenderable.emitColor.set(Random.nextColor())
        // glitchRenderable.render(shaderProgram)
        // translation.mul(-1f)
        // glitchRenderable.translateLocal(translation)
    }

    fun update(dt: Float, beat: Float){
        val speed = (16f * (beat%1 - 0.5f).pow(4))
        val movement = dt * speed + 0.3f
        val colorMultiplier = max(0.4f, speed)
        color.mul(colorMultiplier, renderable.emitColor)
    }
}

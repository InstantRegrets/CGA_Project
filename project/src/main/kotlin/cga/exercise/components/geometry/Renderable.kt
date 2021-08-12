package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.GBufferShader
import cga.exercise.game.ShadowShader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GLUtil
import java.lang.Float.max
import kotlin.math.pow

class Renderable(
    val meshes: MutableList<Mesh>,
    modelMatrix: Matrix4f = Matrix4f(),
    parent: Transformable? = null,
    var emitColor: Vector3f = Vector3f(1f),
    var pulseStrength: Float= 0f,
):
    IRenderable, Transformable(modelMatrix, parent) {

    override fun render(shaderProgram: ShaderProgram) {
        // todo not working atm
        // val color = Vector3f(emitColor)
        // shaderProgram.setUniform("emitColor",color)
        // todo NO, BAD BASTI!!!!
        val bm: Boolean
        if(shaderProgram is GBufferShader){
            shaderProgram.setUniform("pulseStrength",pulseStrength)
            bm = true
        }else {
            bm = false
        }

        val mm = getWorldModelMatrix()
        shaderProgram.setUniform("model_matrix",mm)
        meshes.forEach{
            it.render(shaderProgram, bm)
        }
    }
}

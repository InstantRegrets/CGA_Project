package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GLUtil
import java.lang.Float.max
import kotlin.math.pow

class Renderable(
    val meshes: MutableList<Mesh>,
    modelMatrix: Matrix4f = Matrix4f(),
    parent: Transformable? = null,
    var emitColor: Vector3f = Vector3f(1f)
):
    IRenderable, Transformable(modelMatrix, parent) {

    private fun ease(x: Float,pow: Float = 8f):Float = 1f + 80f*((x-0.5f).pow(pow))
    private fun pulse(x: Float, intensity: Float = 2f):Float = max((x*intensity-intensity/2f).pow(2f) + 0.25f,1f)

    init {
    }

    fun render(shaderProgram: ShaderProgram, addMatrix4f: Matrix4f? = null, beat: Float? = null) {

        val color = Vector3f(emitColor)
        val mm = getWorldModelMatrix()
        if (addMatrix4f != null)
            mm.mul(addMatrix4f)

        if(beat != null){
            val pow = when{
                (beat < 0.5f || beat > 3.5f) ->  9f
                (beat < 2.5f && beat > 1.5f) ->  10f
                else -> 11f
            }

            val beatM = Matrix4f().scale(ease(beat % 1,pow))
            color.mul(pulse(beat%1))
            mm.mul(beatM)
        }

        shaderProgram.setUniform("model_matrix",mm)
        //shaderProgram.setUniform("emitColor",color) TODO
        meshes.forEach{
            it.render(shaderProgram)
        }
    }
    override fun render(shaderProgram: ShaderProgram){
        render(shaderProgram, null, null)
    }
}

package cga.exercise.game.note

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import kotlin.math.pow

class Note(
    val data: NoteData,
    val shader: ShaderProgram
) {
    private val model = NoteModel(data.key)
    private val lighting = NoteLigthing()

    private fun pulse(x: Float):Float = (0.1f).pow(x.pow(2))

    fun update(beat: Float){
        val x = pulse(data.beat - beat)
        val pulseMat = Matrix4f().scale(x)
        // todo lighting.update(shader)
        model.update(shader, pulseMat)
    }
}
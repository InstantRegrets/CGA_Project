package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class Renderable(
    val meshes: MutableList<Mesh>,
    modelMatrix: Matrix4f = Matrix4f(),
    parent: Transformable? = null,
    var emitColor: Vector3f = Vector3f(1f),
    var pulseStrength: Float= 0f,
    var vibeStrength: Float= 1f,
):
    IRenderable, Transformable(modelMatrix, parent) {

    override fun render(shaderProgram: ShaderProgram) {
        val color = Vector3f(emitColor)
        if (shaderProgram.targetEmitColor)
            shaderProgram.setUniform("emitColor",color)
        if(shaderProgram.targetPulseStrength)
            shaderProgram.setUniform("pulseStrength",pulseStrength)
        if(shaderProgram.targetVibeStrength)
            shaderProgram.setUniform("vibeStrength",vibeStrength)

        val mm = getWorldModelMatrix()
        shaderProgram.setUniform("model_matrix",mm)
        meshes.forEach{
            it.render(shaderProgram)
        }
    }
}

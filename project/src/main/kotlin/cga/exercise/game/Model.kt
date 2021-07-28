package cga.exercise.game

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11

val defaultOBJAttributes = arrayOf(
    VertexAttribute(3, GL11.GL_FLOAT,  32, 0), //Position
    VertexAttribute(2, GL11.GL_FLOAT,  32, 12),  //Tex
    VertexAttribute(3, GL11.GL_FLOAT,  32, 20)  //Normals
)

interface Model {
    val renderable: Renderable
    val emitColor: Vector3f

    fun update(shaderProgram: ShaderProgram, addMatrix4f: Matrix4f? = null) {
        renderable.emitColor = emitColor
        renderable.render(shaderProgram, addMatrix4f)
    }
}


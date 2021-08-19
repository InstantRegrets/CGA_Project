package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.lwjgl.opengl.GL33.*

class Quad{
    //These are just IDs
    private var vao: Int //the attribute specifications
    private var vbo: Int //our actual vertices
    private val vertexData:FloatArray = floatArrayOf(
        // positions        // texture Coords
        -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
        -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
        1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
        1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
    )
    private val attributes = arrayOf(
        VertexAttribute(3, GL_FLOAT, 20, 0), //Position
        VertexAttribute(2, GL_FLOAT, 20, 12),  //Tex
    )

    init {
        //generating ID's
        vao = glGenVertexArrays()
        vbo = glGenBuffers()

        //binding Buffers, order is irrelevant, as long as you don't bind the same target
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        //upload our data
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)

        //Creating attributes & enabling them
        for ((i, a) in attributes.withIndex()) {
            glVertexAttribPointer(i, a.n, a.type, false, a.stride, a.offset)
            glEnableVertexAttribArray(i)
        }

        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0)
    }

    fun draw(shaderProgram: ShaderProgram){
        glBindVertexArray(vao)
        //use in constructor specified draw mode, to draw a maximum of <indexCount> vertices, starting with index 0
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glBindVertexArray(0) //cleanup
    }
}

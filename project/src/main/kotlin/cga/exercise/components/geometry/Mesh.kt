package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import org.lwjgl.opengl.GL30.*

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexData plain float array of vertex data
 * @param indexData  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */
//I hate yellow lines, ik I could directly call genVertexArray() / genBuffers() on vao,vbo and ibo, but doing this for "readability"
@Suppress("JoinDeclarationAndAssignment")
class Mesh(
    vertexData: FloatArray,
    indexData: IntArray,
    attributes: Array<VertexAttribute>,
    val material: Material,
    private val drawMode: Int = GL_TRIANGLES //default to Triangles
) {
    //These are just IDs
    private var vao: Int //the attribute specifications
    private var vbo: Int //our actual vertices
    private var ibo: Int //indices
    private val indexCount = indexData.size

    init {
        //generating ID's
        vao = glGenVertexArrays()
        vbo = glGenBuffers()
        ibo = glGenBuffers()

        //binding Buffers, order is irrelevant, as long as you don't bind the same target
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)

        //upload our data
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_STATIC_DRAW)

        //Creating attributes & enabling them
        for ((i, a) in attributes.withIndex()) {
            glVertexAttribPointer(i, a.n, a.type, false, a.stride, a.offset)
            glEnableVertexAttribArray(i)
        }

        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0)
    }

    /**
     * renders the mesh
     */
    fun render(shaderProgram: ShaderProgram) {

        //bind our attributes
        if (shaderProgram.targetMaterial)
            material.bind(shaderProgram)
        glBindVertexArray(vao)
        //use in constructor specified draw mode, to draw a maximum of <indexCount> vertices, starting with index 0
        glDrawElements(drawMode, indexCount, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0) //cleanup
    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (ibo != 0) glDeleteBuffers(ibo)
        if (vbo != 0) glDeleteBuffers(vbo)
        if (vao != 0) glDeleteVertexArrays(vao)
    }
}

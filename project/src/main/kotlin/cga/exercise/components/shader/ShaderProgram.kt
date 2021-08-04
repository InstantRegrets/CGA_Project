package cga.exercise.components.shader

import cga.framework.GLError
import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by Fabian on 16.09.2017.
 * Creates a shader object from vertex and fragment shader paths
 * @param vertexShaderPath      vertex shader path
 * @param fragmentShaderPath    fragment shader path
 * @throws Exception if shader compilation failed, an exception is thrown
 */
open class ShaderProgram(vertexShaderPath: String, fragmentShaderPath: String) {
    var programID: Int = 0
    var vShaderId: Int = 0
    var fShaderId: Int = 0
    //Matrix buffers for setting matrix uniforms. Prevents allocation for each uniform
    private val m4x4buf: FloatBuffer = BufferUtils.createFloatBuffer(16)
    private val v2fBuf: FloatBuffer = BufferUtils.createFloatBuffer(2)
    /**
     * Sets the active shader program of the OpenGL render pipeline to this shader
     * if this isn't already the currently active shader
     */
    fun use() {
        val curProg = glGetInteger(GL_CURRENT_PROGRAM)
        if (curProg != programID) glUseProgram(programID)

    }

    /**
     * Frees the allocated OpenGL objects
     */
    fun cleanup() {
        glDeleteProgram(programID)
    }

    //setUniform() functions are added later during the course
    // float vector uniforms
    /**
     * Sets a single float uniform
     * @param name  Name of the uniform variable in the shader
     * @param value Value
     * @return returns false if the uniform was not found in the shader
     */
    private fun setUniform(name: String, f: (i: Int) -> Unit):Boolean{
        if (programID == 0) return false
        val loc = glGetUniformLocation(programID, name)
        if (loc != -1) {
            f(loc)
            GLError.checkThrow("Error setting Uniform: $name at location $loc")
            return true
        }
        println("Error setting uniform $name. Error:${(glGetError())}")
        return false
    }

    fun setUniform(name: String, value: Float): Boolean =
        setUniform(name){ glUniform1f(it, value) }

    fun setUniform(name: String, value: Vector2f): Boolean =
        setUniform(name){ glUniform2f(it, value.x, value.y) }

    fun setUniform(name: String, value: Int) =
        setUniform(name){ glUniform1i(it, value) }

    fun setUniform(name:String, value: Matrix4f, transpose: Boolean = false) =
        setUniform(name){ glUniformMatrix4fv(it,transpose,value.get(m4x4buf)) }

    fun setUniform(name: String, value: Vector3f) =
        setUniform(name){ glUniform3f(it, value.x, value.y, value.z) }


    init {
        val vPath = Paths.get(vertexShaderPath)
        val fPath = Paths.get(fragmentShaderPath)
        val vSource = String(Files.readAllBytes(vPath))
        val fSource = String(Files.readAllBytes(fPath))
        val vShader = glCreateShader(GL_VERTEX_SHADER)
        vShaderId = vShader
        if (vShader == 0) throw Exception("Vertex shader object couldn't be created.")
        val fShader = glCreateShader(GL_FRAGMENT_SHADER)
        fShaderId = fShader
        if (fShader == 0) {
            glDeleteShader(vShader)
            throw Exception("Fragment shader object couldn't be created.")
        }
        glShaderSource(vShader, vSource)
        glShaderSource(fShader, fSource)
        glCompileShader(vShader)
        if (glGetShaderi(vShader, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(vShader)
            glDeleteShader(fShader)
            glDeleteShader(vShader)
            throw Exception("Vertex shader compilation failed:\n$log")
        }
        glCompileShader(fShader)
        if (glGetShaderi(fShader, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(fShader)
            glDeleteShader(fShader)
            glDeleteShader(vShader)
            throw Exception("Fragment shader compilation failed:\n$log")
        }
        programID = glCreateProgram()
        if (programID == 0) {
            glDeleteShader(vShader)
            glDeleteShader(fShader)
            throw Exception("Program object creation failed.")
        }
        glAttachShader(programID, vShader)
        glAttachShader(programID, fShader)
        glLinkProgram(programID)
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            val log = glGetProgramInfoLog(programID)
            glDetachShader(programID, vShader)
            glDetachShader(programID, fShader)
            glDeleteShader(vShader)
            glDeleteShader(fShader)
            throw Exception("Program linking failed:\n$log")
        }
        glDetachShader(programID, vShader)
        glDetachShader(programID, fShader)
        glDeleteShader(vShader)
        glDeleteShader(fShader)
    }
}
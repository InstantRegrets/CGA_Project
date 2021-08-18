package cga.exercise.components.shader

import cga.framework.GLError
import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL40.*
import java.io.File
import java.nio.FloatBuffer

/**
 * Created by Fabian on 16.09.2017.
 * Creates a shader object from vertex and fragment shader paths
 * @param vertexShaderPath      vertex shader path
 * @param fragmentShaderPath    fragment shader path
 * @throws Exception if shader compilation failed, an exception is thrown
 */
open class ShaderProgram(vertexShaderPath: String, fragmentShaderPath: String, geometryShaderPath: String? = null) {
    var programID: Int = 0
    open val targetMaterial = true
    open val targetPulseStrength = true
    open val targetEmitColor = true
    open val targetVibeStrength = true

    var vShaderId: Int = 0
    var fShaderId: Int = 0
    var gShaderId: Int = 0
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

    private fun createShader(shaderType: Int, shaderPath: String): Int {
        val source = File(shaderPath).readText()
        val shader = glCreateShader(shaderType)
        if (shader == 0) throw Exception ("${glGetString(shaderType)} couldn't be created")
        glShaderSource(shader, source)
        glCompileShader(shader)
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(shader)
            glDeleteShader(shader)
            throw Exception("${glGetString(shaderType)} shader compilation failed:\n$log")
        }
        return shader
    }

    init {
        // to ensure we allow both normal and shader with a geometry option, the geomPath can be null
        // this makes this init a little cumbersome to work with, but we solve it by using kotlin's let binding
        val vShader = createShader(GL_VERTEX_SHADER, vertexShaderPath)
        val gShader = geometryShaderPath?.let { createShader(GL_GEOMETRY_SHADER, geometryShaderPath) }
        val fShader = createShader(GL_FRAGMENT_SHADER, fragmentShaderPath)
        programID = glCreateProgram()
        if (programID == 0) {
            glDeleteShader(vShader)
            glDeleteShader(fShader)
            throw Exception("Program object creation failed.")
        }
        glAttachShader(programID, vShader)
        gShader?.let { glAttachShader(programID, gShader) }
        glAttachShader(programID, fShader)
        glLinkProgram(programID)
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            val log = glGetProgramInfoLog(programID)
            glDetachShader(programID, vShader)
            gShader?.let{ glDetachShader(programID, gShader) }
            glDetachShader(programID, fShader)
            glDeleteShader(vShader)
            glDeleteShader(fShader)
            throw Exception("Program linking failed:\n$log")
        }
        glDetachShader(programID, vShader)
        gShader?.let{ glDetachShader(programID, gShader) }
        glDetachShader(programID, fShader)
        glDeleteShader(vShader)
        glDeleteShader(fShader)
        gShader?.let { glDeleteShader(gShader) }
    }
}
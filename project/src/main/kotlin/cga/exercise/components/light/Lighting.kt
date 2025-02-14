package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

interface Lighting {
    fun update(shaderProgram: ShaderProgram, viewMatrix: Matrix4f)
}


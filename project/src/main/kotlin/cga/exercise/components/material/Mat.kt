package cga.exercise.components.material

import cga.exercise.components.shader.ShaderProgram

interface Mat{
    fun bind(shaderProgram: ShaderProgram)
}


package cga.exercise.components.shader

import org.lwjgl.opengl.GL30

class GBufferShader(): ShaderProgram(
    vertexShaderPath = "assets/shaders/components/shader/gVert.glsl",
    fragmentShaderPath = "assets/shaders/components/shader/gFrag.glsl"
) {
    init {
        //We actually dont need glBindFragDataLocation,
        //as this is done in the Fragment shader
    }

}
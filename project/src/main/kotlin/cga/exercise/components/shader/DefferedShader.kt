package cga.exercise.components.shader

import cga.exercise.components.shader.ShaderProgram

class DefferedShader: ShaderProgram(
    "assets/shaders/components/shader/deferredVert.glsl",
    "assets/shaders/components/shader/deferredFrag.glsl",
) {
}

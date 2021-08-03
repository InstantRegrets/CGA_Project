package cga.exercise.game

import cga.exercise.components.shader.ShaderProgram

class DefferedShader: ShaderProgram(
    "assets/shaders/components/shader/defferedVert.glsl",
    "assets/shaders/components/shader/defferedFrag.glsl",
) {
}

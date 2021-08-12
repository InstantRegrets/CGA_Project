package cga.exercise.game

import cga.exercise.components.shader.ShaderProgram

class ShadowShader: ShaderProgram(
    vertexShaderPath = "assets/shaders/components/shader/shadowVert.glsl",
    fragmentShaderPath = "assets/shaders/components/shader/shadowFrag.glsl"
)
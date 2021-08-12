package cga.exercise.game

import cga.exercise.components.shader.ShaderProgram

class GBufferShader(): ShaderProgram(
    vertexShaderPath = "assets/shaders/components/shader/gVert.glsl",
    geometryShaderPath = "assets/shaders/components/shader/gGeom.glsl",
    fragmentShaderPath = "assets/shaders/components/shader/gFrag.glsl"

)
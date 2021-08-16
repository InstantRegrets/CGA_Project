package cga.exercise.components.shader

class GeometryShader: ShaderProgram(
    vertexShaderPath ="assets/shaders/components/geometry/gVert.glsl",
    fragmentShaderPath = "assets/shaders/components/geometry/gFrag.glsl",
    geometryShaderPath = "assets/shaders/components/geometry/gGeom.glsl"
) {
    
}

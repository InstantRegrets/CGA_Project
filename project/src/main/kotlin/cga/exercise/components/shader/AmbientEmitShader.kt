package cga.exercise.components.shader

import org.joml.Vector2f

class AmbientEmitShader: ShaderProgram(
    vertexShaderPath = "assets/shaders/components/light/ambientEmitPassVert.glsl",
    fragmentShaderPath = "assets/shaders/components/light/ambientEmitPassFrag.glsl"
) {
    fun setup(width: Float, height: Float){
        use()
        setUniform("inDiffuse", 2)
        setUniform("inEmissive", 4)
        setUniform("screenSize", Vector2f(width,height))
    }
}

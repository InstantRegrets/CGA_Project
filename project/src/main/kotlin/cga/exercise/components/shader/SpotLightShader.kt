package cga.exercise.components.shader

import org.joml.Vector2f

class SpotLightShader: ShaderProgram(
    vertexShaderPath = "assets/shaders/components/light/spotLightPassVert.glsl",
    fragmentShaderPath = "assets/shaders/components/light/spotLightPassFrag.glsl"
) {
    fun setup(width: Float, height:Float){
        use()
        setUniform("inPosition", 0)
        setUniform("inNormal", 1)
        setUniform("inDiffuse", 2)
        setUniform("inSpecular", 3)
        setUniform("shadowMap",6)
        setUniform("shininess", 64f)
        setUniform("screenSize", Vector2f(width, height))
    }
}

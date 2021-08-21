package cga.exercise.components.shader

import org.joml.Vector2f

class PointLightShader:ShaderProgram(
    vertexShaderPath = "assets/shaders/components/light/lightingPassVert.glsl",
    fragmentShaderPath = "assets/shaders/components/light/lightingPassFrag.glsl"
) {
    fun setup(width: Float, height:Float){
        use()
        setUniform("inPosition", 0)
        setUniform("inNormal", 1)
        setUniform("inDiffuse", 2)
        setUniform("inSpecular", 3)
        //setUniform("inEmissive", 4)
        setUniform("shadowMap",6)
        setUniform("shininess", 64f)
        setUniform("screenSize", Vector2f(width, height))
    }
}

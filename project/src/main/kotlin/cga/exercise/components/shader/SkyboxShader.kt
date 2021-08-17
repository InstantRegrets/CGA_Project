package cga.exercise.components.shader

class SkyboxShader: ShaderProgram(
    vertexShaderPath = "assets/shaders/components/shader/skyboxVert.glsl",
    fragmentShaderPath = "assets/shaders/components/shader/skyboxFrag.glsl"
) {
    fun setup(){
        use()
        setUniform("skybox",0)
    }
}

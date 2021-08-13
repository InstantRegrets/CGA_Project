package cga.exercise.game

import cga.exercise.components.shader.ShaderProgram

class DeferredShader(): ShaderProgram(
    "assets/shaders/components/shader/deferredVert.glsl",
    "assets/shaders/components/shader/deferredFrag.glsl",
) {
    init {
        use()
        setUniform("inPosition", 0)
        setUniform("inNormal", 1)
        setUniform("inDiffuse", 2)
        setUniform("inSpecular", 3)
        setUniform("inEmissive", 4)
        setUniform("inShadow", 5)
        setUniform("shininess", 64f)
    }

}

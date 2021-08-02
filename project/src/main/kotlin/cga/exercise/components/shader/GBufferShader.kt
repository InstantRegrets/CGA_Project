package cga.exercise.components.shader

import org.lwjgl.opengl.GL30

class GBufferShader(): ShaderProgram(
    vertexShaderPath = "assets/shaders/components/shader/gVert.glsl",
    fragmentShaderPath = "assets/shaders/components/shader/gFrag.glsl"
) {
    init {
        //layout (location = 0) out vec3 gPosition;
        //layout (location = 1) out vec3 gNormal;
        //layout (location = 2) out vec4 gAlbedoSpec;
        //layout (location = 3) out vec3 gEmissive;
        GL30.glBindFragDataLocation(this.programID, 0,"gPosition")
        GL30.glBindFragDataLocation(this.programID, 1,"gNormal")
        GL30.glBindFragDataLocation(this.programID, 2,"gAlbedoSpec")
        GL30.glBindFragDataLocation(this.programID, 3,"gEmissive")
    }

}
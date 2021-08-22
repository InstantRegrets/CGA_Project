package cga.exercise.components.shader

import cga.exercise.components.texture.DepthMap
import cga.exercise.components.light.SpotLight
import cga.exercise.game.Scene
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL33.*

class DepthShader(val depthMap: DepthMap) : ShaderProgram(
    vertexShaderPath = "assets/shaders/components/shader/shadowVert.glsl",
    fragmentShaderPath = "assets/shaders/components/shader/shadowFrag.glsl",
    geometryShaderPath = "assets/shaders/components/shader/shadowGeom.glsl",
){
    override val targetMaterial: Boolean = false
    override val targetPulseStrength: Boolean = true
    override val targetEmitColor: Boolean = false
    override val targetVibeStrength: Boolean = false

    fun pass(spotLight: SpotLight, scene: Scene, beat: Float){
        glDisable(GL_BLEND)
        glCullFace(GL_FRONT)
        glDepthMask(true)

        use()
        glViewport(0, 0, depthMap.width, depthMap.height)
        glBindFramebuffer(GL_FRAMEBUFFER, depthMap.fbo)
        glClear(GL_DEPTH_BUFFER_BIT)

        setUniform("LightProjectionViewMatrix", spotLight.calcPVMatrix())
        setUniform("beat", beat)
        scene.gameObjects.forEach{ it.draw(this) }

        glBindFramebuffer(GL_FRAMEBUFFER, 0) //return to default

        glCullFace(GL_BACK)
        glViewport(0, 0, scene.window.windowWidth, scene.window.windowHeight)
        glDepthMask(false)
    }
}
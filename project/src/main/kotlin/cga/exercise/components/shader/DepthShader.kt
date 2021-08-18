package cga.exercise.components.shader

import cga.exercise.components.geometry.DepthMap
import cga.exercise.game.Scene
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
    init {
        setUniform("depthMap",6)
    }

    fun pass(scene: Scene, beat: Float){
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glCullFace(GL_FRONT)
        glDepthMask(true)

        use()
        glViewport(0, 0, depthMap.width, depthMap.height)
        glBindFramebuffer(GL_FRAMEBUFFER, depthMap.fbo)
        glClear(GL_DEPTH_BUFFER_BIT)

        scene.sun.bindShadowViewMatrix(this)
        setUniform("beat", beat)
        scene.gameObjects.forEach{ it.draw(this) }

        glBindFramebuffer(GL_FRAMEBUFFER, 0) //return to default

        glCullFace(GL_BACK)
        glViewport(0, 0, scene.window.windowWidth, scene.window.windowHeight)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glDepthMask(false)


    }
}
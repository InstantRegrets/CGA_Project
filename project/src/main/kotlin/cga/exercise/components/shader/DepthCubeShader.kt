package cga.exercise.components.shader

import cga.exercise.components.light.PointLight
import cga.exercise.components.texture.DepthCubemap
import cga.exercise.game.Scene
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL33.*

class DepthCubeShader(val cubemap: DepthCubemap) : ShaderProgram(
    vertexShaderPath = "assets/shaders/components/shader/shadowCubeVert.glsl",
    fragmentShaderPath = "assets/shaders/components/shader/shadowCubeFrag.glsl",
    geometryShaderPath = "assets/shaders/components/shader/shadowCubeGeom.glsl",
){
    override val targetMaterial: Boolean = false
    override val targetPulseStrength: Boolean = false
    override val targetEmitColor: Boolean = false
    override val targetVibeStrength: Boolean = false

    fun pass(pointLight: PointLight, scene: Scene, beat: Float){
        glDisable(GL_BLEND)
        glCullFace(GL_FRONT)
        glDepthMask(true)

        use()
        glViewport(0, 0, cubemap.width, cubemap.height)
        glBindFramebuffer(GL_FRAMEBUFFER, cubemap.fbo)
        glClear(GL_DEPTH_BUFFER_BIT)

        setUniform("LightProjectionViewMatrix", pointLight.calcPVMatrixArray(cubemap.aspect,scene.camera.viewMatrix))
        // setUniform("beat", beat)
        setUniform("farPlane", pointLight.farPlane)
        setUniform("lightPos", pointLight.getWorldPosition())
        scene.gameObjects.forEach{ it.draw(this) }

        glBindFramebuffer(GL_FRAMEBUFFER, 0) //return to default

        glCullFace(GL_BACK)
        glViewport(0, 0, scene.window.windowWidth, scene.window.windowHeight)
        glDepthMask(false)
        glEnable(GL_BLEND)
    }

}
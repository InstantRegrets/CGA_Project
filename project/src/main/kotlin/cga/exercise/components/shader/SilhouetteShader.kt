package cga.exercise.components.shader

import cga.exercise.game.Scene
import cga.framework.GLError
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

class SilhouetteShader: ShaderProgram(
    vertexShaderPath = "assets/shaders/components/shader/silhouetteVert.glsl",
    fragmentShaderPath = "assets/shaders/components/shader/silhouetteFrag.glsl",
) {
    override val targetEmitColor: Boolean = false
    override val targetPulseStrength: Boolean = false
    override val targetMaterial: Boolean = false

    fun pass(scene: Scene, beat: Float){
        use()
        glCullFace(GL_FRONT)
        glDepthMask(true)

        GLError.checkThrow()
        // scene.camera.bind(this)
        setUniform("outlineColor", Vector3f(0f,0f,0f))
        setUniform("normal_offset", 0.65f)
        scene.gameObjects.forEach { it.draw(this) }

        glCullFace(GL_BACK)
        glDepthMask(false)
        setUniform("outlineColor", Vector3f(1f,1f,1f))
        setUniform("normal_offset", 0.0f)
        scene.gameObjects.forEach { it.draw(this) }


        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()
        glDepthMask(true)
    }
}
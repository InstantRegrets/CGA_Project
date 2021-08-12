package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.DepthMap
import cga.exercise.components.geometry.GeometryBuffer
import cga.exercise.components.light.Light
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.sound.SoundContext
import cga.exercise.components.sound.SoundListener
import cga.exercise.components.texture.Skybox
import cga.exercise.game.environment.chaos.Environment
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.orb.Orb
import cga.exercise.game.gameObjects.player.Player
import cga.exercise.game.gameObjects.trees.CherryTree
import cga.exercise.game.level.Level
import cga.exercise.game.note.NoteKey
import cga.framework.GLError
import cga.framework.GameWindow
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.GLFW_KEY_F
import org.lwjgl.glfw.GLFW.GLFW_KEY_J
import org.lwjgl.opengl.GL33.*
import kotlin.math.PI
import kotlin.math.sin


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val camera: TronCamera
    private val quad = Quad()
    private val level: Level
    private val player = Player()
    private val sun = Sun()
    private val gameObjects: MutableList<GameObject> = mutableListOf()
    private val gBufferShader: ShaderProgram
    private val gBuffer: GeometryBuffer
    private val depthMap: DepthMap
    private val simpleDepthShader: ShaderProgram
    private val debugDepthQuad: ShaderProgram
    private val deferredShader: ShaderProgram
    private val skybox = Skybox.invoke("assets/textures/skyboxNight")
    private val skyboxShader: ShaderProgram

    //scene setup
    init {
        //initial opengl state
        glClearColor(0f, 0f, 0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        gBufferShader = GBufferShader()
        skyboxShader = ShaderProgram(
            vertexShaderPath = "assets/shaders/components/shader/skyboxVert.glsl",
            fragmentShaderPath = "assets/shaders/components/shader/skyboxFrag.glsl"
        )

        debugDepthQuad = ShaderProgram(
            vertexShaderPath = "assets/shaders/components/shader/debugVert.glsl",
            fragmentShaderPath = "assets/shaders/components/shader/debugFrag.glsl"
        )
        debugDepthQuad.use()
        debugDepthQuad.setUniform("depthMap",6)
        depthMap = DepthMap()
        simpleDepthShader = ShadowShader()
        simpleDepthShader.use()


        deferredShader = DeferredShader()
        gBuffer = GeometryBuffer(window)

        SoundContext.setup()


        // CAMERA
        camera = TronCamera()
        camera.rotateLocal(-0.65f, 0.0f, 0f)
        camera.translateLocal(Vector3f(0f, 0f, 4f))
        camera.parent = player.model.renderable

        //Sound and level
        level = Level()
        level.setup()
        skybox.setup()
        skyboxShader.use()
        skyboxShader.setUniform("skybox",0)
        //Setup uniforms for deferredShader (Texture locations = output of gbuffer)


        gameObjects.addAll(
            listOf(
                CherryTree(),
                Environment(),
                // Orb(), Orb(), Orb(), Orb(), Orb(), Orb(), Orb(), Orb(), Orb(), Orb(),
                level, player, sun,
            )
        )

    }

    fun render(dt: Float, t: Float) {
        shadowPass(dt,t)
        geometryPass(dt, t)
        lightingPass(dt, t)
        renderSkybox()
        SoundListener.setPosition(camera)
        GLError.checkThrow()
    }

    private fun geometryPass(dt: Float, t: Float){
        gBuffer.bind()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        gBufferShader.use()

        val beat = level.beatsPerSeconds * t
        gBufferShader.setUniform("beat",beat)
        camera.bind(gBufferShader)
        sun.bindShadowViewMatrix(gBufferShader)
        gameObjects.forEach{ it.draw(gBufferShader) }
        glBindFramebuffer(GL_FRAMEBUFFER, 0) //return to default
    }

    private fun shadowPass(dt: Float, t: Float) {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        simpleDepthShader.use()
        glViewport(0,0,depthMap.width, depthMap.height)
        glBindFramebuffer(GL_FRAMEBUFFER, depthMap.fbo)
        glClear(GL_DEPTH_BUFFER_BIT)

        sun.bindShadowViewMatrix(simpleDepthShader)
        gameObjects.forEach{it.draw(simpleDepthShader)}

        glBindFramebuffer(GL_FRAMEBUFFER, 0) //return to default

        glViewport(0,0,window.windowWidth,window.windowHeight)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    }

    private fun lightingPass(dt: Float, t: Float){
        //Lighting Pass
        //We can clear here, because we bound the default FBO again
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        deferredShader.use()
        gBuffer.bindTextures()
        GLError.checkThrow()

        glActiveTexture(GL_TEXTURE6)
        glBindTexture(GL_TEXTURE_2D, depthMap.texture)
        deferredShader.setUniform("shadowMap", 6)
        deferredShader.setUniform("near_plane", sun.near_plane)
        deferredShader.setUniform("far_plane", sun.far_plane)

        gameObjects.forEach { it.processLighting(deferredShader, camera.viewMatrix) }
        Light.bindAmount(deferredShader)
        // Bind screen for writing
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glEnable(GL_DEPTH_TEST)

        quad.draw(deferredShader)

        glBindFramebuffer(GL_READ_FRAMEBUFFER, gBuffer.gBufferID)
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)//write to default
        //copy the depth buffer from gbuffer to default
        glBlitFramebuffer(
            0,0, window.windowWidth, window.windowHeight,
            0,0, window.windowWidth, window.windowHeight,
            GL_DEPTH_BUFFER_BIT, GL_NEAREST
        )
        glBindFramebuffer(GL_FRAMEBUFFER, 0) //set both to default again
    }


    private fun renderSkybox() {
        glDepthFunc(GL_LEQUAL)
        skyboxShader.use()
        //We eliminate the translation part of the matrix
        val viewMatrix = Matrix4f(Matrix3f(camera.getCalculateViewMatrix()))
        skyboxShader.setUniform("view_matrix", viewMatrix)
        skyboxShader.setUniform("projection_matrix", camera.getCalculateProjectionMatrix())
        skybox.render()
        glDepthFunc(GL_LESS)
    }


    fun rainbow(vect: Vector3f, p: Float) {
        val r = (sin(p * 2 * PI + 0 / 3.0 * PI) / 2 + 0.5).toFloat()
        val g = (sin(p * 2 * PI + 2 / 3.0 * PI) / 2 + 0.5).toFloat()
        val b = (sin(p * 2 * PI + 4 / 3.0 * PI) / 2 + 0.5).toFloat()
        vect.x = r
        vect.y = g
        vect.z = b
    }

    fun update(dt: Float, t: Float) {
        val beat = level.beatsPerSeconds * t
        gameObjects.forEach{
            it.processInput(window, dt)
            it.update(dt, beat)
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if (key == GLFW_KEY_F && action == 1) {
            level.onKey(NoteKey.Left)
        }
        if (key == GLFW_KEY_J && action == 1) {
            level.onKey(NoteKey.Right)
        }
    }

    var x: Double = 0.0
    var y: Double = 0.0
    fun onMouseMove(xPos: Double, yPos: Double) {
        if (x == 0.0) {
            x = xPos
        } else {
            val diff = (x - xPos) * 0.002
            x = xPos
            // Bike parent von Camera
            // bike <- rotateAround <- local <- v
            camera.rotateAroundPoint(0f, diff.toFloat(), 0f, Vector3f(0f,0f,0f))
        }
          if (y == 0.0) {
              y = yPos
          } else {
              val diff = (y - yPos) * 0.002
              y = yPos
              // Bike parent von Camera
              // bike <- rotateAround <- local <- v
              camera.camTarget.rotateAroundPoint(diff.toFloat(), 0f, 0f,  Vector3f(0f,0f,0f))
          }
    }

    fun cleanup() {
        //level.cleanup()
        SoundContext.cleanup()
    }
}


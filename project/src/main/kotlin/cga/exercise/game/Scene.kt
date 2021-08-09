package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.GeometryBuffer
import cga.exercise.components.light.Light
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.sound.SoundContext
import cga.exercise.components.sound.SoundListener
import cga.exercise.components.texture.Skybox
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.player.Player
import cga.exercise.game.gameObjects.street.Street
import cga.exercise.game.gameObjects.trees.CherryTree
import cga.framework.GLError
import cga.framework.GameWindow
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import java.util.*
import kotlin.math.PI
import kotlin.math.sin


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val NR_POINT_LIGHTS: Int = 64
    private val sceneLights = sceneLights()
    private val camera: TronCamera
    private val quad = Quad()
    // private val level: Level
    private val gameObjects: MutableList<GameObject> = mutableListOf()
    private val gBufferShader: ShaderProgram
    private val gBuffer = GeometryBuffer(window)
    private val deferredShader: ShaderProgram
    private val skybox = Skybox.invoke("assets/textures/skybox")
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
        deferredShader = ShaderProgram(
            "assets/shaders/components/shader/deferredVert.glsl",
            "assets/shaders/components/shader/deferredFrag.glsl",
        )
        gBufferShader = ShaderProgram(
            vertexShaderPath = "assets/shaders/components/shader/gVert.glsl",
            fragmentShaderPath = "assets/shaders/components/shader/gFrag.glsl"
        )
        skyboxShader = ShaderProgram(
            vertexShaderPath = "assets/shaders/components/shader/skyboxVert.glsl",
            fragmentShaderPath = "assets/shaders/components/shader/skyboxFrag.glsl"
        )
        gameObjects.addAll(
            listOf(
                Player(),
                Street(),
                CherryTree()
            )
        )

        // CAMERA
        camera = TronCamera()
        camera.rotateLocal(-0.65f, 0.0f, 0f)
        camera.translateLocal(Vector3f(0f, 0f, 4f))
        camera.parent = (gameObjects.first() as Player).model.renderable

        //Sound and level
        SoundContext.setup()
        // level = Level("caramelldansen")
        // level.setup()        GLError.checkThrow("Failed init")
        skybox.setup()
        skyboxShader.use()
        skyboxShader.setUniform("skybox",0)
        //Setup uniforms for deferredShader (Texture locations = output of gbuffer)
        deferredShader.use()
        deferredShader.setUniform("inPosition", 0)
        deferredShader.setUniform("inNormal", 1)
        deferredShader.setUniform("inDiffuse", 2)
        deferredShader.setUniform("inSpecular", 3)
        deferredShader.setUniform("inEmissive", 4)
        deferredShader.setUniform("shininess", 64f)

    }

    fun render(dt: Float, t: Float) {
        //renderSkybox()
        deferredRender()
        renderSkybox()
        SoundListener.setPosition(camera)
        GLError.checkThrow()
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


    private fun deferredRender(){
        //Geometry pass into gBuffer
        gBuffer.bind()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        gBufferShader.use()
        camera.bind(gBufferShader)
        gameObjects.forEach{it.draw(gBufferShader)}
        glBindFramebuffer(GL_FRAMEBUFFER, 0) //return to default

        //Lighting Pass
        //We can clear here, because we bound the default FBO again
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        initLightRendering()
        deferredShader.use()
        gBuffer.bindTextures()
        GLError.checkThrow()

        gameObjects.forEach { it.processLighting(deferredShader, camera.viewMatrix) }
        Light.bindAmount(deferredShader)
        sceneLights.forEach { it.bind(deferredShader, camera.viewMatrix) }
        //level.update(dt, t)
        endLightRendering()
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

    private fun initLightRendering(){
        glDisable(GL_DEPTH_TEST)
    }
    private fun endLightRendering() {
        // Bind screen for writing
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glEnable(GL_DEPTH_TEST)
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
        gameObjects.forEach{
            it.processInput(window, dt)
            it.update(dt, t)
        }
        if (window.getKeyState(GLFW_KEY_L)){
            val l = sceneLights.last()
            l.cleanup()
            sceneLights.remove(l)

        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if (key == GLFW_KEY_W && action == 1) {
            // level.onKey(NoteKey.Left)
        }
        if (key == GLFW_KEY_E && action == 1) {
            // level.onKey(NoteKey.Right)
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
            camera.rotateAroundPoint(0f, diff.toFloat(), 0f, gameObjects.first().getPosition())
        }
        //  if (y == 0.0) {
        //      y = yPos
        //  } else {
        //      val diff = (y - yPos) * 0.002
        //      y = yPos
        //      // Bike parent von Camera
        //      // bike <- rotateAround <- local <- v
        //      camera.rotateAroundPoint(diff.toFloat(), 0f, 0f, gameObjects.first().getPosition())
        //  }
    }

    fun cleanup() {
        //level.cleanup()
        SoundContext.cleanup()
    }

    fun sceneLights(): MutableList<PointLight> {
        val l = arrayListOf<PointLight>()
        val r = Random(10)
        val rf = { r.nextFloat() * 20f - 10f }
        val ra = { r.nextFloat() * 0.5f }
        for (i in 1 until NR_POINT_LIGHTS) {
            val pl = PointLight(
                    Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat()),
                    Vector3f(1f, 0.15f, 0.12f),
                )
            pl.translateLocal(Vector3f(rf(), ra(), rf()))
            l.add(pl)
        }
        return l // .toList()
    }
}


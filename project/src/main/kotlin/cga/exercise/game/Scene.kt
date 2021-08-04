package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.light.LightMode
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.GBufferShader
import cga.exercise.components.sound.SoundContext
import cga.exercise.components.sound.SoundListener
import cga.exercise.game.environment.ground.Ground
import cga.exercise.game.player.Player
import cga.framework.GLError
import cga.framework.GameWindow
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL30.*
import java.util.*
import kotlin.math.PI
import kotlin.math.sin


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val NR_POINT_LIGHTS: Int = 8
    private val sceneLights = sceneLights()
    private val camera: TronCamera
    private var lightMode = LightMode.BlinnPhong
    private var player = Player()
    private val ground = Ground()
    private val quad = Quad()
    // private val level: Level
    private val gBufferShader = GBufferShader()
    private val gBuffer = GeometryBuffer(window)
    private val deferredShader = DefferedShader()

    //scene setup
    init {

        //initial opengl state
        glClearColor(0f, 0f, 0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glDisable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        // CAMERA
        camera = TronCamera()
        camera.rotateLocal(-0.65f, 0.0f, 0f)
        camera.translateLocal(Vector3f(0f, 0f, 4f))

        //Sound and level
        SoundContext.setup()
        // level = Level("caramelldansen")
        // level.setup()        GLError.checkThrow("Failed init")

        //Setup Uniforms
        deferredShader.use()


    }

    fun render(dt: Float, t: Float) {
        // geometry pass into gbuffer
        gBuffer.bind()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        gBufferShader.use()
        ground.update(gBufferShader)
        player.draw(gBufferShader)
        camera.bind(gBufferShader)
        glBindFramebuffer(GL_FRAMEBUFFER, 0) //return to default

        //Lighting Pass
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        initLightRendering()
        deferredShader.use()
        gBuffer.bindTextures()
        GLError.checkThrow()
        deferredShader.setUniform("inPosition", 0)
        deferredShader.setUniform("inNormal", 1)
        deferredShader.setUniform("inDiffuse", 2)
        deferredShader.setUniform("inSpecular", 3)
        deferredShader.setUniform("inEmissive", 4)
        deferredShader.setUniform("lightMode", lightMode.ordinal)
        deferredShader.setUniform("shininess", 64f)
        player.light(deferredShader, camera)
        sceneLights.forEach { it.bind(deferredShader, camera.viewMatrix) }
        //level.update(dt, t)
        quad.draw(deferredShader)
        endLightRendering()

        glBindFramebuffer(GL_FRAMEBUFFER, 0) //return to default

        SoundListener.setPosition(camera)
        GLError.checkThrow()
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
        if (window.getKeyState(GLFW.GLFW_KEY_L))
            lightMode = LightMode.BlinnPhong
        else
            lightMode = LightMode.Phong
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if (key == GLFW.GLFW_KEY_W && action == 1) {
            // level.onKey(NoteKey.Left)
        }
        if (key == GLFW.GLFW_KEY_E && action == 1) {
            // level.onKey(NoteKey.Right)
        }
    }

    var x: Double = 0.0
    fun onMouseMove(xPos: Double, yPos: Double) {
        if (x == 0.0) {
            x = xPos
        } else {
            val diff = (x - xPos) * 0.002
            x = xPos
            // Bike parent von Camera
            // bike <- rotateAround <- local <- v
            camera.rotateAroundPoint(0f, diff.toFloat(), 0f, Vector3f(0f))
        }
    }

    fun cleanup() {
        //level.cleanup()
        SoundContext.cleanup()
    }

    fun loadGround() {

    }


    fun sceneLights(): List<PointLight> {
        val l = arrayListOf<PointLight>()
        val r = Random(10)
        val rf = { r.nextFloat() * 20f - 10f }
        val ra = { r.nextFloat() * 0.5f }
        for (i in 1 until NR_POINT_LIGHTS) {
            l.add(
                PointLight(
                    i, Vector3f(rf(), ra(), rf()),
                    Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat()),
                    Vector3f(1f, 0.15f, 0.12f),
                )
            )
        }
        return l.toList()
    }
}


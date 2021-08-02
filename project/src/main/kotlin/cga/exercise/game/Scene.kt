package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.light.LightMode
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.GBufferShader
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.sound.SoundContext
import cga.exercise.components.sound.SoundListener
import cga.exercise.game.environment.ground.Ground
import cga.exercise.game.level.Level
import cga.exercise.game.note.NoteKey
import cga.exercise.game.player.Player
import cga.framework.GLError
import cga.framework.GameWindow
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer
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
    private val level: Level
    private val gBufferShader = GBufferShader()
    private val gBuffer = glGenFramebuffers()

    //scene setup
    init {

        //initial opengl state
        glClearColor(0f, 0f, 0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        // CAMERA
        camera = TronCamera()

        // debug
        camera.rotateLocal(-0.65f, 0.0f, 0f)
        camera.translateLocal(Vector3f(0f, 0f, 4f))

        // camera.rotateAroundPoint(1f,1f,1f, bike.getWorldPosition())

        SoundContext.setup()
        level = Level("caramelldansen")
        level.setup()

        setupGBuffer()
        GLError.checkThrow("HULULU????")

    }


    private val ambientLight = Vector3f(0.5f, 0.0f, 0.5f)
    fun ambient() {
        staticShader.setUniform("ambLight", ambientLight)
        staticShader.setUniform("ambientStrength", 0.05f)
    }

    fun render(dt: Float, t: Float) {
        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        gBufferShader.use()
        GLError.checkThrow("HULULU")
        camera.bind(gBufferShader)
        GLError.checkThrow("HULULU")


        // LIGHTING
        //staticShader.setUniform("lightMode",lightMode.ordinal)
        //ambient()
        //sceneLights.forEach { it.bind(staticShader) }

        // OBJECTS
        //level.update(dt, t)
        player.update(gBufferShader, camera)
        GLError.checkThrow("HULULU")
        ground.update(gBufferShader)
        GLError.checkThrow("HULULU")
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        SoundListener.setPosition(camera)
        GLError.checkThrow()
    }

    private fun setupGBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer)
        GLError.checkThrow()
        //position color buffer
        val gPosition = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, gPosition)
        val gPosFrameBuffer: ByteBuffer? = null
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA16F,
            window.windowWidth,
            window.windowHeight,
            0,
            GL_RGBA,
            GL_FLOAT,
            gPosFrameBuffer
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gPosition, 0)
        GLError.checkThrow()

        //normal color buffer
        val gNormal = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, gNormal)
        val gNormalFrameBuffer: ByteBuffer? = null
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA16F,
            window.windowWidth,
            window.windowHeight,
            0,
            GL_RGBA,
            GL_FLOAT,
            gNormalFrameBuffer
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, gNormal, 0)
        GLError.checkThrow()

        val gAlbedoSpec = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gAlbedoSpec);
        val gAlbedoBuffer: ByteBuffer? = null
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            window.windowWidth,
            window.windowHeight,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            gAlbedoBuffer
        );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, gAlbedoSpec, 0);
        GLError.checkThrow()
//
        val gEmissive = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gEmissive);
        val gEmissiveBuff: ByteBuffer? = null
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            window.windowWidth,
            window.windowHeight,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            gEmissiveBuff
        );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, gEmissive, 0);
        GLError.checkThrow()
//
        val buffers = intArrayOf(GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3)
        GL20.glDrawBuffers(buffers)
        GLError.checkThrow()

        val renderBufferObject = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, renderBufferObject)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, window.windowWidth, window.windowHeight)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBufferObject)
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw Exception("YIIIIIKES")
        //Bind default to enable GL_clear again
        GLError.checkThrow()

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        GLError.checkThrow()

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
            level.onKey(NoteKey.Left)
        }
        if (key == GLFW.GLFW_KEY_E && action == 1) {
            level.onKey(NoteKey.Right)
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
        level.cleanup()
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

package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.LightMode
import cga.exercise.components.light.PointLight
import cga.exercise.components.sound.SoundContext
import cga.exercise.components.sound.SoundListener
import cga.exercise.components.texture.Texture2D
import cga.exercise.game.environment.ground.Ground
import cga.exercise.game.level.Level
import cga.exercise.game.note.NoteKey
import cga.exercise.game.player.Player
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import java.util.*
import kotlin.math.PI
import kotlin.math.sin


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val NR_POINT_LIGHTS: Int = 8
    private val szeneLights = szeneLights()
    private val camera: TronCamera
    private val movementSpeed: Float = 6f
    private var boost = 1f
    private val rotationSpeed: Float = 3f
    private var lightMode = LightMode.BlinnPhong
    private var player = Player()
    private val ground = Ground()
    private val level: Level

    //scene setup
    init {

        //initial opengl state
        glClearColor(0f, 0f, 0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow() //todo change to glEnable again after tests are complete
        glDepthFunc(GL_LESS); GLError.checkThrow()

        // CAMERA
        camera = TronCamera()

        // debug
        camera.rotateLocal(-0.65f, 0.0f, 0f)
        camera.translateLocal(Vector3f(0f, 0f, 4f))

        // camera.rotateAroundPoint(1f,1f,1f, bike.getWorldPosition())

        SoundContext.setup()
        level = Level("map")
        level.setup()
    }


    private val ambientLight = Vector3f(0.5f,0.0f,0.5f)
    fun ambient(){
        staticShader.setUniform("ambLight", ambientLight)
        staticShader.setUniform("ambientStrength", 0.05f)
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        camera.bind(staticShader)

        // LIGHTNING

        staticShader.setUniform("lightMode",lightMode.ordinal)
        ambient()
        szeneLights.forEach { it.bind(staticShader) }

        // OBJECTS
        level.update(dt, t)
        player.update(staticShader, camera)
        ground.update(staticShader)
        SoundListener.setPosition(camera)
        GLError.checkThrow()
    }

    fun rainbow(vect: Vector3f, p: Float){
        val r = (sin(p * 2 * PI + 0/3.0 * PI) /2 + 0.5).toFloat()
        val g = (sin(p * 2 * PI + 2/3.0 * PI) /2 + 0.5).toFloat()
        val b = (sin(p * 2 * PI + 4/3.0 * PI) /2 + 0.5).toFloat()
        vect.x = r
        vect.y = g
        vect.z = b
    }

    fun update(dt: Float, t: Float) {
        if (window.getKeyState(GLFW.GLFW_KEY_L))
            lightMode =  LightMode.BlinnPhong
        else
            lightMode =  LightMode.Phong
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if(key == GLFW.GLFW_KEY_F && action == 1){ level.onKey(NoteKey.Left) }
        if(key == GLFW.GLFW_KEY_J && action == 1){ level.onKey(NoteKey.Right) }
    }

    var x: Double = 0.0
    fun onMouseMove(xPos: Double, yPos: Double) {
        if (x == 0.0){
            x = xPos
        }
        else{
            val diff = (x - xPos)*0.002
            x = xPos
            // Bike parent von Camera
            // bike <- rotateAround <- local <- v
            camera.rotateAroundPoint(0f, diff.toFloat(),0f,Vector3f(0f))
        }
    }

    fun cleanup() {
        level.cleanup()
        SoundContext.cleanup()
    }

    fun loadGround(){

    }


    fun szeneLights(): List<PointLight> {
        val l = arrayListOf<PointLight>()
        val r = Random(10)
        val rf = { r.nextFloat() * 20f -10f }
        val ra = { r.nextFloat() * 0.5f }
        for(i in 1 until NR_POINT_LIGHTS){
            l.add(PointLight(
                i, Vector3f(rf(),ra(),rf()),
                Vector3f(r.nextFloat(),r.nextFloat(),r.nextFloat()),
                Vector3f(1f, 0.15f, 0.12f),
            ))
        }
        return l.toList()
    }
}

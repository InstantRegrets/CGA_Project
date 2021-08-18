package cga.exercise.game.gameObjects.player

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Model
import cga.exercise.game.gameObjects.Phase
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import cga.framework.Random
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.PI
import kotlin.math.sin

class Player : Transformable(), GameObject {
    var color = Random.nextColor()
    var pose = Pose.Neutral
    var renderable: Renderable = renderable(color, Pose.Neutral)
    val light = PointLight(color, Vector3f(1f, 0.7f, 1.8f))
    var movementSpeed: Float = 8f
    var rotationSpeed: Float = 6f
    var army: ArrayList<Player> = arrayListOf()


    enum class Pose { Right, Left, Neutral}

    override fun switchPhase(phase: Phase) {
        when(phase){
            Phase.Day -> {
                renderable.pulseStrength = 0.1f
                army.clear()
            }
            Phase.Night -> {
                renderable.pulseStrength = 0.05f
            }
            Phase.Chaos -> {
                renderable.pulseStrength = 0.5f
                for(i in -10..10) {
                    for (j in -10..10) {
                        val p = Player()
                        p.translateLocal(Vector3f(2f*i.toFloat(), 0f, 2f*j.toFloat()))
                        p.scaleLocal(Vector3f(0.2f))
                        p.renderable.pulseStrength = 0.01f
                        army.add(p)
                    }
                }
            }
        }
    }

    init {
        renderable.parent = this
        light.parent = this

        translateLocal(Vector3f(0f,-4.4f,0f))
        scaleLocal(Vector3f(0.5f))
    }

    override fun draw(shaderProgram: ShaderProgram) {
        renderable.render(shaderProgram)
        army.forEach { it.draw(shaderProgram) }
    }

    override fun update(dt: Float, beat: Float) {
        rainbow(color,beat)
    }

    override fun processInput(window: GameWindow, dt: Float) {
        if (window.getKeyState(GLFW_KEY_W)) {
            renderable.translateLocal(Vector3f(0f, 0f, -movementSpeed * dt))
            if (window.getKeyState(GLFW_KEY_A)) renderable.rotateLocal(0f, rotationSpeed * dt, 0f)
            if (window.getKeyState(GLFW_KEY_D)) renderable.rotateLocal(0f, -rotationSpeed * dt, 0f)
        }
        if (window.getKeyState(GLFW_KEY_S)) {
            renderable.translateLocal(Vector3f(0f, 0f, movementSpeed * dt))
            if (window.getKeyState(GLFW_KEY_A)) renderable.rotateLocal(0f, rotationSpeed * dt, 0f)
            if (window.getKeyState(GLFW_KEY_D)) renderable.rotateLocal(0f, -rotationSpeed * dt, 0f)
        }
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
         light.bind(shaderProgram, viewMatrix4f)
    }

    fun rainbow(vect: Vector3f, p: Float) {
        val r = (sin(p * 2 * PI + 0 / 3.0 * PI) / 2 + 0.5).toFloat()
        val g = (sin(p * 2 * PI + 2 / 3.0 * PI) / 2 + 0.5).toFloat()
        val b = (sin(p * 2 * PI + 4 / 3.0 * PI) / 2 + 0.5).toFloat()
        vect.x = r
        vect.y = g
        vect.z = b
    }

    companion object{
        private val meshLeft = ModelLoader.loadModel("assets/models/duck/Duckpose1.obj",0f, PI.toFloat(),0f)!!.meshes
        private val meshRight = ModelLoader.loadModel("assets/models/duck/Duckpose1.obj",0f,PI.toFloat(),0f)!!.meshes // todo change
        private val meshneutral = ModelLoader.loadModel("assets/models/duck/Duckpose1.obj",0f,PI.toFloat(),0f)!!.meshes // todo chagne

        // Custom getter so we create a new Renderable everytime it is called
        fun renderable(color: Vector3f, pose: Pose): Renderable {
            val r = when(pose) {
                Pose.Left-> Renderable(meshLeft)
                Pose.Right-> Renderable(meshRight)
                Pose.Neutral-> Renderable(meshneutral)
            }


            r.emitColor = color
            return r
        }
    }
}


package cga.exercise.game.gameObjects.player

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.Random
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.PI
import kotlin.math.sin

class Player : Transformable(), GameObject {
    var color = Random.nextColor()
    var pose = Pose.Neutral
    var renderable: Renderable = getPose(color,pose)
    val light = PointLight(color, Vector3f(1f, 0.7f, 1.8f))
    var movementSpeed: Float = 8f
    var rotationSpeed: Float = 6f
    var army: ArrayList<Renderable> = arrayListOf()


    enum class Pose { Right, Left, Neutral}

    override fun switchPhase(phase: Phase) {
        when(phase){
            Phase.Day -> {
                renderable.pulseStrength = 0.05f
                army.clear()
            }
            Phase.Night -> {
                renderable.pulseStrength = 0.05f
            }
            Phase.Chaos -> {
                renderable.pulseStrength = 0.3f
                for(i in -5..5) {
                    for (j in -10..0) {
                        val p = createRenderable(Random.nextColor())
                        p.parent = this
                        p.translateLocal(Vector3f(2f*i.toFloat(), 0f, 2f*j.toFloat()))
                        p.scaleLocal(Vector3f(0.2f))
                        p.pulseStrength = 0.01f
                        army.add(p)
                    }
                }
            }
        }
    }

    init {
        renderable.parent = this
        light.parent = this
    }

    override fun draw(shaderProgram: ShaderProgram) {
        renderable.render(shaderProgram)
        army.forEach { it.render(shaderProgram) }
    }

    override fun update(dt: Float, beat: Float) {
        renderable = getPose(color, pose)
    }

    override fun processInput(window: GameWindow, dt: Float) {
        if (window.getKeyState(GLFW_KEY_A)) pose = Pose.Left
        else if (window.getKeyState(GLFW_KEY_D)) pose = Pose.Right
        else pose = Pose.Neutral
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
         light.bind(shaderProgram, viewMatrix4f)
    }


    companion object{
        private val meshLeft = ModelLoader.loadModel("assets/models/duck/duckPoseLeft.obj",0f, PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load left Player model")
        private val meshRight = ModelLoader.loadModel("assets/models/duck/duckPoseRight.obj",0f,PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load right Player model")
        private val meshNeutral = ModelLoader.loadModel("assets/models/duck/duckPoseNeutral.obj",0f,PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load neutral Player model")
        private val renderableLeft = Renderable(meshLeft)
        private val renderableRight = Renderable(meshRight)
        private val renderableNeutral = Renderable(meshNeutral)
        val renderables = listOf(renderableLeft, renderableRight, renderableNeutral)
        init {
            renderables.forEach {
                it.translateLocal(Vector3f(5f,0f,45f))
                it.scaleLocal(Vector3f(0.5f))
            }
        }
        // Custom getter so we create a new Renderable everytime it is called
        fun getPose(color: Vector3f, pose: Pose): Renderable {
            val r = when(pose) {
                Pose.Left-> renderableLeft
                Pose.Right-> renderableRight
                Pose.Neutral-> renderableNeutral
            }

            r.emitColor = color
            return r
        }
        fun createRenderable(color: Vector3f): Renderable{
            val r = Renderable(meshNeutral)
            r.emitColor = color
            return r
        }
    }
}


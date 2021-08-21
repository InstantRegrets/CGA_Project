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

class Player : Transformable(), GameObject {
    var color = Random.nextColor()
    var pose = Pose.Neutral
    var renderable: Renderable = getPose(color,pose)
    var army: ArrayList<Renderable> = arrayListOf()
    enum class Pose { Right, Left, Wide, Neutral}

    init {
        playerModels.forEach {
            it.parent = this
        }
        bongoLeft.parent = renderableNeutral
        bongoRight.parent = renderableNeutral
        translateLocal(Vector3f(9.8f,-4.3f,38f))
        scaleLocal(Vector3f(0.75f))
    }

    override fun switchPhase(phase: Phase) {
        when(phase){
            Phase.Day -> {
                playerModels.forEach{it.pulseStrength = 0.05f}
                army.clear()
            }
            Phase.Night -> {
                playerModels.forEach{it.pulseStrength = 0.0f}
            }
            Phase.Chaos -> {
                playerModels.forEach{it.pulseStrength = 0.3f}
                for(i in -5..5) {
                    for (j in -10..0) {
                        val p = createRenderable(Random.nextColor())
                        p.translateGlobal(this.getWorldPosition())
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
    }

    override fun draw(shaderProgram: ShaderProgram) {
        renderable.render(shaderProgram)
        bongoLeft.render(shaderProgram)
        bongoRight.render(shaderProgram)
        army.forEach { it.render(shaderProgram) }
    }

    override fun update(dt: Float, beat: Float) {
        renderable = getPose(color, pose)
    }

    override fun processInput(window: GameWindow, dt: Float) {
        if (window.getKeyState(GLFW_KEY_D) && window.getKeyState(GLFW_KEY_A)) {
            pose = Pose.Wide
            bongoRight.emitColor.set(0f,0f,1f)
            bongoLeft.emitColor.set(1f,0f,0f)
        }
        else if (window.getKeyState(GLFW_KEY_D)) {
            pose = Pose.Right
            bongoRight.emitColor.set(0f,0f,1f)
        }
        else if(window.getKeyState(GLFW_KEY_A)) {
            pose = Pose.Left
            bongoLeft.emitColor.set(1f,0f,0f)
        }
        else {
            pose = Pose.Neutral
            bongoRight.emitColor.set(0.25f)
            bongoLeft.emitColor.set(0.25f)
        }
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) { }


    companion object{
        private val meshLeft = ModelLoader.loadModel("assets/models/duck/duckPoseLeft.obj",0f, PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load left Player model")
        private val meshRight = ModelLoader.loadModel("assets/models/duck/duckPoseRight.obj",0f,PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load right Player model")
        private val meshWide = ModelLoader.loadModel("assets/models/duck/duckPoseWide.obj",0f,PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load right Player model")
        private val meshNeutral = ModelLoader.loadModel("assets/models/duck/duckPoseNeutral.obj",0f,PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load neutral Player model")
        val bongoLeft = ModelLoader.loadModel("assets/models/duck/bongo1.obj",0f,0f,0f)
            ?: throw Exception("Could not load left Bongo model")
        val bongoRight = ModelLoader.loadModel("assets/models/duck/bongo2.obj",0f,0f,0f)
            ?: throw Exception("Could not load left Bongo model")

        private val renderableLeft = Renderable(meshLeft)
        private val renderableRight = Renderable(meshRight)
        private val renderableWide = Renderable(meshWide)
        private val renderableNeutral = Renderable(meshNeutral)
        val playerModels = listOf(renderableLeft, renderableRight, renderableWide, renderableNeutral)
        init {

        }
        // Custom getter so we don't create a new Renderable everytime it is called
        fun getPose(color: Vector3f, pose: Pose): Renderable {
            val r = when(pose) {
                Pose.Left-> renderableLeft
                Pose.Right-> renderableRight
                Pose.Wide-> renderableWide
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


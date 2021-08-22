package cga.exercise.game.gameObjects.player

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.DepthCubeShader
import cga.exercise.components.shader.DepthShader
import cga.exercise.components.shader.GeometryShader
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.framework.Colors
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.Random
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.PI

class Player : Transformable(), GameObject {
    var color = Random.nextColor()
    var pose = Pose.Neutral
    var renderable: Renderable = getPose(color,pose)
    var army: ArrayList<Transformable> = arrayListOf()
    enum class Pose { Right, Left, Wide, Neutral}

    init {
        playerModels.forEach {
            it.parent = this
        }
        bongoLeft.parent = renderableNeutral
        bongoRight.parent = renderableNeutral
        translateLocal(Vector3f(10f,-4.3f,38f))
        scaleLocal(Vector3f(0.75f))
    }

    override fun switchPhase(phase: Phase) {
        when(phase){
            Phase.Day -> {
                playerModels.forEach{it.pulseStrength = 0.07f}
                bongoLeft.pulseStrength = 0.07f
                bongoRight.pulseStrength = 0.07f
                army.clear()
            }
            Phase.Night -> {
                playerModels.forEach{it.pulseStrength = 0.0f}
                bongoLeft.pulseStrength = 0.00f
                bongoRight.pulseStrength = 0.00f
                army.clear()
            }
            Phase.Chaos -> {
                playerModels.forEach{it.pulseStrength = 0.2f}
                bongoLeft.pulseStrength = 0.15f
                bongoRight.pulseStrength = 0.15f
                createArmy()
            }
        }
    }
    fun createArmy(){
        for(i in (-4..4)) {
            for (j in (-4..4)) {
                if (i == 0 && j == 0){}
                else {
                    val p = Transformable()
                    p.translateLocal(Vector3f(5.5f * i.toFloat(), 0f, 5.5f * j.toFloat()))
                    p.translateLocal(Vector3f(0f, -2.2f, 0f))
                    army.add(p)
                }
            }
        }
        val offsets = arrayListOf<Vector3f>()
        army.forEach {
            offsets.add(it.getPosition())
        }
        meshLeft2.forEach { it.setupInstancing(offsets) }
        meshRight2.forEach { it.setupInstancing(offsets) }
    }
    var currentMeshes = meshLeft2
    fun renderInstancedArmy(shaderProgram: ShaderProgram) {
        if (shaderProgram is GeometryShader
            //|| shaderProgram is DepthCubeShader
            || shaderProgram is DepthShader
        ) {
            val mm = getWorldModelMatrix().scale(0.5f)
            val pulseStrength = 0.00f
            shaderProgram.setUniform("model_matrix", mm)
            shaderProgram.setUniform("pulseStrength", pulseStrength)
            if (shaderProgram is GeometryShader) {
                shaderProgram.setUniform("vibeStrength", 0.5f)
                shaderProgram.setUniform("emitColor", Vector3f(0f))
            }
            currentMeshes.forEach { it.renderInstanced(shaderProgram, army.size) }
        }
    }

    init {
        renderable.parent = this
    }

    override fun draw(shaderProgram: ShaderProgram) {
        renderable.render(shaderProgram)
        bongoLeft.render(shaderProgram)
        bongoRight.render(shaderProgram)
        renderInstancedArmy(shaderProgram)
    }

    override fun update(dt: Float, beat: Float) {
        renderable = getPose(color, pose)

        if( beat.toInt()%2 == 0){
            currentMeshes = meshLeft2
        }
        else{
            currentMeshes = meshRight2
        }
    }

    override fun processInput(window: GameWindow, dt: Float) {
        if (window.getKeyState(GLFW_KEY_D) && window.getKeyState(GLFW_KEY_A) || window.getKeyState(GLFW_KEY_S)) {
            pose = Pose.Wide
            bongoRight.emitColor.set(Colors.blue)
            bongoLeft.emitColor.set(Colors.red)
        }
        else if (window.getKeyState(GLFW_KEY_D)) {
            pose = Pose.Right
            bongoRight.emitColor.set(Colors.blue)
            bongoLeft.emitColor.set(0.25f)
        }
        else if(window.getKeyState(GLFW_KEY_A)) {
            pose = Pose.Left
            bongoLeft.emitColor.set(Colors.red)
            bongoRight.emitColor.set(0.25f)
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
        private val meshRight2 = ModelLoader.loadModel("assets/models/duck/duckPoseRight.obj",0f,PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load right Player model")
        private val meshLeft2 = ModelLoader.loadModel("assets/models/duck/duckPoseLeft.obj",0f, PI.toFloat(),0f)?.meshes
            ?: throw Exception("Could not load left Player model")
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


package cga.exercise.game.gameObjects.note

import cga.exercise.components.geometry.*
import cga.exercise.components.light.Light
import cga.exercise.components.light.PointLight
import cga.exercise.components.material.AnimatedMaterial
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.random.Random

class Note(
    val data: NoteData,
): Transformable(), GameObject {
    private val color: Vector3f
    private val renderable: Renderable = noteRenderable()
    private val light: Light
    private val startPosition: Vector3f

    // todo move out of this class?
    private val targetPosition: Vector3f
    private val spawnBeat = 2f

    init {
        if (data.key == NoteKey.Left) {
            color = Vector3f(1f ,0f,0f)
            startPosition = Vector3f(Random.nextFloat()*-1f- 2f,1.5f, -2f )
            targetPosition = Vector3f(-1f,2f,0f)
        } else {
            color = Vector3f(0f ,0f,1f)
            startPosition = Vector3f(Random.nextFloat()*1f + 2f,1.5f, -2f)
            targetPosition = Vector3f(1f,2f,0f)
        }
        translateLocal(startPosition)
        scaleLocal(Vector3f(0.2f))
        renderable.emitColor = color
        light = PointLight(color, Vector3f(1f,0.15f,0.15f))

        renderable.parent = this
        light.parent = this
    }

    override fun draw(shaderProgram: ShaderProgram) {
        renderable.render(shaderProgram)
    }

    // todo this could be done so much more efficient
    override fun update(dt: Float, beat: Float) {
        renderable.meshes.forEach { (it.material as AnimatedMaterial).counter += dt*30 }
        // val f = (data.beat - beat) / spawnBeat
        // val newPos = Vector3f(targetPosition).sub(Vector3f(startPosition.mul(f)))
        // setPosition(newPos)
        scaleLocal(Vector3f(1.02f))
        // translateLocal(Vector3f(0f,0f,1.2f*dt))
    }

    override fun processInput(window: GameWindow, dt: Float) {

    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        light.bind(shaderProgram, viewMatrix4f)
    }

    override fun switchPhase(phase: Phase) {
        // TODO("Not yet implemented")
    }

    companion object {
        private val obj = OBJLoader.loadOBJ("assets/models/note/mesh.obj")
        private val meshes =  obj.objects.flatMap { it.meshes }
        private val diff = ModelLoader.loadTexArray("note", "diff")
        private val emit = ModelLoader.loadTexArray("note", "emit")
        private val spec = ModelLoader.loadTexArray("note", "spec")
        fun noteRenderable(): Renderable {
            val mat = AnimatedMaterial(diff,emit,spec, Vector2f(3.0f))
            val m =  meshes.map { Mesh(it.vertexData, it.indexData, ModelLoader.defaultOBJAttributes, mat) }.toMutableList()
            val r = Renderable(m)
            return r
        }
    }
}

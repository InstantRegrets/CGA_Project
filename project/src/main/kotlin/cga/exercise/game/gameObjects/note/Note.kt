package cga.exercise.game.gameObjects.note

import cga.exercise.components.geometry.*
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

class Note(
    val data: NoteData,
    currentBeat: Float,
): Transformable(), GameObject {
    private val color: Vector3f
    private val renderable: Renderable = note.renderable
    private val startPosition: Vector3f
    private val direction: Vector3f

    private val beatOffset = data.beat - currentBeat

    init {
        if (data.key == NoteKey.Left) {
            color = Vector3f(1f ,0f,0f)
            startPosition = Vector3f(-10f,-2f,-10f )
            direction = Vector3f(9f,0f,10f)
        } else {
            color = Vector3f(0f ,0f,1f)
            startPosition = Vector3f(10f,-2f,-10f)
            direction = Vector3f(-9f,0f,10f)
        }
        translateLocal(startPosition)
        renderable.emitColor = color
        renderable.parent = this
    }

    override fun draw(shaderProgram: ShaderProgram) {
        renderable.render(shaderProgram)
    }

    // todo this could be done so much more efficient
    private val movement = Vector3f()
    override fun update(dt: Float, beat: Float) {
        //renderable.meshes.forEach { (it.material as AnimatedMaterial).counter += dt*30 }
        direction.mul(dt/beatOffset,movement)
        translateLocal(movement)
    }

    override fun processInput(window: GameWindow, dt: Float) {

    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) { }

    override fun switchPhase(phase: Phase) {
        // TODO("Not yet implemented")
    }

    companion object {
        val note = SimonNote("assets/models/note/SimonNote/note.obj")
        private val obj = OBJLoader.loadOBJ("assets/models/note/mesh.obj")
        private val meshes =  obj.objects.flatMap { it.meshes }
        private val diff = ModelLoader.loadTexArray("note", "diff")
        private val emit = ModelLoader.loadTexArray("note", "emit")
        private val spec = ModelLoader.loadTexArray("note", "spec")

        init {
            note.renderable.scaleLocal(Vector3f(0.75f))
            note.renderable.translateLocal(Vector3f(0f, -4f, 0f))
        }

        fun noteRenderable(): Renderable {
            val mat = AnimatedMaterial(diff,emit,spec, Vector2f(3.0f))
            val m =  meshes.map { Mesh(it.vertexData, it.indexData, ModelLoader.defaultOBJAttributes, mat) }.toMutableList()
            val r = Renderable(m)
            return r
        }
    }
}

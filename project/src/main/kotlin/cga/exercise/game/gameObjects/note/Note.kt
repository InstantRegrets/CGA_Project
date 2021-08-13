package cga.exercise.game.gameObjects.note

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.Light
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.gameObjects.CustomModel
import cga.exercise.game.gameObjects.GameObject
import cga.framework.GameWindow
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

class Note(
    val data: NoteData,
): Transformable(), GameObject {
    private val color: Vector3f
    private val renderable: Renderable = CustomModel("orb").renderable
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
}

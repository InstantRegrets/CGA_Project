package cga.exercise.game.note

import cga.exercise.game.CustomModel
import org.joml.Vector3f
import kotlin.random.Random

class NoteModel(key: NoteKey): CustomModel(
    "assets/models/sphere.obj",
    "assets/textures/ground_emit.png",
    "assets/textures/ground_diff.png",
    "assets/textures/ground_spec.png",
    emitColor = Vector3f(0f)
){
    init {
        renderable.scaleLocal(Vector3f(Random.nextFloat()* 0.5f+ 0.5f))

        if (key == NoteKey.Left) {
            emitColor.set(1f ,0f,0f)
            renderable.translateLocal( Vector3f(Random.nextFloat()*-1f- 2f,1.5f, Random.nextFloat()*2f-1f) )
        } else {
            emitColor.set(0f,0f,1f)
            renderable.translateLocal( Vector3f(Random.nextFloat()*1f + 2f,1.5f, Random.nextFloat()*2f-1f) )
        }
    }
}
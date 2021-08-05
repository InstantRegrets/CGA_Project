package cga.exercise.game.note

import cga.exercise.game.gameObjects.Model
import org.joml.Vector3f
import kotlin.random.Random

class NoteModel(key: NoteKey, objPath: String = "assets/models/sphere.obj"): Model(objPath){
    init {
        renderable.scaleLocal(Vector3f(Random.nextFloat()* 0.5f+ 0.5f))

        if (key == NoteKey.Left) {
            renderable.emitColor.set(1f ,0f,0f)
            renderable.translateLocal( Vector3f(Random.nextFloat()*-1f- 2f,1.5f, Random.nextFloat()*2f-1f) )
        } else {
            renderable.emitColor.set(0f,0f,1f)
            renderable.translateLocal( Vector3f(Random.nextFloat()*1f + 2f,1.5f, Random.nextFloat()*2f-1f) )
        }
    }
}

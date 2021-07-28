package cga.exercise.game.player

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.game.Model
import cga.framework.ModelLoader
import org.joml.Vector3f
import kotlin.math.PI

class PlayerModel(parent: Transformable): Model {
    override val renderable: Renderable = ModelLoader.loadModel(
        "assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",
        -0.5f * PI.toFloat(), 0.5f * PI.toFloat(), 0f
    ) ?: throw NullPointerException()

    init {
        renderable.parent = parent
    }

    override val emitColor = Vector3f(0.25f)
}
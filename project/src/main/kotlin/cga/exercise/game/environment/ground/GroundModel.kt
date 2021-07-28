package cga.exercise.game.environment.ground

import cga.exercise.game.CustomModel
import org.joml.Vector3f
import kotlin.random.Random

class GroundModel: CustomModel(
    "assets/models/ground.obj",
    "assets/textures/ground_emit.png",
    "assets/textures/ground_diff.png",
    "assets/textures/ground_spec.png",
    emitColor = Vector3f(0.1f, 0.1f, 0f)
)

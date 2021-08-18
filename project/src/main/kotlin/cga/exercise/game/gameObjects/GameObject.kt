package cga.exercise.game.gameObjects

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.level.Event
import cga.framework.GameWindow
import org.joml.Matrix4f
import org.joml.Vector3f

interface GameObject {
    //draw call to actually render the object
    fun draw(shaderProgram: ShaderProgram)
    //Update every frame
    fun update(dt: Float, beat :Float)
    //handle input from game window if necessary
    fun processInput(window: GameWindow, dt: Float)
    //Call light binding methods
    fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) //TODO: Remove view matrix and upload in scene
    // switching between game phases
    fun switchPhase(phase:Phase)
}

enum class Phase() {
    Day,
    Night,
    Chaos,
}
val phaseAmount = Phase.values().size + 1



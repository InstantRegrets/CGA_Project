package cga.exercise.game.gameObjects

import cga.exercise.components.shader.ShaderProgram
import cga.framework.GameWindow
import org.joml.Matrix4f

interface GameObject {
    //draw call to actually render the object
    fun draw(shaderProgram: ShaderProgram)
    //Update every frame
    fun update(dt: Float,t :Float)
    //handle input from game window if necessary
    fun processInput(window: GameWindow, dt: Float)
    //Call light binding methods
    fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) //TODO: Remove view matrix and upload in scene
}

package cga.exercise.game.gameObjects

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.shader.ShaderProgram
import cga.framework.ModelLoader
import org.joml.Matrix4f


@Suppress("JoinDeclarationAndAssignment")
abstract class Model(
    objPath: String,
    pitch: Float = 0f,
    yaw: Float = 0f,
    roll: Float = 0f
) {
    val renderable: Renderable
     init {
        renderable = ModelLoader.loadModel(objPath,pitch, yaw, roll)?: throw Exception("could not load model $objPath")
    }
    fun draw(shaderProgram: ShaderProgram, addMatrix4f: Matrix4f? = null){
        renderable.render(shaderProgram, addMatrix4f)
    }
}

package cga.exercise.game.player

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram

class Player {
    val player = Transformable()
    val model = PlayerModel(player)
    val lighting = PlayerLighting(player)

    fun draw(shaderProgram: ShaderProgram){
        model.update(shaderProgram)
    }
    fun light(shaderProgram: ShaderProgram, camera: TronCamera){
        lighting.update(shaderProgram, camera.viewMatrix)

    }
}


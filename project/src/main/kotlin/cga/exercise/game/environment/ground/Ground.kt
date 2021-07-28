package cga.exercise.game.environment.ground

import cga.exercise.components.shader.ShaderProgram

class Ground() {

    private val model = GroundModel()

    fun update(shaderProgram: ShaderProgram){
        model.update(shaderProgram)
    }
}
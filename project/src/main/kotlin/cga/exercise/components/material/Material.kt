package cga.exercise.components.material

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var tcMultiplier : Vector2f = Vector2f(1.0f)
): Mat {

    override fun bind(shaderProgram: ShaderProgram) {
        emit.bind(0)
        shaderProgram.setUniform("emitMat", 0)
        diff.bind(1)
        shaderProgram.setUniform("diffMat", 1)
        specular.bind(2)
        shaderProgram.setUniform("specularMat", 2)
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
    }
}
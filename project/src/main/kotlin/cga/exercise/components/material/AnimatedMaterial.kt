package cga.exercise.components.material

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f

class AnimatedMaterial(var diff: Array<Texture2D>,
                       var emit: Array<Texture2D>,
                       var specular: Array<Texture2D>,
                       var tcMultiplier : Vector2f = Vector2f(4.0f)
): Mat {
    var counter = 0.0

    override fun bind(shaderProgram: ShaderProgram) {
        val c = counter.toInt()
        emit[c%emit.size].bind(0)
        shaderProgram.setUniform("emitMat", 0)

        diff[c%diff.size].bind(1)
        shaderProgram.setUniform("diffMat", 1)
        specular[c%specular.size].bind(2)
        shaderProgram.setUniform("specularMat", 2)

        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
    }
}
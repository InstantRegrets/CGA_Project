package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

abstract class Light: Transformable() {
    abstract fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f)

    fun Vector4f.toVector3f(): Vector3f = Vector3f(x,y,z)

    /**
     * We need a way to track all the indexes of the lights
     * this Companion object takes care of this and assigns new indexes,
     * whenever we register or cleanup a Light.
     *
     * to use any Lights
     */
    companion object LightManager{
        var plAmount: Int = 0
        var slAmount: Int = 0

        fun bindAmount(shaderProgram: ShaderProgram){
            shaderProgram.setUniform("plAmount", plAmount)
            shaderProgram.setUniform("slAmount", slAmount)
            plAmount = 0
            slAmount = 0
        }
    }
}
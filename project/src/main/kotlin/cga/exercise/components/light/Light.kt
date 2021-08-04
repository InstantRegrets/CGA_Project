package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

abstract class Light: Transformable() {
    abstract fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f)

    fun Vector4f.toVector3f(): Vector3f = Vector3f(x,y,z)
}
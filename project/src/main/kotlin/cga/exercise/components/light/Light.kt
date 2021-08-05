package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

abstract class Light: Transformable() {
    protected abstract fun setIndex(index: Int)
    abstract fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f)

    fun cleanup(){ remove(this) }

    fun Vector4f.toVector3f(): Vector3f = Vector3f(x,y,z)

    /**
     * We need a way to track all the indexes of the lights
     * this Companion object takes care of this and assigns new indexes,
     * whenever we register or cleanup a Light.
     *
     * to use any Lights
     */
    companion object LightManager{
        private val pointLights = mutableSetOf<PointLight>()
        private val spotLights = mutableSetOf<SpotLight>()

        fun register(l: Light){
            when (l){
                is PointLight -> { pointLights.add(l); resetPlIds() }
                is SpotLight -> { spotLights.add(l); resetSlIds() }
                else -> throw UnsupportedOperationException()
            }
        }

        fun remove(l: Light){
            when (l){
                is PointLight -> { pointLights.remove(l); resetPlIds() }
                is SpotLight -> { spotLights.remove(l); resetSlIds() }
                else -> throw UnsupportedOperationException()
            }
        }

        fun bindAmount(shaderProgram: ShaderProgram){
            shaderProgram.setUniform("plAmount", pointLights.size)
            shaderProgram.setUniform("slAmount", spotLights.size)
        }

        private fun resetPlIds(){
            for ((index, pl) in pointLights.withIndex()){
                pl.setIndex(index)
            }
        }

        private fun resetSlIds(){
            for ((index, sl) in spotLights.withIndex()){
                sl.setIndex(index)
            }
        }
    }

}
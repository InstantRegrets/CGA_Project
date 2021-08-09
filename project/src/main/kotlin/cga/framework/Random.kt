package cga.framework

import org.joml.Vector3f
import kotlin.math.PI

object Random  {
    private val r = kotlin.random.Random(0)

    fun nextColor(): Vector3f {
        return Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat())
    }

    fun nextInt(from: Int, until: Int): Int {
        return r.nextInt(from, until)
    }
    fun nextFloat(from: Float, until: Float): Float {
        val range = until - from;
        return r.nextFloat() * range + from
    }

    fun nextFloat(until: Float): Float {
        return r.nextFloat() * until
    }

    fun nextFloat(): Float {
        return r.nextFloat()
    }

    fun nextRadiant(): Float {
        return r.nextFloat() * 2f * PI.toFloat()
    }

    fun nextVec3(until: Float): Vector3f {
        return Vector3f(nextFloat(until), nextFloat(until), nextFloat(until))
    }

    fun nextVec3(from: Float, until: Float): Vector3f {
        return Vector3f(nextFloat(from, until), nextFloat(from, until), nextFloat(from, until))
    }

}
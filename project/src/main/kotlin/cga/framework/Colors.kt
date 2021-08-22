package cga.framework

import org.joml.Vector3f

object Colors {
    val red = Vector3f(0.807f, 0.109f, 0.529f)
    val redFlash = Vector3f(red).mul(1.1f)
    val blue = Vector3f(0.160f, 0.737f, 0.858f)
    val blueFlash = Vector3f(blue).mul(1.1f)
}

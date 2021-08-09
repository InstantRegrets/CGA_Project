package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f

open class Transformable(var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotateLocal(pitch: Float, yaw: Float, roll: Float) {
        modelMatrix.rotateXYZ(pitch, yaw, roll)
    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val rotMat = Matrix4f().rotateXYZ(pitch, yaw, roll)

        val transMatBack = Matrix4f()
            .translate(altMidpoint)
        val transMat = Matrix4f().translate(Vector3f(altMidpoint).negate())

        // T * R * T^-1 * v
        // todo names

        val test = Matrix4f()
        test.mul(transMat)
        test.mul(rotMat)
        test.mul(transMatBack)
        test.mul(modelMatrix, modelMatrix)

    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    fun translateLocal(deltaPos: Vector3f) {
        val result = Matrix4f().translate(deltaPos)
        modelMatrix.mul(result)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: global operations will be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun translateGlobal(deltaPos: Vector3f) = Matrix4f().translate(deltaPos).mul(modelMatrix, modelMatrix)

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scaleLocal(scale: Vector3f) {
        val scaleMat = Matrix4f().scale(scale)
        modelMatrix.mul(scaleMat)
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f {
        return Vector3f(modelMatrix.m30(), modelMatrix.m31(), modelMatrix.m32())
    }

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f {
            val mm = getWorldModelMatrix()
            return Vector3f(mm.m30(), mm.m31(), mm.m32())
    }

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f = Vector3f(modelMatrix.m00(), modelMatrix.m01(), modelMatrix.m02()).normalize()

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f = Vector3f(modelMatrix.m10(), modelMatrix.m11(), modelMatrix.m12()).normalize()

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f = Vector3f(modelMatrix.m20(), modelMatrix.m21(), modelMatrix.m22()).normalize()

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {
        val m = getWorldModelMatrix()
        val v = Vector3f(m.m00(), m.m01(), m.m02())
        return v.normalize()
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {
        val m = getWorldModelMatrix()
        val v = Vector3f(m.m10(), m.m11(), m.m12())
        return v.normalize()
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {
        val m = getWorldModelMatrix()
        val v = Vector3f(m.m20(), m.m21(), m.m22())
        return v.normalize()
    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    fun getWorldModelMatrix(): Matrix4f {
        if (parent == null) {
            return getLocalModelMatrix()
        } else {
            val parentMM = parent?.getWorldModelMatrix() ?: throw IllegalStateException("Excuse me wtf?")
            return Matrix4f(parentMM).mul(modelMatrix)
        }
    }

    /**
     * Returns object model matrix
     * @return modelMatrix
     */
        fun getLocalModelMatrix(): Matrix4f {
        return Matrix4f(modelMatrix)
    }
}

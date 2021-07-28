package cga.exercise.game

import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.texture.Texture2D
import cga.framework.OBJLoader
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11

open class CustomModel(
    objPath: String,
    emitPath: String,
    diffPath: String,
    specPath: String,
    override val emitColor: Vector3f,
): Model {
    override val renderable: Renderable
    init {
        val groundOBJ = OBJLoader.loadOBJ(objPath)
        val groundOBJMesh = groundOBJ.objects.first().meshes.first()

        val emit = Texture2D.invoke(emitPath, true)
        emit.setTexParams(
            GL11.GL_REPEAT, GL11.GL_REPEAT,
            GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR
        )
        val diff = Texture2D.invoke(diffPath, true)
        diff.setTexParams(GL11.GL_REPEAT, GL11.GL_REPEAT, GL11.GL_LINEAR, GL11.GL_LINEAR)
        val spec = Texture2D.invoke(specPath, true)
        spec.setTexParams(GL11.GL_REPEAT, GL11.GL_REPEAT, GL11.GL_LINEAR, GL11.GL_LINEAR)
        val material = Material(diff, emit, spec, tcMultiplier = Vector2f(64f))

        val groundMesh = Mesh(groundOBJMesh.vertexData, groundOBJMesh.indexData, defaultOBJAttributes, material)

        renderable = Renderable(mutableListOf(groundMesh))
    }
}
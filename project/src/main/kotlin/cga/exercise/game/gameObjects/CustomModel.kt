package cga.exercise.game.gameObjects

import cga.exercise.components.geometry.*
import cga.exercise.components.material.Material
import cga.exercise.components.texture.Texture2D
import cga.framework.OBJLoader
import org.joml.Vector2f
import org.lwjgl.opengl.GL33.*

/**
 * Class that loads this specific File Format:
 * name
 *  - mesh.obj
 *  - diff.png
 *  - emit.png
 *  - spec.png
 */
open class CustomModel(
    name: String
 ) {
    val objPath = "assets/models/$name/mesh.obj"
    val emitPath = "assets/models/$name/emit.png"
    val diffPath = "assets/models/$name/diff.png"
    val specPath = "assets/models/$name/spec.png"
    open val renderable: Renderable = loadRenderable(objPath, emitPath, diffPath, specPath)

    fun loadRenderable(objPath:String, emitPath: String, diffPath: String, specPath: String): Renderable {
        val groundOBJ = OBJLoader.loadOBJ(objPath)

        val groundOBJMesh = groundOBJ.objects.first().meshes.first()

        val emit = Texture2D.invoke(emitPath, true)
        emit.setTexParams(
            GL_REPEAT, GL_REPEAT,
            GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR,
        )
        val diff = Texture2D.invoke(diffPath, true)
        diff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val spec = Texture2D.invoke(specPath, true)
        spec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val material = Material(diff, emit, spec, tcMultiplier = Vector2f(64f))

        val defaultOBJAttributes = arrayOf(
            VertexAttribute(3, GL_FLOAT,  32, 0),  //Position
            VertexAttribute(2, GL_FLOAT,  32, 12),  //Tex
            VertexAttribute(3, GL_FLOAT,  32, 20),  //Normals
        )

        val groundMesh = Mesh(groundOBJMesh.vertexData, groundOBJMesh.indexData, defaultOBJAttributes, material)

        return Renderable(mutableListOf(groundMesh))
    }
}

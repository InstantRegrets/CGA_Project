package cga.exercise.components.texture

import cga.framework.GLError
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage

@Suppress("DuplicatedCode")
/**
 * This is used to create a skybox.
 * The images have to be in this format:
 * https://learnopengl.com/img/advanced/cubemaps_skybox.png
 * also named right.jpg, left.jpg, top.jpg, bottom.jpg, front.jpg, back.jpg
 */
class Skybox(private var id: Int = -1) {
    companion object {
        operator fun invoke(folderPath: String): Skybox {
            val faces = listOf(
                "right.jpg",
                "left.jpg",
                "top.jpg",
                "bottom.jpg",
                "front.jpg",
                "back.jpg"
            )
            val texID = glGenTextures()
            glBindTexture(GL_TEXTURE_CUBE_MAP, texID)
            for (i in faces.indices) {
                val path = "$folderPath/${faces[i]}"
                val widthRead = BufferUtils.createIntBuffer(1)
                val heightRead = BufferUtils.createIntBuffer(1)
                val readChannels = BufferUtils.createIntBuffer(1)
                //flip y coordinate to make OpenGL happy
                STBImage.stbi_set_flip_vertically_on_load(false)
                val imageData = STBImage.stbi_load(path, widthRead, heightRead, readChannels, 0)
                    ?: throw Exception("Image file \"" + path + "\" couldn't be read:\n" + STBImage.stbi_failure_reason())
                GL11.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                    0, GL_RGB,widthRead.get(),heightRead.get(),0, GL_RGB, GL_UNSIGNED_BYTE, imageData)
                GLError.checkThrow()
                STBImage.stbi_image_free(imageData)
            }
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
            GLError.checkThrow()
            if (texID!=-1)
                return Skybox(texID)
            else throw Exception("Something went wrong...")

        }

    }
    private val vertices = floatArrayOf(
        -1.0f,  1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,

        -1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,

        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,

        -1.0f, -1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,

        -1.0f,  1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f, -1.0f,

        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f,  1.0f
    )

    private val skyboxVAO = glGenVertexArrays()
    private val skyboxVBO = glGenBuffers()
    fun setup(){
        glBindVertexArray(skyboxVAO)
        glBindBuffer(GL_ARRAY_BUFFER, skyboxVBO)
        GL15.glBufferData(GL_ARRAY_BUFFER,vertices, GL_STATIC_DRAW)
        glEnableVertexAttribArray(0)
        GL20.glVertexAttribPointer(0,3, GL_FLOAT, false, 3*4,0)
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun bind(textureUnit: Int) {
        glActiveTexture(GL_TEXTURE0 + textureUnit)
        glBindTexture(GL_TEXTURE_CUBE_MAP, id)
    }

    fun render(){
        glBindVertexArray(skyboxVAO)
        bind(0)
        glDrawArrays(GL_TRIANGLES,0,36)
        glBindVertexArray(0)
    }

    fun unbind() {
        glBindTexture(0,0)
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun cleanup() {
        unbind()
        if (id != 0) {
            GL11.glDeleteTextures(id)
            id = 0
        }
    }
}

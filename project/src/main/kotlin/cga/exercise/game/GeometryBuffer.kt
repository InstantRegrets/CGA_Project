package cga.exercise.game

import cga.framework.GLError
import cga.framework.GameWindow
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL33.*
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImageWrite
import java.nio.ByteBuffer

@Suppress("JoinDeclarationAndAssignment")
class GeometryBuffer(window: GameWindow){
    val gBufferID: Int
    val textureIDs: IntArray = IntArray(totalTextures)
    val width: Int = window.windowWidth
    val height: Int = window.windowHeight
    companion object{
        const val totalTextures: Int = 5
    }
    init {
        gBufferID = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, gBufferID)
        GLError.checkThrow()
        glGenTextures(textureIDs)
        for (i in 0 until totalTextures){
            glBindTexture(GL_TEXTURE_2D, textureIDs[i])
            glTexImage2D(GL_TEXTURE_2D,0, GL_RGB32F,
                width,height,0,
                GL_RG, GL_FLOAT, null as ByteBuffer?)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i,
                GL_TEXTURE_2D, textureIDs[i],0)
        }

        val buffers = intArrayOf(
            GL_COLOR_ATTACHMENT0,
            GL_COLOR_ATTACHMENT1,
            GL_COLOR_ATTACHMENT2,
            GL_COLOR_ATTACHMENT3,
            GL_COLOR_ATTACHMENT4,
        )
        glDrawBuffers(buffers)
        GLError.checkThrow()

        val renderBufferObject = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, renderBufferObject)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, window.windowWidth, window.windowHeight)
        glFramebufferRenderbuffer(
            GL_FRAMEBUFFER,
            GL_DEPTH_ATTACHMENT,
            GL_RENDERBUFFER,
            renderBufferObject
        )
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw Exception("YIIIIIKES")
        //Bind default to enable GL_clear again
        GLError.checkThrow()

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        GLError.checkThrow()
    }

    fun getPositionTex(): Int = textureIDs[0]
    fun getNormalTex(): Int = textureIDs[1]
    fun getAlbedoSpecTex(): Int = textureIDs[2]
    fun getEmissiveTex(): Int = textureIDs[3]

    fun bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, gBufferID)
    }

    fun bindTextures(){
        for (i in 0 until totalTextures){
            glActiveTexture(GL_TEXTURE0+i)
            glBindTexture(GL_TEXTURE_2D, textureIDs[i])
        }
    }


}
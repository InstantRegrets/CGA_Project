package cga.exercise.components.geometry

import cga.framework.GLError
import cga.framework.GameWindow
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33.*
import java.nio.ByteBuffer

@Suppress("JoinDeclarationAndAssignment")
class GeometryBuffer(window: GameWindow){
    var gBufferID: Int = glGenFramebuffers()
    private val textureIDs: IntArray = IntArray(totalTextures)
    private val width: Int = window.windowWidth
    private val height: Int = window.windowHeight
    private val finalColorAttachment = GL_COLOR_ATTACHMENT6
    val depthTexture = glGenTextures()
    val finalTexture = glGenTextures()

    companion object{
        const val totalTextures: Int = 6
    }
    init {
        glBindFramebuffer(GL_FRAMEBUFFER, gBufferID)
        GLError.checkThrow()
        glGenTextures(textureIDs)
        for ((i, tex) in textureIDs.withIndex()){
            glBindTexture(GL_TEXTURE_2D,tex)
            glTexImage2D(GL_TEXTURE_2D,0, GL_RGB32F,
                width,height,0,
                GL_RG, GL_FLOAT, null as ByteBuffer?)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i,
                GL_TEXTURE_2D, tex,0)
        }


        GLError.checkThrow()
        glBindTexture(GL_TEXTURE_2D, depthTexture)
        glTexImage2D(GL_TEXTURE_2D,0, GL_DEPTH24_STENCIL8, width, height, 0,
            GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, null as ByteBuffer?)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0)


        glBindTexture(GL_TEXTURE_2D, finalTexture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
            GL_RGB, GL_FLOAT, null as ByteBuffer?)
        glFramebufferTexture2D(GL_FRAMEBUFFER, finalColorAttachment, GL_TEXTURE_2D,finalTexture,0)

        glDrawBuffer(GL_NONE)

        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            print("FB Error, status: $status")
            throw Exception("Framebuffer incomplete!")
        }
        GLError.checkThrow()

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun startFrame(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gBufferID)
        glDrawBuffer(finalColorAttachment)
        glClear(GL_COLOR_BUFFER_BIT)
    }

    fun bindForGeomPass(){
        //renders into seperate color attachments
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gBufferID)
        val buffers = intArrayOf(
            GL_COLOR_ATTACHMENT0, //position
            GL_COLOR_ATTACHMENT1, //normal
            GL_COLOR_ATTACHMENT2, //diffuse
            GL_COLOR_ATTACHMENT3, //specular
            GL_COLOR_ATTACHMENT4, //emissive
            GL_COLOR_ATTACHMENT5 //shadow
        )
        glDrawBuffers(buffers)
    }

    fun bindForStencilPass(){
        //we dont want to write a fully black image into our color buffer during this
        glDrawBuffer(GL_NONE)
    }

    fun bindForLightPass(){
        //affects final image
        glDrawBuffer(finalColorAttachment)
        for ((i, tex) in textureIDs.withIndex()){
            glActiveTexture(GL_TEXTURE0+i)
            glBindTexture(GL_TEXTURE_2D, tex)
        }
    }

    fun bindForFinalPass(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,0)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, gBufferID)
        glReadBuffer(finalColorAttachment)
    }

    fun bindForDepthReadout(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,0)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, gBufferID)
        GLError.checkThrow()
    }

    fun cleanup(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glDeleteFramebuffers(gBufferID)
        gBufferID = -1
    }

}

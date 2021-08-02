package cga.exercise.game

import cga.framework.GLError
import cga.framework.GameWindow
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D
import org.lwjgl.opengl.GL33.*
import java.nio.ByteBuffer

class GeometryBuffer(window: GameWindow){
    val gBuffer: Int
    val gPosition: Int
    val gNormal: Int
    val gAlbedoSpec: Int
    val gEmissive: Int

    init {
        gBuffer = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer)
        GLError.checkThrow()
        //position color buffer
        gPosition = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, gPosition)
        val gPosFrameBuffer: ByteBuffer? = null
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA16F,
            window.windowWidth,
            window.windowHeight,
            0,
            GL_RGBA,
            GL_FLOAT,
            gPosFrameBuffer
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gPosition, 0)
        GLError.checkThrow()

        //normal color buffer
        gNormal = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, gNormal)
        val gNormalFrameBuffer: ByteBuffer? = null
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA16F,
            window.windowWidth,
            window.windowHeight,
            0,
            GL_RGBA,
            GL_FLOAT,
            gNormalFrameBuffer
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, gNormal, 0)
        GLError.checkThrow()

        gAlbedoSpec = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gAlbedoSpec);
        val gAlbedoBuffer: ByteBuffer? = null
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            window.windowWidth,
            window.windowHeight,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            gAlbedoBuffer
        );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, gAlbedoSpec, 0);
        GLError.checkThrow()
//
        gEmissive = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gEmissive);
        val gEmissiveBuff: ByteBuffer? = null
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            window.windowWidth,
            window.windowHeight,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            gEmissiveBuff
        );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, gEmissive, 0);
        GLError.checkThrow()
//
        val buffers = intArrayOf(
            GL_COLOR_ATTACHMENT0,
            GL_COLOR_ATTACHMENT1,
            GL_COLOR_ATTACHMENT2,
            GL_COLOR_ATTACHMENT3
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

    fun bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer)
    }

    fun bindTextures(){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, gPosition);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, gNormal);
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, gAlbedoSpec);
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, gEmissive);
    }
}
package cga.exercise.components.geometry

import org.lwjgl.opengl.GL33.*
import java.nio.ByteBuffer

class DepthMap(val width: Int = 1024, val height: Int = 1024) {
    val fbo: Int = glGenFramebuffers()
    val texture = glGenTextures()
    init {

        // generate the depth texture
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
            width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null as ByteBuffer?)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        // attach it as the framebuffer's depth buffer:
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texture, 0)
        glDrawBuffer(GL_NONE) // tell opengl we are not going to draw any pixels
        glReadBuffer(GL_NONE) // tell opengl we are not going to draw any pixels
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

    }

    fun bind(){
    }

    fun bindTexture(){
    }
}
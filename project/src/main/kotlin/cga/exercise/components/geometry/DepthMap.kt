package cga.exercise.components.geometry

import cga.framework.Quality
import cga.framework.quality
import org.lwjgl.opengl.GL33.*
import java.nio.ByteBuffer

class DepthMap {
    val fbo: Int = glGenFramebuffers()
    val texture = glGenTextures()
    val width: Int
    val height: Int
    private val borderColor = floatArrayOf(1f,1f,1f,1f)
    init {

        when(quality){
            Quality.Low -> { width = 512; height = 512 }
            Quality.High -> { width = 1024; height = 1024 }
            Quality.Ultra -> { width = 16*1024; height = 16*1024 }
        }

        // generate the depth texture
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
            width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null as ByteBuffer?)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,  GL_CLAMP_TO_BORDER)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,  GL_CLAMP_TO_BORDER)
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor)



        // attach it as the framebuffer's depth buffer:
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texture, 0)
        glDrawBuffer(GL_NONE) // tell opengl we are not going to draw any pixels
        glReadBuffer(GL_NONE) // tell opengl we are not going to draw any pixels
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

    }
}
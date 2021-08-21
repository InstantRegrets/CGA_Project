package cga.exercise.components.texture

import cga.framework.Quality
import cga.framework.quality
import org.lwjgl.opengl.GL33.*
import java.nio.ByteBuffer

class DepthCubemap {
    val fbo: Int = glGenFramebuffers()
    val texture = glGenTextures()
    val width: Int
    val height: Int
    val aspect: Float
    private val borderColor = floatArrayOf(1f,1f,1f,1f)
    init {

        when(quality){
            Quality.Low -> { width = 256; height = 256 }
            Quality.High -> { width = 2*256; height = 2*256 }
            Quality.Ultra -> { width = 4*256; height = 4*256 }
        }
        aspect =width.toFloat() / height.toFloat()

        // generate the depth texture
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture)
        for(i in 0 until 6){
            glTexImage2D(
                GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT,
                width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null as ByteBuffer?
            )
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)


        // attach it as the framebuffer's depth buffer:
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, texture, 0)
        glDrawBuffer(GL_NONE) // tell opengl we are not going to draw any pixels
        glReadBuffer(GL_NONE) // tell opengl we are not going to draw any pixels
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
}
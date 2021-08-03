import cga.framework.GameWindow
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryStack
import java.awt.Window
import java.nio.ByteBuffer

class GBuffer(window: GameWindow) {
    val gBufferId: Int
    val textureIds: IntArray?
    val width: Int
    val height: Int
    val positionTexture: Int
        get() = textureIds!![0]
    val depthTexture: Int
        get() = textureIds!![TOTAL_TEXTURES - 1]

    fun cleanUp() {
        GL30.glDeleteFramebuffers(gBufferId)
        if (textureIds != null) {
            for (i in 0 until TOTAL_TEXTURES) {
                GL11.glDeleteTextures(textureIds[i])
            }
        }
    }

    companion object {
        private const val TOTAL_TEXTURES = 6
    }

    init {
        // Create G-Buffer
        gBufferId = GL30.glGenFramebuffers()
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, gBufferId)
        textureIds = IntArray(TOTAL_TEXTURES)
        GL11.glGenTextures(textureIds)
        width = window.windowWidth
        height = window.windowHeight

        // Create textures for position, diffuse color, specular color, normal, shadow factor and depth
        // All coordinates are in world coordinates system
        for (i in 0 until TOTAL_TEXTURES) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIds[i])
            var attachmentType: Int
            attachmentType = when (i) {
                TOTAL_TEXTURES - 1 -> {
                    // Depth component
                    GL11.glTexImage2D(
                        GL11.GL_TEXTURE_2D,
                        0,
                        GL30.GL_DEPTH_COMPONENT32F,
                        width,
                        height,
                        0,
                        GL11.GL_DEPTH_COMPONENT,
                        GL11.GL_FLOAT,
                        null as ByteBuffer?
                    )
                    GL30.GL_DEPTH_ATTACHMENT
                }
                else -> {
                    GL11.glTexImage2D(
                        GL11.GL_TEXTURE_2D,
                        0,
                        GL30.GL_RGB32F,
                        width,
                        height,
                        0,
                        GL11.GL_RGB,
                        GL11.GL_FLOAT,
                        null as ByteBuffer?
                    )
                    GL30.GL_COLOR_ATTACHMENT0 + i
                }
            }
            // For sampling
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST.toFloat())
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST.toFloat())

            // Attach the the texture to the G-Buffer
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachmentType, GL11.GL_TEXTURE_2D, textureIds[i], 0)
        }
        MemoryStack.stackPush().use { stack ->
            val intBuff = stack.mallocInt(TOTAL_TEXTURES)
            val values = intArrayOf(
                GL30.GL_COLOR_ATTACHMENT0,
                GL30.GL_COLOR_ATTACHMENT1,
                GL30.GL_COLOR_ATTACHMENT2,
                GL30.GL_COLOR_ATTACHMENT3,
                GL30.GL_COLOR_ATTACHMENT4,
                GL30.GL_COLOR_ATTACHMENT5
            )
            for (i in values.indices) {
                intBuff.put(values[i])
            }
            intBuff.flip()
            GL20.glDrawBuffers(intBuff)
        }

        // Unbind
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
    }
}
package cga.exercise.components.sound

import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL10.*
import org.lwjgl.stb.STBVorbis.*
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.File
import java.nio.IntBuffer
import java.nio.ShortBuffer
import java.nio.file.Path


class SoundBuffer(path: File) {
    internal val bufferId: Int = alGenBuffers()
    var length: Float = 0f
    init {
        val info = STBVorbisInfo.malloc()
        val pcm = readVorbis(path.absolutePath, info)
        val c = if (info.channels() == 1) AL_FORMAT_MONO16 else AL_FORMAT_STEREO16
        // if (info.channels() > 1)
        //     println("Warning, audio with multiple channels will be played back `as is` without any effects")
        alBufferData(bufferId, c, pcm, info.sample_rate())
    }

    private fun readVorbis(resource: String, info: STBVorbisInfo): ShortBuffer {
        try {
            val stack = MemoryStack.stackPush()
            val error: IntBuffer = stack.mallocInt(1);
            val decoder: Long = stb_vorbis_open_filename(resource, error, null)
            if (decoder == 0L){
                throw Exception("failed to create decoder, gl with this code: ${error.get(0)}")
            }

            stb_vorbis_get_info(decoder, info);

            val channels: Int = info.channels();

            val lengthSamples: Int = stb_vorbis_stream_length_in_samples(decoder) * channels;
            length = stb_vorbis_stream_length_in_seconds(decoder)

            val pcm = BufferUtils.createShortBuffer(lengthSamples)

            pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
            stb_vorbis_close(decoder);

            return pcm;
        } catch (e: Exception) {
            // we arrive here if the song path is invalid
            // todo better error handling
            throw e
        }
    }

    fun cleanup() {
        alDeleteBuffers(bufferId)
    }
}

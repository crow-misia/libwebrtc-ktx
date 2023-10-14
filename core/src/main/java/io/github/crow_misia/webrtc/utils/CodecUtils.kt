@file:JvmName("CodecUtils")

package io.github.crow_misia.webrtc.utils

import org.webrtc.HardwareVideoDecoderFactory
import org.webrtc.HardwareVideoEncoderFactory
import org.webrtc.VideoDecoderFactory
import org.webrtc.VideoEncoderFactory

/**
 * check H.264 Support.
 *
 * @param videoEncoderFactory Video Encoder Factory
 * @param videoDecoderFactory Video Decoder Factory
 * @return If true, H.264 supported.
 */
@JvmOverloads
fun isH264Supported(
    videoEncoderFactory: VideoEncoderFactory = HardwareVideoEncoderFactory(null, true, true),
    videoDecoderFactory: VideoDecoderFactory = HardwareVideoDecoderFactory(null),
): Boolean {
    val h264EncoderSupported = videoEncoderFactory.supportedCodecs.any {
        it.name.equals("h264", ignoreCase = true)
    }
    val h264DecoderSupported = videoDecoderFactory.supportedCodecs.any {
        it.name.equals("h264", ignoreCase = true)
    }

    return h264EncoderSupported && h264DecoderSupported
}
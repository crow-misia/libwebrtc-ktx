@file:JvmName("CodecUtils")
package io.github.crow_misia.webrtc.utils

import org.webrtc.HardwareVideoDecoderFactory
import org.webrtc.HardwareVideoEncoderFactory

/**
 * check H.264 Support.
 *
 * @return If true, H.264 supported.
 */
fun isH264Supported(): Boolean {
    val hardwareVideoEncoderFactory = HardwareVideoEncoderFactory(null, true, true)
    val hardwareVideoDecoderFactory = HardwareVideoDecoderFactory(null)

    val h264EncoderSupported = hardwareVideoEncoderFactory.supportedCodecs.any {
        it.name.equals("h264", ignoreCase = true)
    }
    val h264DecoderSupported = hardwareVideoDecoderFactory.supportedCodecs.any {
        it.name.equals("h264", ignoreCase = true)
    }

    return h264EncoderSupported && h264DecoderSupported
}
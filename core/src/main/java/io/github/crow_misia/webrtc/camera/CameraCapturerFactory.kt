@file:Suppress("unused")

package io.github.crow_misia.webrtc.camera

import android.content.Context
import io.github.crow_misia.webrtc.log.WebRtcLogger
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.CameraVideoCapturer

object CameraCapturerFactory {
    private val TAG = CameraCapturerFactory::class.simpleName

    @JvmStatic
    fun create(context: Context,
               fixedResolution: Boolean = false,
               preferenceFrontCamera: Boolean = true,
               eventsHandler: CameraVideoCapturer.CameraEventsHandler? = null
    ): CameraVideoCapturer? {
        var capturer: CameraVideoCapturer? = null
        if (Camera2Enumerator.isSupported(context)) {
            capturer = createCapturer(Camera2Enumerator(context), preferenceFrontCamera, eventsHandler)
        }
        if (capturer == null) {
            capturer = createCapturer(Camera1Enumerator(true), preferenceFrontCamera, eventsHandler)
        }

        capturer ?: return null

        return when (capturer.isScreencast) {
            fixedResolution -> capturer
            else -> {
                WebRtcLogger.d(TAG, "Wrap capturer: original.isScreencast=%b, fixedResolution=%b", capturer.isScreencast, fixedResolution)
                CameraVideoCapturerWrapper(capturer, fixedResolution)
            }
        }
    }

    private fun createCapturer(enumerator: CameraEnumerator,
                               preferenceFrontCamera: Boolean,
                               eventsHandler: CameraVideoCapturer.CameraEventsHandler? = null): CameraVideoCapturer? {
        var capturer = enumerator.deviceNames.asSequence()
            .mapNotNull { deviceName -> findDeviceCamera(enumerator, deviceName, preferenceFrontCamera, eventsHandler) }
            .firstOrNull()

        if (capturer != null) {
            return capturer
        }

        capturer = enumerator.deviceNames.asSequence()
            .mapNotNull { deviceName -> findDeviceCamera(enumerator, deviceName, preferenceFrontCamera, eventsHandler) }
            .firstOrNull()

        return capturer
    }

    private fun findDeviceCamera(enumerator: CameraEnumerator,
                                 deviceName: String,
                                 frontFacing: Boolean,
                                 eventsHandler: CameraVideoCapturer.CameraEventsHandler? = null) : CameraVideoCapturer? {
        var capturer: CameraVideoCapturer? = null
        if (enumerator.isFrontFacing(deviceName) == frontFacing) {
            capturer = enumerator.createCapturer(deviceName, eventsHandler)
        }
        return capturer
    }
}

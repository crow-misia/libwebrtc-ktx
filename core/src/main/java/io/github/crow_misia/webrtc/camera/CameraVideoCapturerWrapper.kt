package io.github.crow_misia.webrtc.camera

import org.webrtc.CameraVideoCapturer

class CameraVideoCapturerWrapper(
    private val capturer: CameraVideoCapturer,
    private val fixedResolution: Boolean = false,
) : CameraVideoCapturer by capturer {
    override fun isScreencast(): Boolean {
        return fixedResolution
    }
}

package io.github.crow_misia.webrtc

import android.content.Context
import io.github.crow_misia.webrtc.log.WebRtcLogger
import io.github.crow_misia.webrtc.option.MediaConstraintsOption
import org.webrtc.CameraVideoCapturer
import org.webrtc.MediaStream
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack

@Suppress("MemberVisibilityCanBePrivate", "unused")
class RTCLocalVideoManager(
    private val capturer: VideoCapturer,
    private val trackIdGenerator: () -> String,
) {
    companion object {
        private val TAG = RTCLocalVideoManager::class.simpleName
    }

    var source: VideoSource? = null
        private set
    var surfaceTextureHelper: SurfaceTextureHelper? = null
        private set

    var track: VideoTrack? = null
        private set

    var enabled: Boolean
        get() = track?.enabled() ?: false
        set(value) { track?.setEnabled(value) }

    fun initTrack(
        factory: PeerConnectionFactory,
        option: MediaConstraintsOption,
        appContext: Context,
    ) {
        WebRtcLogger.d(TAG, "initTrack isScreencast=%b", capturer.isScreencast)

        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", option.videoUpstreamContext)
        source = factory.createVideoSource(capturer.isScreencast)?.also {
            capturer.initialize(surfaceTextureHelper, appContext, it.capturerObserver)
        }

        val trackId = trackIdGenerator()
        track = factory.createVideoTrack(trackId, source)?.also {
            it.setEnabled(true)
            WebRtcLogger.d(TAG, "video track created: %s", it)
        }
    }

    fun attachTrackToStream(stream: MediaStream) {
        WebRtcLogger.d(TAG, "attachTrackToStream")
        track?.also {
            stream.addTrack(it)
        }
    }

    fun detachTrackToStream(stream: MediaStream) {
        WebRtcLogger.d(TAG, "detachTrackToStream")
        track?.also {
            stream.removeTrack(it)
        }
    }

    fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler) {
        WebRtcLogger.d(TAG, "switchCam %s", capturer::class.simpleName)

        if (capturer is CameraVideoCapturer) {
            capturer.switchCamera(switchHandler)
        }
    }

    fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler, cameraName: String) {
        WebRtcLogger.d(TAG, "switchCam %s, %s", capturer::class.simpleName, cameraName)

        if (capturer is CameraVideoCapturer) {
            capturer.switchCamera(switchHandler, cameraName)
        }
    }

    fun dispose() {
        WebRtcLogger.d(TAG, "dispose")

        WebRtcLogger.d(TAG, "dispose surfaceTextureHelper")
        surfaceTextureHelper?.dispose()
        surfaceTextureHelper = null

        WebRtcLogger.d(TAG, "dispose track")
        track?.also {
            it.setEnabled(false)
            it.dispose()
        }
        track = null

        WebRtcLogger.d(TAG, "dispose source")
        source?.dispose()
        source = null
    }
}

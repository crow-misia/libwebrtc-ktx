@file:Suppress("unused")

package io.github.crow_misia.webrtc

import android.content.Context
import io.github.crow_misia.webrtc.log.WebRtcLogger
import io.github.crow_misia.webrtc.option.MediaConstraintsOption
import org.webrtc.*

sealed interface RTCLocalVideoManager {
    val track: MediaStreamTrack?
    var enabled: Boolean

    fun initTrack(
        factory: PeerConnectionFactory,
        option: MediaConstraintsOption,
        appContext: Context,
    )

    fun attachTrackToStream(stream: MediaStream)
    fun detachTrackToStream(stream: MediaStream)
    fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler)
    fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler, cameraName: String)
    fun dispose()
}

object RTCNoneLocalVideoManager : RTCLocalVideoManager {
    override val track: MediaStreamTrack? = null
    override var enabled: Boolean = false
        set(_) { field = false }

    override fun initTrack(factory: PeerConnectionFactory,
                           option: MediaConstraintsOption,
                           appContext: Context) { }
    override fun attachTrackToStream(stream: MediaStream) { }
    override fun detachTrackToStream(stream: MediaStream) { }
    override fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler) { }
    override fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler, cameraName: String) { }
    override fun dispose() { }
}

class RTCLocalVideoManagerImpl(
    private val capturer: VideoCapturer,
    private val trackIdGenerator: () -> String,
) : RTCLocalVideoManager {
    companion object {
        private val TAG = RTCLocalVideoManagerImpl::class.simpleName
    }

    private var source: VideoSource? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    override var track: VideoTrack? = null
        private set

    override var enabled: Boolean
        get() = track?.enabled() ?: false
        set(value) { track?.setEnabled(value) }

    override fun initTrack(
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

    override fun attachTrackToStream(stream: MediaStream) {
        WebRtcLogger.d(TAG, "attachTrackToStream")
        track?.also {
            stream.addTrack(it)
        }
    }

    override fun detachTrackToStream(stream: MediaStream) {
        WebRtcLogger.d(TAG, "detachTrackToStream")
        track?.also {
            stream.removeTrack(it)
        }
    }

    override fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler) {
        WebRtcLogger.d(TAG, "switchCam %s", capturer::class.simpleName)

        if (capturer is CameraVideoCapturer) {
            capturer.switchCamera(switchHandler)
        }
    }

    override fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler, cameraName: String) {
        WebRtcLogger.d(TAG, "switchCam %s, %s", capturer::class.simpleName, cameraName)

        if (capturer is CameraVideoCapturer) {
            capturer.switchCamera(switchHandler, cameraName)
        }
    }

    override fun dispose() {
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

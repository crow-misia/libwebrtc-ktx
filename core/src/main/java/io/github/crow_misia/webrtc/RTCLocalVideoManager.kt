@file:Suppress("unused")

package io.github.crow_misia.webrtc

import android.content.Context
import io.github.crow_misia.webrtc.log.WebRtcLogger
import io.github.crow_misia.webrtc.option.MediaConstraintsOption
import org.webrtc.*
import java.util.*

interface RTCLocalVideoManager {
    val track: MediaStreamTrack?
    var enabled: Boolean

    fun initTrack(factory: PeerConnectionFactory,
                  option: MediaConstraintsOption,
                  appContext: Context)
    fun attachTrackToStream(stream: MediaStream)
    fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler?)
    fun dispose()
}

class RTCNoneLocalVideoManager : RTCLocalVideoManager {
    override val track: MediaStreamTrack? = null
    override var enabled: Boolean = false

    override fun initTrack(factory: PeerConnectionFactory,
                           option: MediaConstraintsOption,
                           appContext: Context) { }
    override fun attachTrackToStream(stream: MediaStream) { }
    override fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler?) { }
    override fun dispose() { }
}

class RTCLocalVideoManagerImpl(
    private val capturer: VideoCapturer
) : RTCLocalVideoManager {
    companion object {
        private val TAG = RTCLocalVideoManagerImpl::class.simpleName
    }

    private var source: VideoSource? = null
    override var track:  VideoTrack?  = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    override var enabled: Boolean
        get() = track?.enabled() ?: false
        set(value) { track?.setEnabled(value) }

    override fun initTrack(factory: PeerConnectionFactory,
                           option: MediaConstraintsOption,
                           appContext: Context) {
        WebRtcLogger.d(TAG, "initTrack isScreencast=%b", capturer.isScreencast)

        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", option.videoUpstreamContext)
        source = factory.createVideoSource(capturer.isScreencast)?.also {
            capturer.initialize(surfaceTextureHelper, appContext, it.capturerObserver)
        }

        val trackId = UUID.randomUUID().toString()
        track = factory.createVideoTrack(trackId, source)
        track?.setEnabled(true)
    }

    override fun attachTrackToStream(stream: MediaStream) {
        WebRtcLogger.d(TAG, "attachTrackToStream")
        track?.also { stream.addTrack(it) }
    }

    override fun switchCamera(switchHandler: CameraVideoCapturer.CameraSwitchHandler?) {
        WebRtcLogger.d(TAG, "switchCam %s", capturer::class.simpleName)

        if (capturer is CameraVideoCapturer) {
            capturer.switchCamera(switchHandler)
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

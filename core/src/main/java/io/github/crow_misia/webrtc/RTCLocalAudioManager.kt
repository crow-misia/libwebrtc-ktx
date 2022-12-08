@file:Suppress("unused")

package io.github.crow_misia.webrtc

import io.github.crow_misia.webrtc.log.WebRtcLogger
import io.github.crow_misia.webrtc.option.MediaConstraintsOption
import org.webrtc.*
import java.util.*

sealed interface RTCLocalAudioManager {
    val track: MediaStreamTrack?
    var enabled: Boolean

    fun initTrack(factory: PeerConnectionFactory, option: MediaConstraintsOption)
    fun dispose()
}

object RTCNoneLocalAudioManager : RTCLocalAudioManager {
    override val track: MediaStreamTrack? = null
    override var enabled: Boolean = false
        set(_) {
            field = false
        }

    override fun initTrack(factory: PeerConnectionFactory, option: MediaConstraintsOption) = Unit
    override fun dispose() = Unit
}

class RTCLocalAudioManagerImpl(
    private val trackIdGenerator: () -> String,
) : RTCLocalAudioManager {
    companion object {
        private val TAG = RTCLocalAudioManagerImpl::class.simpleName
    }

    private var source: AudioSource? = null

    override var track: AudioTrack? = null
        private set

    override var enabled
        get() = track?.enabled() ?: false
        set(value) {
            track?.setEnabled(value)
        }

    override fun initTrack(factory: PeerConnectionFactory, option: MediaConstraintsOption) {
        WebRtcLogger.d(TAG, "initTrack")

        val constraints = createSourceConstraints(option)
        source = factory.createAudioSource(constraints)
        WebRtcLogger.d(TAG, "audio source created: %s", source)
        val trackId = trackIdGenerator()
        track = factory.createAudioTrack(trackId, source)?.also {
            it.setEnabled(true)
            WebRtcLogger.d(TAG, "audio track created: %s", it)
        }
    }

    private fun createSourceConstraints(option: MediaConstraintsOption): MediaConstraints {
        return MediaConstraints().apply {
            // echo cancellation
            addMandatory(MediaConstraintsOption.ECHO_CANCELLATION_CONSTRAINT, option.audioProcessingEchoCancellation)
            // goog echo cancellation
            addMandatory(MediaConstraintsOption.GOOG_ECHO_CANCELLATION_CONSTRAINT, option.audioProcessingEchoCancellation)
            // auto gain control
            addMandatory(MediaConstraintsOption.AUTO_GAIN_CONTROL_CONSTRAINT, option.audioProcessingAutoGainControl)
            // goog auto gain control
            addMandatory(MediaConstraintsOption.GOOG_AUTO_GAIN_CONTROL_CONSTRAINT, option.audioProcessingAutoGainControl)
            // goog experimental auto gain control
            addMandatory(MediaConstraintsOption.GOOG_EXPERIMENTAL_AUTO_GAIN_CONTROL_CONSTRAINT, option.audioProcessingExperimentalAGC)
            // goog highpass filter
            addMandatory(MediaConstraintsOption.GOOG_HIGH_PASS_FILTER_CONSTRAINT, option.audioProcessingHighpassFilter)
            // noise suppression
            addMandatory(MediaConstraintsOption.NOISE_SUPPRESSION_CONSTRAINT, option.audioProcessingNoiseSuppression)
            // goog noise suppression
            addMandatory(MediaConstraintsOption.GOOG_NOISE_SUPPRESSION_CONSTRAINT, option.audioProcessingNoiseSuppression)
            // experimental noise suppression
            addMandatory(MediaConstraintsOption.GOOG_EXPERIMENTAL_NOISE_SUPPRESSION_CONSTRAINT, option.audioProcessingExperimentalNS)
            // goog typing noise detection
            addMandatory(MediaConstraintsOption.GOOG_TYPING_NOISE_DETECTION_CONSTRAINT, option.audioProcessingTypingNoiseDetection)
            // goog audio mirroring
            addMandatory(MediaConstraintsOption.GOOG_AUDIO_MIRRORING_CONSTRAINT, option.audioProcessingAudioMirroring)
        }
    }

    override fun dispose() {
        WebRtcLogger.d(TAG, "dispose")

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

    private fun MediaConstraints.addMandatory(name: String, value: Boolean) {
        val valueString = if (value) "true" else "false"
        mandatory.add(MediaConstraints.KeyValuePair(name, valueString))
    }
}

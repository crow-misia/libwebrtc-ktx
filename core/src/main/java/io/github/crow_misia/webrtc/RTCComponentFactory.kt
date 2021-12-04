@file:Suppress("unused")

package io.github.crow_misia.webrtc

import android.content.Context
import io.github.crow_misia.webrtc.log.WebRtcLogger
import io.github.crow_misia.webrtc.option.MediaConstraintsOption
import org.webrtc.*
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule
import java.util.*

class RTCComponentFactory(
    private val option: MediaConstraintsOption,
) {
    companion object {
        private val TAG = RTCComponentFactory::class.simpleName
    }

    fun createPeerConnectionFactory(context: Context, errorCallback: (errorReason: ErrorReason, errorMessage: String) -> Unit): PeerConnectionFactory {
        WebRtcLogger.d(TAG, "createPeerConnectionFactory")

        val options = PeerConnectionFactory.Options()
        val factoryBuilder = PeerConnectionFactory.builder()
            .setOptions(options)

        WebRtcLogger.d(TAG, "videoEncoderFactory: %s", option.videoEncoderFactory)
        WebRtcLogger.d(TAG, "videoUpstreamContext: %s", option.videoUpstreamContext)
        val encoderFactory = when {
            option.videoEncoderFactory != null -> requireNotNull(option.videoEncoderFactory)
            option.videoUpstreamContext != null -> DefaultVideoEncoderFactory(
                option.videoUpstreamContext,
                true,
                false
            )
            option.videoCodec == MediaConstraintsOption.VideoCodec.H264 &&
                    option.videoDownstreamContext != null -> DefaultVideoEncoderFactory(
                option.videoDownstreamContext,
                false,
                false
            )
            else -> SoftwareVideoEncoderFactory()
        }

        WebRtcLogger.d(TAG, "videoDecoderFactory: %s", option.videoDecoderFactory)
        WebRtcLogger.d(TAG, "videoDownstreamContext: %s", option.videoDownstreamContext)
        val decoderFactory = when {
            option.videoDecoderFactory != null -> requireNotNull(option.videoDecoderFactory)
            option.videoDownstreamContext != null -> DefaultVideoDecoderFactory(option.videoDownstreamContext)
            else -> SoftwareVideoDecoderFactory()
        }

        WebRtcLogger.d(TAG, "decoderFactory: %s", decoderFactory)
        WebRtcLogger.d(TAG, "encoderFactory: %s", encoderFactory)

        decoderFactory.supportedCodecs.forEach {
            WebRtcLogger.d(TAG, "decoderFactory supported codec: %s %s", it.name, it.params)
        }
        encoderFactory.supportedCodecs.forEach {
            WebRtcLogger.d(TAG, "encoderFactory supported codec: %s %s", it.name, it.params)
        }

        val audioDeviceModule = createJavaAudioDevice(context, errorCallback)

        return factoryBuilder
            .setAudioDeviceModule(audioDeviceModule)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory().also {
                audioDeviceModule.release()
            }
    }

    private fun createJavaAudioDevice(appContext: Context, errorCallback: (ErrorReason, String) -> Unit): AudioDeviceModule {
        WebRtcLogger.d(TAG, "createJavaAudioDevice")

        val audioRecordErrorCallback= object : JavaAudioDeviceModule.AudioRecordErrorCallback {
            override fun onWebRtcAudioRecordInitError(errorMessage: String) {
                WebRtcLogger.e(TAG, "onWebRtcAudioRecordInitError: %s", errorMessage)
                errorCallback(ErrorReason.AUDIO_RECORD_INIT_ERROR, errorMessage)
            }

            override fun onWebRtcAudioRecordStartError(
                errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode, errorMessage: String) {
                WebRtcLogger.e(TAG, "onWebRtcAudioRecordStartError: %s. %s", errorCode, errorMessage)
                errorCallback(ErrorReason.AUDIO_RECORD_START_ERROR, errorMessage)
            }

            override fun onWebRtcAudioRecordError(errorMessage: String) {
                WebRtcLogger.e(TAG, "onWebRtcAudioRecordError: %s", errorMessage)
                errorCallback(ErrorReason.AUDIO_RECORD_ERROR, errorMessage)
            }
        }
        val audioTrackErrorCallback = object : JavaAudioDeviceModule.AudioTrackErrorCallback {
            override fun onWebRtcAudioTrackInitError(errorMessage: String) {
                WebRtcLogger.e(TAG, "onWebRtcAudioTrackInitError: %s", errorMessage)
                errorCallback(ErrorReason.AUDIO_TRACK_INIT_ERROR, errorMessage)
            }

            override fun onWebRtcAudioTrackStartError(
                errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode, errorMessage: String) {
                WebRtcLogger.e(TAG, "onWebRtcAudioTrackStartError: %s. %s", errorCode, errorMessage)
                errorCallback(ErrorReason.AUDIO_TRACK_START_ERROR, errorMessage)
            }

            override fun onWebRtcAudioTrackError(errorMessage: String) {
                WebRtcLogger.e(TAG, "onWebRtcAudioTrackError: %s", errorMessage)
                errorCallback(ErrorReason.AUDIO_TRACK_ERROR, errorMessage)
            }
        }

        return JavaAudioDeviceModule.builder(appContext)
            .setUseHardwareAcousticEchoCanceler(JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported() && option.useHardwareAcousticEchoCanceler)
            .setUseHardwareNoiseSuppressor(JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported() && option.useHardwareNoiseSuppressor)
            .setAudioRecordErrorCallback(audioRecordErrorCallback)
            .setAudioTrackErrorCallback(audioTrackErrorCallback)
            .setAudioSource(option.audioSource)
            .setAudioFormat(option.audioFormat)
            .setAudioAttributes(option.audioAttributes)
            .setUseStereoInput(option.useStereoInput)
            .setUseStereoOutput(option.useStereoOutput)
            .setUseLowLatency(option.useLowLatency)
            .createAudioDeviceModule()
    }

    @JvmOverloads
    fun createVideoManager(trackIdGenerator: () -> String = { UUID.randomUUID().toString() }): RTCLocalVideoManager {
        val videoManager = option.videoCapturer?.let {
            RTCLocalVideoManagerImpl(it, trackIdGenerator)
        } ?: RTCNoneLocalVideoManager
        WebRtcLogger.d(TAG, "videoManager created: %s", videoManager)
        return videoManager
    }

    @JvmOverloads
    fun createAudioManager(trackIdGenerator: () -> String = { UUID.randomUUID().toString() }): RTCLocalAudioManager {
        return if (option.audioUpstreamEnabled) {
            RTCLocalAudioManagerImpl(trackIdGenerator)
        } else {
            RTCNoneLocalAudioManager
        }
    }

    enum class ErrorReason {
        AUDIO_TRACK_INIT_ERROR,
        AUDIO_TRACK_START_ERROR,
        AUDIO_TRACK_ERROR,
        AUDIO_RECORD_INIT_ERROR,
        AUDIO_RECORD_START_ERROR,
        AUDIO_RECORD_ERROR,
    }
}
@file:Suppress("unused")

package io.github.crow_misia.webrtc

import android.content.Context
import io.github.crow_misia.webrtc.log.WebRtcLogger
import io.github.crow_misia.webrtc.option.MediaConstraintsOption
import org.webrtc.*
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule

class RTCComponentFactory(
    private val option: MediaConstraintsOption
) {
    companion object {
        private val TAG = RTCComponentFactory::class.simpleName
    }

    fun createPeerConnectionFactory(context: Context, errorCallback: (errorMessage: String) -> Unit): PeerConnectionFactory {
        WebRtcLogger.d(TAG, "createPeerConnectionFactory")

        val options = PeerConnectionFactory.Options()
        val factoryBuilder = PeerConnectionFactory.builder()
            .setOptions(options)

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

        val decoderFactory = when {
            option.videoDecoderFactory != null -> requireNotNull(option.videoDecoderFactory)
            option.videoDownstreamContext != null -> DefaultVideoDecoderFactory(option.videoDownstreamContext)
            else -> SoftwareVideoDecoderFactory()
        }

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

    private fun createJavaAudioDevice(appContext: Context, errorCallback: (errorMessage: String) -> Unit): AudioDeviceModule {
        WebRtcLogger.d(TAG, "createJavaAudioDevice")

        val audioRecordErrorCallback: JavaAudioDeviceModule.AudioRecordErrorCallback =
            object : JavaAudioDeviceModule.AudioRecordErrorCallback {
                override fun onWebRtcAudioRecordInitError(errorMessage: String) {
                    WebRtcLogger.e(TAG, "onWebRtcAudioRecordInitError: %s", errorMessage)
                    errorCallback(errorMessage)
                }

                override fun onWebRtcAudioRecordStartError(
                    errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode, errorMessage: String) {
                    WebRtcLogger.e(TAG, "onWebRtcAudioRecordStartError: %s. %s", errorCode, errorMessage)
                    errorCallback(errorMessage)
                }

                override fun onWebRtcAudioRecordError(errorMessage: String) {
                    WebRtcLogger.e(TAG, "onWebRtcAudioRecordError: %s", errorMessage)
                    errorCallback(errorMessage)
                }
            }
        val audioTrackErrorCallback: JavaAudioDeviceModule.AudioTrackErrorCallback =
            object : JavaAudioDeviceModule.AudioTrackErrorCallback {
                override fun onWebRtcAudioTrackInitError(errorMessage: String) {
                    WebRtcLogger.e(TAG, "onWebRtcAudioTrackInitError: %s", errorMessage)
                    errorCallback(errorMessage)
                }

                override fun onWebRtcAudioTrackStartError(
                    errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode, errorMessage: String) {
                    WebRtcLogger.e(TAG, "onWebRtcAudioTrackStartError: %s. %s", errorCode, errorMessage)
                    errorCallback(errorMessage)
                }

                override fun onWebRtcAudioTrackError(errorMessage: String) {
                    WebRtcLogger.e(TAG, "onWebRtcAudioTrackError: %s", errorMessage)
                    errorCallback(errorMessage)
                }
            }

        return JavaAudioDeviceModule.builder(appContext)
            .setUseHardwareAcousticEchoCanceler(JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported() && option.useHardwareAcousticEchoCanceler)
            .setUseHardwareNoiseSuppressor(JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported() && option.useHardwareNoiseSuppressor)
            .setAudioRecordErrorCallback(audioRecordErrorCallback)
            .setAudioTrackErrorCallback(audioTrackErrorCallback)
            .setAudioSource(option.audioSource)
            .setUseStereoInput(option.useStereoInput)
            .setUseStereoOutput(option.useStereoOutput)
            .createAudioDeviceModule()
    }

    fun createVideoManager(): RTCLocalVideoManager {
        val videoManager = option.videoCapturer?.let {
            RTCLocalVideoManagerImpl(it)
        } ?: RTCNoneLocalVideoManager()
        WebRtcLogger.d(TAG, "videoManager created: %s", videoManager)
        return videoManager
    }

    fun createAudioManager(): RTCLocalAudioManager {
        return if (option.audioUpstreamEnabled) {
            RTCLocalAudioManagerImpl()
        } else {
            RTCNoneLocalAudioManager()
        }
    }
}
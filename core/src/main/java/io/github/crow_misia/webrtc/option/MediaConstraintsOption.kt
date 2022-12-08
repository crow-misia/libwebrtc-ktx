package io.github.crow_misia.webrtc.option

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.MediaRecorder
import org.webrtc.EglBase
import org.webrtc.VideoCapturer
import org.webrtc.VideoDecoderFactory
import org.webrtc.VideoEncoderFactory

@Suppress("MemberVisibilityCanBePrivate", "unused")
class MediaConstraintsOption {
    companion object {
        const val ECHO_CANCELLATION_CONSTRAINT = "echoCancellation"
        const val GOOG_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        const val AUTO_GAIN_CONTROL_CONSTRAINT = "autoGainControl"
        const val GOOG_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        const val GOOG_EXPERIMENTAL_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl2"
        const val NOISE_SUPPRESSION_CONSTRAINT = "noiseSuppression"
        const val GOOG_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
        const val GOOG_EXPERIMENTAL_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression2"
        const val GOOG_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        const val GOOG_TYPING_NOISE_DETECTION_CONSTRAINT = "googTypingNoiseDetection"
        const val GOOG_AUDIO_MIRRORING_CONSTRAINT = "googAudioMirroring"
    }

    enum class AudioCodec {
        OPUS,
    }

    enum class VideoCodec {
        H264,
        VP8,
        VP9,
        AV1,
    }

    /**
     * 端末組み込みの acoustic echo canceler の使用の設定
     */
    var useHardwareAcousticEchoCanceler: Boolean = true

    /**
     * 端末組み込みの noise suppressor の使用の設定
     */
    var useHardwareNoiseSuppressor: Boolean = true

    /**
     * 入力音声のエコーキャンセル処理の有無の設定
     */
    var audioProcessingEchoCancellation: Boolean = true

    /**
     * 入力音声の自動ゲイン調整処理の有無の設定
     */
    var audioProcessingAutoGainControl: Boolean = true

    /**
     * 入力音声の実験的自動ゲイン調整処理の有無の設定
     */
    var audioProcessingExperimentalAGC: Boolean = true

    /**
     * 入力音声のハイパスフィルタ処理の有無の設定
     */
    var audioProcessingHighpassFilter: Boolean = true

    /**
     * 入力音声のノイズ抑制処理の有無の設定
     */
    var audioProcessingNoiseSuppression: Boolean = true

    /**
     * 入力音声の実験的ノイズ抑制処理の有無の設定
     */
    var audioProcessingExperimentalNS: Boolean = true

    /**
     * 入力音声のタイピングノイズ検知処理の有無の設定
     */
    var audioProcessingTypingNoiseDetection: Boolean = false

    /**
     * 入力音声の左右チャンネルを入れ替える設定
     */
    var audioProcessingAudioMirroring: Boolean = false

    /**
     * 音声ソースの指定
     */
    var audioSource: Int = MediaRecorder.AudioSource.MIC

    /**
     * 音声フォーマット
     */
    var audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT

    /**
     * ステレオ入力
     */
    var useStereoInput: Boolean = false

    /**
     * ステレオ出力
     */
    var useStereoOutput: Boolean = false

    /**
     * 音声低遅延設定 (Android API 26以降で有効)
     */
    var useLowLatency: Boolean = false

    internal var audioDownstreamEnabled = false
    internal var audioUpstreamEnabled = false
    internal var videoDownstreamEnabled = false
    internal var videoUpstreamEnabled = false

    /**
     * 音声コーデック
     */
    var audioCodec: AudioCodec = AudioCodec.OPUS

    /**
     * 音声属性 (Android API 21以降で有効)
     */
    var audioAttributes: AudioAttributes? = null

    /**
     * 映像コーデック
     */
    var videoCodec: VideoCodec = VideoCodec.H264

    /**
     * 利用する VideoEncoderFactory の指定
     */
    var videoEncoderFactory: VideoEncoderFactory? = null

    /**
     * 利用する VideoDecoderFactory の指定
     */
    var videoDecoderFactory: VideoDecoderFactory? = null

    internal var videoCapturer: VideoCapturer? = null
    internal var videoDownstreamContext: EglBase.Context? = null
    internal var videoUpstreamContext: EglBase.Context? = null

    /**
     * 音声の視聴を有効にする
     */
    fun enableAudioDownstream() {
        audioDownstreamEnabled = true
    }

    /**
     * 音声の配信を有効にする
     */
    fun enableAudioUpstream() {
        audioUpstreamEnabled = true
    }

    /**
     * 映像の視聴を有効にする
     */
    fun enableVideoDownstream(eglContext: EglBase.Context?) {
        videoDownstreamEnabled = true
        videoDownstreamContext = eglContext
    }

    /**
     * 映像の配信を有効にする
     */
    fun enableVideoUpstream(capturer: VideoCapturer, eglContext: EglBase.Context?) {
        videoUpstreamEnabled = true
        videoCapturer = capturer
        videoUpstreamContext = eglContext
    }
}
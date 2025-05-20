package musicapp.player

import android.annotation.SuppressLint
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import musicapp.player.notification.MusicNotificationManager

@SuppressLint("UnsafeOptInUsageError")
object PlayerServiceLocator {
    private lateinit var appContext: PlatformContext

    fun init(context: PlatformContext) {
        if (!::appContext.isInitialized) {
            appContext = context
        }
    }

    val audioAttributes: AudioAttributes by lazy {
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    val exoPlayer: Player by lazy {
        ExoPlayer.Builder(appContext.applicationContext)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(DefaultTrackSelector(appContext.applicationContext))
            .build()
    }

    val mediaSession: MediaSession by lazy {
        MediaSession.Builder(appContext.applicationContext, exoPlayer).build()
    }

    val musicNotificationManager: MusicNotificationManager by lazy {
        MusicNotificationManager(appContext.applicationContext, exoPlayer)
    }

    val playerController: MediaPlayerController by lazy {
        MediaPlayerController(appContext)
    }
}

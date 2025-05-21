package musicapp.player.service

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import musicapp.player.PlayerServiceLocator
import musicapp.player.notification.MusicNotificationManager

class MediaService : MediaSessionService() {
    companion object {
        var isRunning = false
    }

    private val mediaSession: MediaSession = PlayerServiceLocator.mediaSession
    private val musicNotificationManager: MusicNotificationManager =
        PlayerServiceLocator.musicNotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        musicNotificationManager.startMusicNotificationService(
            mediaSession = mediaSession,
            mediaSessionService = this
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession


    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
        mediaSession.apply {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
        }
    }
}
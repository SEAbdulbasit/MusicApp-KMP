package musicapp.player.service

import android.app.Notification
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import musicapp.player.PlayerServiceLocator
import musicapp.player.notification.MusicNotificationManager
import musicapp.utils.PlatformContext

class MediaService : MediaSessionService() {
    companion object {
        var isRunning = false
        const val ACTION_UPDATE_NOTIFICATION = "musicapp.player.service.action.UPDATE_NOTIFICATION"
    }

    init {
        if (!PlayerServiceLocator.isInitialized()) {
            PlayerServiceLocator.init(PlatformContext(applicationContext))
        }
    }

    private val mediaSession by lazy { PlayerServiceLocator.mediaSession }
    private val musicNotificationManager by lazy {
        PlayerServiceLocator.musicNotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true

        if (intent?.action == ACTION_UPDATE_NOTIFICATION) {
            musicNotificationManager.startMusicNotificationService(
                mediaSession = mediaSession,
                mediaSessionService = this
            )
        } else {
            val initialNotification = createInitialNotification()
            startForeground(MusicNotificationManager.NOTIFICATION_ID, initialNotification)

            musicNotificationManager.startMusicNotificationService(
                mediaSession = mediaSession,
                mediaSessionService = this
            )
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createInitialNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, MusicNotificationManager.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Music App")
                .setContentText("Loading music...")
                .setSmallIcon(androidx.media3.session.R.drawable.media_session_service_notification_ic_music_note)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        } else {
            NotificationCompat.Builder(this, MusicNotificationManager.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Music App")
                .setContentText("Loading music...")
                .setSmallIcon(androidx.media3.session.R.drawable.media_session_service_notification_ic_music_note)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        }
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

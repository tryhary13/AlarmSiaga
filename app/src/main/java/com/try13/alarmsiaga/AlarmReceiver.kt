package com.try13.alarmsiaga

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm triggered!")

        // Buat MediaPlayer untuk memainkan suara alarm
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound).apply {
            isLooping = true
        }

        // Atur volume ke maksimum
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )

        // Mulai suara alarm
        mediaPlayer.start()

        // Hentikan alarm setelah 25 detik
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
                Log.d("AlarmReceiver", "Alarm stopped after 25 seconds.")
            }
        }, 25000)

        // 👉 Buka activity konfirmasi kehadiran
        val konfirmasiIntent = Intent(context, KonfirmasiActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(konfirmasiIntent)
    }
}

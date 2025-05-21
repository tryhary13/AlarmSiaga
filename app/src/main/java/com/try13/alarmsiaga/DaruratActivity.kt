package com.try13.alarmsiaga

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DaruratActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_darurat)

        val tvDarurat = findViewById<TextView>(R.id.tvDarurat)
        tvDarurat.text = "⚠️ MODE DARURAT AKTIF!\nSegera menuju titik aman!"

        try {
            // Set volume maksimum
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
            )

            // Mulai alarm keras
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
            mediaPlayer?.apply {
                isLooping = true
                start()
            } ?: run {
                Toast.makeText(this, "Gagal memulai alarm darurat!", Toast.LENGTH_SHORT).show()
                Log.e("DaruratActivity", "MediaPlayer.create() mengembalikan null.")
            }

            // Hentikan alarm otomatis setelah 30 detik
            Handler(Looper.getMainLooper()).postDelayed({
                stopAlarm()
            }, 30000)

        } catch (e: Exception) {
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("DaruratActivity", "Error saat memulai mode darurat", e)
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                Toast.makeText(this, "Alarm Darurat dihentikan", Toast.LENGTH_SHORT).show()
            }
        }
        mediaPlayer = null
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }
}

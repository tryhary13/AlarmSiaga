package com.try13.alarmsiaga

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var inputJudul: EditText
    private lateinit var inputPesan: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "Aplikasi dimulai")

        val btnSetAlarm = findViewById<Button>(R.id.btnSetAlarm)
        val btnKirimNotifikasi = findViewById<Button>(R.id.btnKirimNotifikasi)
        val btnModeDarurat = findViewById<Button>(R.id.btnModeDarurat)
        val btnLihatLog = findViewById<Button>(R.id.btnLihatLog)

        inputJudul = findViewById(R.id.inputJudul)
        inputPesan = findViewById(R.id.inputPesan)

        btnSetAlarm.setOnClickListener {
            Log.d("MainActivity", "Tombol Set Alarm ditekan")
            setAlarmSiaga()
        }

        btnKirimNotifikasi.setOnClickListener {
            Log.d("MainActivity", "Tombol Kirim Notifikasi ditekan")
            kirimNotifikasi()
        }

        btnModeDarurat.setOnClickListener {
            Log.d("MainActivity", "Tombol Mode Darurat ditekan")
            aktifkanModeDarurat()
        }

        btnLihatLog.setOnClickListener {
            Log.d("MainActivity", "Tombol Lihat Log ditekan")
            lihatLog()
        }
    }

    private fun setAlarmSiaga() {
        val calendar = Calendar.getInstance()
        val jamSekarang = calendar.get(Calendar.HOUR_OF_DAY)
        val menitSekarang = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(this, { _, jam, menit ->
            val waktuAlarm = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, jam)
                set(Calendar.MINUTE, menit)
                set(Calendar.SECOND, 0)
            }

            if (waktuAlarm.timeInMillis <= System.currentTimeMillis()) {
                waktuAlarm.add(Calendar.DAY_OF_MONTH, 1)
            }

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                waktuAlarm.timeInMillis,
                pendingIntent
            )

            val waktuFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(waktuAlarm.time)
            Toast.makeText(this, "Alarm Siaga diset pukul $waktuFormat", Toast.LENGTH_SHORT).show()
            simpanLog("Alarm Siaga diset pukul $waktuFormat")
            Log.d("MainActivity", "Alarm diset pukul $waktuFormat")
        }, jamSekarang, menitSekarang, true)

        timePicker.show()
    }

    private fun kirimNotifikasi() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "channel_alarm_siaga"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifikasi Alarm Siaga",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi penting untuk anggota tim"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("MainActivity", "Channel notifikasi dibuat")
        }

        val judul = inputJudul.text.toString().ifBlank { "AlarmSiaga" }
        val pesan = inputPesan.text.toString().ifBlank { "⚠️ Panggilan Siaga! Segera berkumpul di titik aman." }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(judul)
            .setContentText(pesan)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())

        Toast.makeText(this, "Notifikasi dikirim ke tim!", Toast.LENGTH_SHORT).show()
        simpanLog("Notifikasi dikirim: [$judul] $pesan")
        Log.d("MainActivity", "Notifikasi dikirim: [$judul] $pesan")
    }

    private fun aktifkanModeDarurat() {
        val intent = Intent(this, DaruratActivity::class.java)
        try {
            startActivity(intent)
            simpanLog("Mode Darurat diaktifkan.")
            Toast.makeText(this, "Mode Darurat Aktif!", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "Mode Darurat berhasil diaktifkan")
        } catch (e: Exception) {
            Log.e("MainActivity", "Gagal mengaktifkan Mode Darurat: ${e.message}", e)
        }
    }

    private fun lihatLog() {
        val intent = Intent(this, LogActivity::class.java)
        startActivity(intent)
        Log.d("MainActivity", "LogActivity dibuka")
    }

    private fun simpanLog(teks: String) {
        val waktu = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logLengkap = "$waktu - $teks\n"

        try {
            openFileOutput("log_siaga.txt", MODE_APPEND).use {
                it.write(logLengkap.toByteArray())
            }
            Log.d("MainActivity", "Log disimpan: $logLengkap")
        } catch (e: Exception) {
            Log.e("MainActivity", "Gagal menyimpan log: ${e.message}", e)
        }
    }
}

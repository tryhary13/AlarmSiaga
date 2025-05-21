package com.try13.alarmsiaga

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class LogActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private lateinit var btnRefresh: Button
    private lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        tvLog = findViewById(R.id.tvLog)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnClear = findViewById(R.id.btnClearLog)

        btnRefresh.setOnClickListener {
            loadLog()
        }

        btnClear.setOnClickListener {
            clearLog()
        }

        loadLog()
    }

    private fun loadLog() {
        try {
            val fileInput = openFileInput("log_siaga.txt")
            val reader = BufferedReader(InputStreamReader(fileInput))
            val logText = reader.readText()
            reader.close()

            if (logText.isBlank()) {
                tvLog.text = "Belum ada log siaga."
            } else {
                tvLog.text = logText
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tvLog.text = "Belum ada log siaga."
        }
    }

    private fun clearLog() {
        try {
            val outputStream = openFileOutput("log_siaga.txt", MODE_PRIVATE)
            val writer = OutputStreamWriter(outputStream)
            writer.write("") // kosongkan file
            writer.flush()
            writer.close()

            Toast.makeText(this, "Log berhasil dihapus.", Toast.LENGTH_SHORT).show()
            tvLog.text = "Belum ada log siaga."
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menghapus log.", Toast.LENGTH_SHORT).show()
        }
    }
}

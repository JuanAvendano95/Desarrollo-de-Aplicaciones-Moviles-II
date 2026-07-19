package com.example.ringtones

import android.content.ContentValues
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException

class MainActivity : AppCompatActivity() {

 private var mediaPlayer: MediaPlayer? = null
 private lateinit var audioManager: AudioManager

 override fun onCreate(savedInstanceState: Bundle?) {
 super.onCreate(savedInstanceState)
 setContentView(R.layout.activity_main)

 audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

 val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
 val btnStop = findViewById<Button>(R.id.btnStop)
 val volumeSeekBar = findViewById<SeekBar>(R.id.volumeSeekBar)

 // Configuración del SeekBar de volumen
 val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
 volumeSeekBar.max = maxVolume
 volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

 volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
 override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
 audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
 }
 override fun onStartTrackingTouch(seekBar: SeekBar?) {}
 override fun onStopTrackingTouch(seekBar: SeekBar?) {}
 })

 // Configuración botón Detener
 btnStop.setOnClickListener {
 mediaPlayer?.stop()
 mediaPlayer?.release()
 mediaPlayer = null
 }

 recyclerView.layoutManager = LinearLayoutManager(this)

        val ringtoneList = listOf(
            RingtoneItem("Alerta Nextel", R.raw.alerta_nextel),
            RingtoneItem("Angry Birds", R.raw.angry_birds),
            RingtoneItem("Bob Esponja", R.raw.bob_esponja),
            RingtoneItem("Bomberman", R.raw.bomberman),
            RingtoneItem("Game of Thrones", R.raw.game_thrones),
            RingtoneItem("iPhone Notificación", R.raw.iphone_notificacion),
            RingtoneItem("Apple Ring", R.raw.ringtones_apple),
            RingtoneItem("Nokia Ring", R.raw.ringtones_nokia),
            RingtoneItem("Sony Ring", R.raw.ringtones_sony),
            RingtoneItem("WhatsApp Apple", R.raw.whatsapp_apple)
        )

        recyclerView.adapter = RingtoneAdapter(ringtoneList,
            onPlayClick = { ringtone ->
                playRingtone(ringtone.resourceId)
            },
            onDownloadClick = { ringtone ->
                downloadRingtone(ringtone)
            }
        )
    }

    private fun playRingtone(resourceId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer?.start()
    }

    private fun downloadRingtone(ringtone: RingtoneItem) {
        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${ringtone.name}.mp3")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        try {
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { output ->
                    resources.openRawResource(ringtone.resourceId).use { input ->
                        input.copyTo(output)
                    }
                }
                Toast.makeText(this, "Archivo guardado en Descargas", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}

package com.example.ringtones

import android.content.ContentValues
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnStop: Button
    private lateinit var volumeSeekBar: SeekBar
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        btnStop = findViewById(R.id.btnStop)
        volumeSeekBar = findViewById(R.id.volumeSeekBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val ringtoneList = listOf(
            RingtoneItem("Alerta Nextel", R.raw.alerta_nextel),
            RingtoneItem("Angry Birds", R.raw.angry_birds),
            RingtoneItem("Bob Esponja", R.raw.bob_esponja),
            RingtoneItem("Bomberman", R.raw.bomberman),
            RingtoneItem("Game of Thrones", R.raw.game_thrones),
            RingtoneItem("iPhone Notificación", R.raw.iphone_notificacion),
            RingtoneItem("Ringtones Apple", R.raw.ringtones_apple),
            RingtoneItem("Ringtones Nokia", R.raw.ringtones_nokia),
            RingtoneItem("Ringtones Sony", R.raw.ringtones_sony),
            RingtoneItem("WhatsApp Apple", R.raw.whatsapp_apple)
        )

        val adapter = RingtoneAdapter(
            ringtones = ringtoneList,
            onPlayClick = { ringtone -> playRingtone(ringtone) },
            onDownloadClick = { ringtone -> downloadRingtone(ringtone) },
            onShareClick = { ringtone -> shareRingtone(ringtone) }
        )

        recyclerView.adapter = adapter

        volumeSeekBar.max = 100
        volumeSeekBar.progress = 70
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                mediaPlayer?.setVolume(volume, volume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnStop.setOnClickListener {
            stopRingtone()
        }
    }

    private fun playRingtone(ringtone: RingtoneItem) {
        try {
            if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }

            mediaPlayer = MediaPlayer.create(this, ringtone.resourceId).apply {
                val currentVolume = volumeSeekBar.progress / 100f
                setVolume(currentVolume, currentVolume)

                start()
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                }
            }
            Toast.makeText(this, "Reproduciendo: ${ringtone.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRingtone() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
            Toast.makeText(this, "Reproducción detenida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadRingtone(ringtone: RingtoneItem) {
        try {
            val inputStream = resources.openRawResource(ringtone.resourceId)
            val filename = "${ringtone.name}.mp3"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    Toast.makeText(this, "Descargado: ${ringtone.name}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al descargar el archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareRingtone(ringtone: RingtoneItem) {
        try {
            val inputStream = resources.openRawResource(ringtone.resourceId)
            val cacheFile = File(cacheDir, "${ringtone.name}.mp3")
            val outputStream = FileOutputStream(cacheFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val uri: Uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", cacheFile)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/mp3"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "Compartir ${ringtone.name} vía"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al compartir el archivo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

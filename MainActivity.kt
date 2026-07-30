package com.example.ringtones

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var btnStop: Button
    private lateinit var ringtoneAdapter: RingtoneAdapter

    // Lista corregida y limpia con los nombres exactos de tus archivos en res/raw
    private val ringtoneList = listOf(
        RingtoneItem("Alerta Nextel", R.raw.alerta_nextel),
        RingtoneItem("Angry Birds", R.raw.angry_birds),
        RingtoneItem("Bob Esponja", R.raw.bob_esponja),
        RingtoneItem("Bomberman", R.raw.bomberman),
        RingtoneItem("Game of Thrones", R.raw.game_thrones),
        RingtoneItem("iPhone Notificación", R.raw.iphone_notificacion),
        RingtoneItem("Apple Ringtones", R.raw.ringtones_apple),
        RingtoneItem("Nokia Ringtones", R.raw.ringtones_nokia),
        RingtoneItem("Sony Ringtones", R.raw.ringtones_sony),
        RingtoneItem("WhatsApp Apple", R.raw.whatsapp_apple)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        volumeSeekBar = findViewById(R.id.volumeSeekBar)
        btnStop = findViewById(R.id.btnStop)

        // Asegúrate de que este ID sea el correcto en tu activity_main.xml (ej. R.id.recyclerView)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        ringtoneAdapter = RingtoneAdapter(
            ringtoneList,
            onPlayClick = { ringtone -> playRingtone(ringtone) },
            onShareClick = { ringtone -> shareRingtone(ringtone) },
            onDownloadClick = { ringtone ->
                Toast.makeText(this, "Descargando ${ringtone.name}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = ringtoneAdapter

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
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, ringtone.resourceId)
            val volume = volumeSeekBar.progress / 100f
            mediaPlayer?.setVolume(volume, volume)
            mediaPlayer?.start()
            Toast.makeText(this, "Reproduciendo ${ringtone.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al reproducir el audio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRingtone() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
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

            showPointEarnedDialog()

            startActivity(Intent.createChooser(intent, "Compartir ${ringtone.name} vía"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al compartir el archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPointEarnedDialog() {
        AlertDialog.Builder(this)
            .setTitle("¡FELICIDADES!")
            .setMessage("Has ganado un punto")
            .setPositiveButton("ACEPTAR") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("CANCELAR") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}

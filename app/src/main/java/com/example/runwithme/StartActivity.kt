package com.example.runwithme
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.example.runwithme.databinding.ActivityPageBinding

class StartActivity : ComponentActivity() {

    private lateinit var binding: ActivityPageBinding
    private lateinit var locationManager: LocationManager
    private lateinit var mediaPlayer: MediaPlayer
    private val songs = listOf(R.raw.rocky_balboa, R.raw.we_are_the_champions, R.raw.never_give_up) // replace with your songs
    private val songNames = listOf("Rocky Balboa", "We Are The Champions", "Never Give Up")
    private var currentSongIndex = 0

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val speed = location.speed * 3.6
            binding.SpeedValueTextView.setText("$speed km/h")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                binding.progressBar.progress = mediaPlayer.currentPosition
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playSong(currentSongIndex)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permissions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        } else {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        }

        binding.playPauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.playPauseButton.setImageResource(R.drawable.pause_music)
            } else {
                mediaPlayer.start()
                binding.playPauseButton.setImageResource(R.drawable.play_music)
            }
        }

        binding.previousButton.setOnClickListener {
            currentSongIndex = if (currentSongIndex > 0) currentSongIndex - 1 else songs.size - 1
            playSong(currentSongIndex)
        }

        binding.nextButton.setOnClickListener {
            currentSongIndex = if (currentSongIndex < songs.size - 1) currentSongIndex + 1 else 0
            playSong(currentSongIndex)
        }
    }

    private fun playSong(index: Int) {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }

        mediaPlayer = MediaPlayer.create(this, songs[index])
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            Log.e("Music player", "Error occurred while playing audio.")
            true
        }
        binding.progressBar.max = mediaPlayer.duration
        mediaPlayer.start()
        handler.post(updateProgressRunnable)
        binding.songNameTextView.setText(songNames[index])
        binding.playPauseButton.setImageResource(R.drawable.play_music)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)
        mediaPlayer.release()
    }
}
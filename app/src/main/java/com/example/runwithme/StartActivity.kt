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
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.example.runwithme.databinding.ActivityPageBinding
import java.util.concurrent.TimeUnit
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StartActivity : ComponentActivity() {

    private lateinit var binding: ActivityPageBinding
    private lateinit var locationManager: LocationManager
    private var totalDistance = 0f
    private var lastLocation: Location? = null
    private var startTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mediaPlayer: MediaPlayer
    private val songs = listOf(R.raw.rocky_balboa, R.raw.we_are_the_champions, R.raw.never_give_up) // replace with your songs
    private val songNames = listOf("Rocky Balboa", "We Are The Champions", "Never Give Up")
    private var currentSongIndex = 0
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var isDark = false
    private var isClose = false

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("LocationUpdates", "onLocationChanged called")
            lastLocation?.let {
                totalDistance += it.distanceTo(location) / 1000 // convert to kilometers
                val roundedDistance = String.format("%.1f", totalDistance)
                binding.distanceValueTextView.setText("$roundedDistance KM")
            }
            lastLocation = location
            val speed = location.speed * 3.6
            val roundedSpeed = String.format("%.1f", speed)
            binding.SpeedValueTextView.setText("$roundedSpeed km/h")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                binding.progressBar.progress = mediaPlayer.currentPosition
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            val millis = SystemClock.elapsedRealtime() - startTime
            val hours = TimeUnit.MILLISECONDS.toHours(millis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
            binding.TimeValueTextView.setText("$hours:$minutes:$seconds")
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTime = SystemClock.elapsedRealtime()
        handler.post(updateTimerRunnable)

        playSong(currentSongIndex)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationUpdates", "Requesting location permissions")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d("LocationUpdates", "GPS is not enabled")
            } else {
                Log.d("LocationUpdates", "GPS is enabled. Requesting location updates")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
            }
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

    private val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_PROXIMITY -> {
                    isClose = event.values[0] < 0.5
                }
                Sensor.TYPE_LIGHT -> {
                    isDark = event.values[0] < 5
                }
            }

            val isInPocket = isClose && isDark
            binding.playPauseButton.isEnabled = !isInPocket
            binding.nextButton.isEnabled = !isInPocket
            binding.previousButton.isEnabled = !isInPocket
            binding.stopActivityButton.isEnabled = !isInPocket
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

    override fun onResume() {
        super.onResume()
        proximitySensor?.also { proximity ->
            sensorManager.registerListener(sensorListener, proximity, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.also { light ->
            sensorManager.registerListener(sensorListener, light, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)
        mediaPlayer.release()
    }
}
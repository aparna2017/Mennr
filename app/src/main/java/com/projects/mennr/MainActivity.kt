package com.projects.mennr

import android.Manifest
import android.app.ActivityManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.HardwarePropertiesManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.projects.mennr.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        userPermissions()
        binding.fab.setOnClickListener { view ->
            scanAndDocument()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }

    // Scan and Document hardware components on the mobile
    private fun scanAndDocument() {

        try {
            var brand = Build.BRAND
            var manufacturer = Build.MANUFACTURER
            var model = Build.MODEL
            var device = Build.DEVICE
            var hardware = Build.HARDWARE
            var board = Build.BOARD
            var bootloader = Build.BOOTLOADER
            var fingerprint = Build.FINGERPRINT
            var display = Build.DISPLAY
            var sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            var cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            var audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            var activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            var hardwarePropertiesManager =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    getSystemService(Context.HARDWARE_PROPERTIES_SERVICE) as HardwarePropertiesManager
                } else {
                    TODO("VERSION.SDK_INT < N")
                }
            var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val stringBuilder = StringBuilder()
            stringBuilder.append("\n").append("Brand :" + brand)
            stringBuilder.append("\n").append("Manufacturer :" + manufacturer)
            stringBuilder.append("\n").append("Model :" + model)
            stringBuilder.append("\n").append("Device :" + device)
            stringBuilder.append("\n").append("Phone Hardware :" + hardware)
            stringBuilder.append("\n").append("Board :" + board)
            stringBuilder.append("\n").append("Fingerprint :" + fingerprint)
            stringBuilder.append("\n").append("BootLoader :" + bootloader)
            stringBuilder.append("\n").append("Display :" + display)
            var memoryInfo = ActivityManager.MemoryInfo()
            activityManager?.getMemoryInfo(memoryInfo)
            stringBuilder.append("\n").append("Memory Available :" + memoryInfo.availMem)
            stringBuilder.append("\n").append("Memory Total :" + memoryInfo.totalMem)
            stringBuilder.append("\n")
                .append("Sensors :")
            stringBuilder.append("\n")
                .append(sensorManager.getSensorList(Sensor.TYPE_ALL).toString())
            stringBuilder.append("\n")
                .append("Camera :")
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)

                stringBuilder.append("\n").append(
                    "LENS_FACING : " + cameraManager.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.LENS_FACING)
                )

                stringBuilder.append("\n").append(
                    "FLASH_INFO_AVAILABLE : " + cameraManager.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                )

                stringBuilder.append("\n").append(
                    "INFO_SUPPORTED_HARDWARE_LEVEL : " + cameraManager.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL).toString()
                )

                stringBuilder.append("\n").append(
                    "SENSOR_INFO_PHYSICAL_SIZE : " + cameraManager.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
                )
            }
            stringBuilder.append("\n")
                .append("Audio :")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                for (mic in audioManager.microphones) {
                    stringBuilder.append("\n")
                        .append(mic.description)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stringBuilder.append("\n")
                    .append("Playback Configurations :" + audioManager.activePlaybackConfigurations.toString())
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                stringBuilder.append("\n")
                    .append("Communication Device :" + audioManager.communicationDevice.toString())
            }
            stringBuilder.append("\n")
                .append("Active Recording Configurations :" + audioManager.activeRecordingConfigurations.toString())
            for (loc in locationManager.getProviders(false))
                stringBuilder.append("\n")
                    .append("Location :" + loc.toString())

            val path = File(Environment.getExternalStorageDirectory(), "/Downloads/")
            var success = true
            if (!path.exists()) {
                success = path.mkdir()
            }
            if (success) {
                //Write text to file!
                File(path, "hardwarespecs.txt").writeText(stringBuilder.toString())
                Toast.makeText(this, resources.getString(R.string.success), Toast.LENGTH_SHORT)
                    .show()

            }

        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun userPermissions(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        }
    }

}
package com.rookmotion.rooktraining.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.rookmotion.rooktraining.R
import com.rookmotion.rooktraining.databinding.ActivityHomeBinding
import com.rookmotion.rooktraining.state.RMViewModelFactory
import com.rookmotion.rooktraining.state.UserViewModel
import com.rookmotion.rooktraining.ui.scanner.SensorScannerActivity
import com.rookmotion.rooktraining.ui.trainingtype.TrainingTypeActivity
import com.rookmotion.rooktraining.utils.rmLocator
import com.rookmotion.rooktraining.utils.toastShort
import com.rookmotion.utils.sdk.permissions.BluetoothManager
import com.rookmotion.utils.sdk.permissions.LocationManager
import com.rookmotion.utils.sdk.permissions.PermissionsManager
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val permissionsManager = PermissionsManager()
    private val locationManager = LocationManager()
    private val bluetoothManager = BluetoothManager()

    private val userViewModel by viewModels<UserViewModel> { RMViewModelFactory(rmLocator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPermissions()
        initState()
        initActions()

        userViewModel.getUser()
        userViewModel.syncIndexes()
    }

    override fun onDestroy() {
        permissionsManager.onDestroy()
        locationManager.onDestroy()
        bluetoothManager.onDestroy()

        super.onDestroy()
    }

    private fun initPermissions() {
        permissionsManager.registerLocationPermissionsListener(
            componentActivity = this,
            onAllowed = { checkPermissions() },
            onDenied = { toastShort(getString(R.string.location_permissions_are_required)) }
        )

        permissionsManager.registerBluetoothPermissionsListener(
            componentActivity = this,
            onAllowed = { checkPermissions() },
            onDenied = { toastShort(getString(R.string.bluetooth_permissions_are_required)) }
        )

        locationManager.registerEnableListener(
            componentActivity = this,
            onSuccess = { checkPermissions() },
            onFailure = { toastShort(getString(R.string.gps_is_required)) }
        )

        bluetoothManager.registerEnableListener(
            componentActivity = this,
            onSuccess = { checkPermissions() },
            onFailure = { toastShort(getString(R.string.bluetooth_is_required)) }
        )

        checkPermissions()
    }

    private fun checkPermissions() {
        val locationPermission = permissionsManager.hasLocationPermissions(this)
        val bluetoothPermission = permissionsManager.hasBluetoothPermissions(this)
        val gpsIsOn = locationManager.isEnabled(this)
        val bluetoothIsOn = bluetoothManager.isEnabled(this)

        if (locationPermission) {
            if (bluetoothPermission) {
                if (gpsIsOn) {
                    if (bluetoothIsOn) {
                        enablePermissionsScreens()
                    } else {
                        disablePermissionsScreens()
                        bluetoothManager.requestEnable()
                    }
                } else {
                    disablePermissionsScreens()
                    locationManager.requestEnable()
                }
            } else {
                disablePermissionsScreens()
                permissionsManager.requestBluetoothPermissions()
            }
        } else {
            disablePermissionsScreens()
            permissionsManager.requestLocationPermissions()
        }
    }

    private fun enablePermissionsScreens() {
        binding.permissionsWarning.isVisible = false
        binding.grantPermissions.isVisible = false

        binding.individualTraining.isEnabled = true
        binding.sensorScanner.isEnabled = true
    }

    private fun disablePermissionsScreens() {
        binding.permissionsWarning.isVisible = true
        binding.grantPermissions.isVisible = true

        binding.individualTraining.isEnabled = false
        binding.sensorScanner.isEnabled = false
    }

    private fun initState() {
        userViewModel.user.observe(this) {
            if (it != null) {
                binding.userUuid.text = "UUID: ${it.userUUID}"
                binding.userEmail.text = "Email: ${it.email}"
            } else {
                Timber.e("user error: user state returned null")
            }
        }
    }

    private fun initActions() {
        binding.grantPermissions.setOnClickListener { checkPermissions() }

        binding.individualTraining.setOnClickListener {
            startActivity(Intent(this, TrainingTypeActivity::class.java))
        }

        binding.sensorScanner.setOnClickListener {
            startActivity(Intent(this, SensorScannerActivity::class.java))
        }
    }
}
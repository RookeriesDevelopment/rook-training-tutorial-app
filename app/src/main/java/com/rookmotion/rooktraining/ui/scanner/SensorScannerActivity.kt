package com.rookmotion.rooktraining.ui.scanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.rookmotion.rooktraining.R
import com.rookmotion.rooktraining.RookTrainingApplication
import com.rookmotion.rooktraining.databinding.ActivitySensorScannerBinding
import com.rookmotion.rooktraining.state.RMApplicationViewModelFactory
import com.rookmotion.rooktraining.state.SensorScannerViewModel
import com.rookmotion.rooktraining.ui.scanner.adapter.BLPeripheralAdapter
import com.rookmotion.rooktraining.utils.snackLong
import com.rookmotion.rooktraining.utils.toastLong
import com.rookmotion.rooktraining.utils.toastShort
import com.welie.blessed.ScanFailure

class SensorScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensorScannerBinding

    private lateinit var blPeripheralAdapter: BLPeripheralAdapter

    private val scannerViewModel by viewModels<SensorScannerViewModel> {
        RMApplicationViewModelFactory(application as RookTrainingApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blPeripheralAdapter = BLPeripheralAdapter { scannerViewModel.linkToSensor(it) }

        initExtras()
        initState()
        initUI()
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            scannerViewModel.stopScan()
            scannerViewModel.releaseScannerResources()
        }

        super.onDestroy()
    }

    private fun initExtras() {
        val mac = intent?.extras?.getString(ScannerKeys.SENSOR_CONNECTED_MAC)?.uppercase()
        val name = intent?.extras?.getString(ScannerKeys.SENSOR_CONNECTED_NAME)

        scannerViewModel.setConnected(mac, name ?: getString(R.string.unknown_sensor))
    }

    private fun initState() {
        scannerViewModel.connectedSensorState.observe(this) {
            when (it) {
                SensorScannerViewModel.ConnectedSensorState.None -> {
                    binding.connectedSensor.isVisible = false
                }
                is SensorScannerViewModel.ConnectedSensorState.Connected -> {
                    binding.connectedSensor.text = getString(
                        R.string.connected_to_placeholder,
                        it.name
                    )
                    binding.connectedSensor.isVisible = true
                }
            }
        }

        scannerViewModel.linkSensorState.observe(this) {
            if (it.linking) {
                showProgress()
            } else {
                showItems()
            }

            if (it.success) {
                toastShort(getString(R.string.sensor_linked))
            }

            if (it.mac != null) {
                selectAndExit(it.mac)
            }

            if (it.error != null) {
                toastLong(it.error)
            }
        }

        lifecycleScope.launchWhenResumed {
            scannerViewModel.scannerImp.discoveredSensors.collect {
                if (it.isNotEmpty()) {
                    blPeripheralAdapter.submitList(it)

                    showItems()
                } else {
                    showProgress()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            scannerViewModel.scannerImp.scannerError.collect {
                if (it != null) {
                    showError(it)
                }
            }
        }
    }

    private fun initUI() {
        binding.data.setHasFixedSize(true)
        binding.data.adapter = blPeripheralAdapter
    }

    private fun selectAndExit(mac: String) {
        val intent = Intent().apply {
            putExtra(ScannerKeys.SENSOR_SELECTED, mac)
        }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showProgress() {
        binding.progress.root.isVisible = true
        binding.data.isVisible = false
    }

    private fun showItems() {
        binding.progress.root.isVisible = false
        binding.data.isVisible = true
    }

    private fun showError(scanFailure: ScanFailure) {
        val messageRes = when (scanFailure) {
            ScanFailure.APPLICATION_REGISTRATION_FAILED,
            ScanFailure.INTERNAL_ERROR,
            ScanFailure.UNKNOWN -> R.string.error_internal_bluetooth
            ScanFailure.ALREADY_STARTED -> R.string.error_already_scanning
            ScanFailure.FEATURE_UNSUPPORTED -> R.string.error_not_supported
            ScanFailure.OUT_OF_HARDWARE_RESOURCES -> R.string.error_no_resources
            ScanFailure.SCANNING_TOO_FREQUENTLY -> R.string.error_too_much_scans
        }

        binding.root.snackLong(getString(messageRes), getString(R.string.retry)) {
            scannerViewModel.startScan(fromError = true)
        }
    }
}
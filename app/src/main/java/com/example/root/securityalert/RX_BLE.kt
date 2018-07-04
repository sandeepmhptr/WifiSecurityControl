package com.example.root.securityalert

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
//import com.loop.toolkit.kotlin.Utils.extensions.applicationContext
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.bluetoothManager
import java.security.AccessController.getContext

class BleScannerFlowable private constructor(
        private val adapter: BluetoothAdapter,
        private val stopCallback: ((BluetoothDevice) -> Boolean)? = null,
        private val scanFilter: Array<out ScanFilter>? = null,
        private val scanSettings: ScanSettings? = null
): FlowableOnSubscribe<BluetoothDevice> {

    override fun subscribe(e: FlowableEmitter<BluetoothDevice>) {
        if (isAboveLollipop()) {
            setupSubscriber21(e)
        } else {
            setupSubscriber(e)
        }
    }

    private fun setupSubscriber(emitter: FlowableEmitter<BluetoothDevice>) {
        adapter.startLeScan { device, rssi, scanRecord ->
            if (stopCallback?.invoke(device) == true) {
                adapter.stopLeScan { device, rssi, scanRecord ->
                    emitter.onComplete()
                }
            }
            emitter.onNext(device)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupSubscriber21(emitter: FlowableEmitter<BluetoothDevice>) {
        val isFilterSet = scanFilter?.isNotEmpty() == true
        val filters = if (isFilterSet) scanFilter!!.toMutableList() else mutableListOf()
        adapter.bluetoothLeScanner?.startScan(filters, scanSettings, ScanCaller(emitter))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class ScanCaller(private val emitter: FlowableEmitter<BluetoothDevice>) : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            emitter.onError(Throwable("Scan failed with code $errorCode"))
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (result != null && result.device != null) {
                if (stopCallback?.invoke(result.device) == true) {
                    adapter.bluetoothLeScanner?.stopScan(this)
                    emitter.onComplete()
                } else {
                    emitter.onNext(result.device)
                }
            }
        }
    }

    class Builder(private val adapter: BluetoothAdapter) {
        private var scanFilter: Array<out ScanFilter>? = null
        private var stopCallback: ((BluetoothDevice) -> Boolean)? = null
        private var settingsBuilder: ScanSettings.Builder? = null
        private var scanSettings: ScanSettings? = null

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun setFilterByName(name: String) : Builder {
            setScanFilters(ScanFilter.Builder()
                    .setDeviceName(name)
                    .build())
            return this
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun setScanFilters(vararg scanFilter: ScanFilter) : Builder{
            this.scanFilter = scanFilter
            return this
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun setScanMode(scanMode: ScanModes) : Builder{
            getSettingsBuilder().setScanMode(scanMode.settingId)
            return this
        }

        fun setStopCallback(function: (BluetoothDevice) -> Boolean) : Builder {
            this.stopCallback = function
            return this
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun setScanSettings(scanSettings: ScanSettings) : Builder {
            this.scanSettings = scanSettings
            return this
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun build(): Flowable<BluetoothDevice> =
                Flowable.create(BleScannerFlowable(adapter,
                        stopCallback,
                        scanFilter,
                        scanSettings ?: settingsBuilder?.build()),
                        BackpressureStrategy.LATEST)

        fun buildLower(): Flowable<BluetoothDevice> =
                Flowable.create(BleScannerFlowable(adapter, stopCallback),
                        BackpressureStrategy.LATEST)

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        private fun getSettingsBuilder() : ScanSettings.Builder {
            if (settingsBuilder == null) {
                settingsBuilder = ScanSettings.Builder()
            }
            return settingsBuilder!!
        }
    }

}


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
enum class ScanModes(val settingId: Int) {
    LOW_LATENCY(ScanSettings.SCAN_MODE_LOW_LATENCY),
    LOW_POWER(ScanSettings.SCAN_MODE_LOW_POWER),
    BALANCED(ScanSettings.SCAN_MODE_BALANCED),
    OPPORTUNISTIC(ScanSettings.SCAN_MODE_BALANCED)
}

fun startScanning( adapter: BluetoothAdapter,ctxt: Context) {
    //val adapter = applicationContext.bluetoothManager.adapter ?: return
    val bleScanner = if (isAboveLollipop()) {
        BleScannerFlowable.Builder(adapter)
                .setScanMode(ScanModes.BALANCED)
                .setStopCallback { it.name == "MyBleDevice" }
                .build()

    } else {
        BleScannerFlowable.Builder(adapter)
                .setStopCallback { it.name == "MyBleDevice" }
                .buildLower()
    }

    bleScanner
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // do something with the bluetooth device
                val add=it.address
                val BTName=it.name
                Log.i(TAG, add)
                Log.i(TAG, add)
                Log.i(TAG, add)
                Log.i(TAG, add)
                Toast.makeText(ctxt, add, Toast.LENGTH_SHORT).show()
                Toast.makeText(ctxt, BTName, Toast.LENGTH_SHORT).show()
            }, {
                // handle error
                it.printStackTrace()
            }, {
                // do on finish scan
            })
}

fun isAboveLollipop() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
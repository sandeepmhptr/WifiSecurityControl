package com.example.root.securityalert;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by root on 27/6/18.
 */

public class BluetoothSendRecv extends BluetoothGattCallback{

    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;
    public String mBluetoothDeviceAddress;
    public BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    // private final IBinder mBinder = new BluetoothLeService.LocalBinder();
    public Context cntxt;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    BluetoothSendRecv(Context contxt)
    {
        cntxt=contxt;
    }
    // Various callback methods defined by the BLE API.
    public final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        mConnectionState = STATE_CONNECTED;
                        broadcastUpdate(intentAction);
                        Log.i(TAG, "Connected to GATT server.");
                        Log.i(TAG, "Attempting to start service discovery:" +
                                mBluetoothGatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        mConnectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                        broadcastUpdate(intentAction);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                        for (BluetoothGattService gattService : gatt.getServices()) {
                            Log.i(TAG, "onServicesDiscovered: ---------------------");
                            Log.i(TAG, "onServicesDiscovered: service=" + gattService.getUuid());
                            for (BluetoothGattCharacteristic characteristic : gattService.getCharacteristics()) {
                                Log.i(TAG, "onServicesDiscovered: characteristic=" + characteristic.getUuid());

                                if (characteristic.getUuid().toString().equals("0000fee9-0000-1000-8000-00805f9b34fb"/*0000ffe9-0000-1000-8000-00805f9b34fb"*/)) {

                                    Log.w(TAG, "onServicesDiscovered: found LED");

                                    String originalString = "560D0F0600F0AA";

                                    byte[] b = hexStringToByteArray(originalString);

                                    characteristic.setValue(b); // call this BEFORE(!) you 'write' any stuff to the server
                                    mBluetoothGatt.writeCharacteristic(characteristic);
                                    //Toast.makeText(this., "R.string.error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
                                    //finish()
                                    System.out.println("Writing Mithun");
                                    Toast.makeText(cntxt, originalString, Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "onServicesDiscovered: , write bytes?! " + byteToHexStr(b));
                                }
                            }
                        }

                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        byte[] data = characteristic.getValue();
                        System.out.println("reading");
                        System.out.println(new String(data));
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }
                //...
            };
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //sendBroadcast(intent);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteToHexStr(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b: bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }


    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }
        //sendBroadcast(intent);
    }

}

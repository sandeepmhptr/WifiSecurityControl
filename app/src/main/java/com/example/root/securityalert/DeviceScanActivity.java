package com.example.root.securityalert;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class DeviceScanActivity extends AppCompatActivity/*ListActivity*/ {
    //private LeDeviceListAdapter mLeDeviceListAdapter;
    public BluetoothAdapter mBluetoothAdapter;
    public  BluetoothManager mBluetoothManager;
               private BluetoothLeService mBluetoothService;
    //private BluetoothSendRecv mSend;
    public Context cntxt;
    public Activity mActvty;
    ArrayList<ViewHolder> arrayOfUsers2 = new ArrayList<ViewHolder>();
    private boolean mScanning;
    private boolean mBounded;
    private Handler mHandler;


    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 50000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    UsersAdapter adapter;
    private Intent visible;
    private String mAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        mHandler = new Handler();

        //mSend=new BluetoothSendRecv(cntxt);
        mActvty= this.getParent();
        visible = this.getIntent();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        mBluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null ) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }     // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.

        if( !mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 1);
        }
        if( !mBluetoothAdapter.isDiscovering()) {
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
        // Construct the data source

        ArrayList<ViewHolder> arrayOfUsers = new ArrayList<ViewHolder>();

       // Create the adapter to convert the array to views

         adapter = new UsersAdapter(this, arrayOfUsers);
         cntxt=this.getApplicationContext();
        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ViewHolder entry= (ViewHolder) parent.getAdapter().getItem(position);
                mAddress = entry.deviceAddress;
                Toast.makeText(cntxt, mAddress, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(cntxt, BluetoothLeService.class);
                cntxt.startService(i);
                bindService(i, mConnection, BIND_AUTO_CREATE);; //if checked, start service
                //final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                //mBluetoothService.mBluetoothDeviceAddress=address;
                //mBluetoothService.mBluetoothManager=mBluetoothManager;
                //mBluetoothService.mBluetoothAdapter = mBluetoothAdapter;
                //mBluetoothService.mBluetoothGatt.connect();
                /*mBluetoothService.mBluetoothGatt = */
                //mSend.mBluetoothGatt=device.connectGatt(mActvty/*cntxt*/, false, mSend.mGattCallback);
                //mSend.mBluetoothDeviceAddress=address;
                //mSend.mBluetoothManager=mBluetoothManager;
                //mSend.mBluetoothAdapter = mBluetoothAdapter;
                //mSend.mBluetoothGatt.connect();
                //mBluetoothService.mBluetoothGatt=mBluetoothGatt;
                //Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            }});

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
        scanLeDevice(true);
            ViewHolder newUser2 = new ViewHolder("adtv2","vvg25");
         adapter.add(newUser2);

    }
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(Client.this, "Service is disconnected", 1000).show();
            mBounded = false;
            mBluetoothService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(Client.this, "Service is connected", 1000).show();
            mBounded = true;
            BluetoothLeService.LocalBinder mLocalBinder = (BluetoothLeService.LocalBinder)service;
            Toast.makeText(cntxt, "Mithun", Toast.LENGTH_SHORT).show();
            mBluetoothService = mLocalBinder.getService();
            if (!mBluetoothService.initialize()) {
                //Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothService.connect(mAddress);
        }
    };


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            ViewHolder newUser2 = new ViewHolder("adtv2","vvg2");
            adapter.add(newUser2);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);


                     if(arrayOfUsers2.isEmpty())
                     {
                         ViewHolder currentX=new ViewHolder("No devices ","found");
                         adapter.add(currentX);
                     }
                     else {
                         Iterator<ViewHolder> it = arrayOfUsers2.iterator();
                         while (it.hasNext()) {
                             ViewHolder currentX = it.next();
                             adapter.add(currentX);
                         }
                     }
                        // Do something with the value

                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        //invalidateOptionsMenu();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, yay! Start the Bluetooth device scan.

                } else {
                    // Alert the user that this application requires the location permission to perform the scan.
                }
            }
            case 123:
            {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, yay! Start the Bluetooth device scan.

                } else {
                    // Alert the user that this application requires the location permission to perform the scan.
                }
            }
        }
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mLeDeviceListAdapter.addDevice(device);
                            //mLeDeviceListAdapter.notifyDataSetChanged();

                            //ViewHolder newUser = new ViewHolder("Nathan", "San Diego");
                            String deviceName=null, deviceAddress=null;
                            if(device!=null)
                               deviceName= device.getName();
                            if (!(deviceName != null && deviceName.length() > 0))
                                deviceName = "unknown device";
                            if(device!=null)
                                deviceAddress= device.getAddress();
                            ViewHolder newUser = new ViewHolder(deviceName, deviceAddress);
                            ViewHolder newUser2 = new ViewHolder("adtv","vvg");
                            if(!arrayOfUsers2.contains(newUser))
                                arrayOfUsers2.add(newUser);
                            //adapter.add(newUser);
                        }
                    });
                }
            };




}


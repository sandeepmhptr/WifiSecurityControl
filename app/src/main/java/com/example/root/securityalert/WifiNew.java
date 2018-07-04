package com.example.root.securityalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;

public class WifiNew extends BroadcastReceiver  implements WifiP2pManager.ChannelListener ,
        WifiP2pManager.ConnectionInfoListener,WifiP2pManager.PeerListListener,
        WifiP2pManager.ActionListener {
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiNewActivity mActivity;
    private WifiP2pManager.PeerListListener myPeerListListener;
    private Context mContext;
    private Intent mIntent;
    private UsersAdapter mAdapter;
    private String mDeviceName;
    private String mAddress;
    public WifiNew(){

    }
    public WifiNew(WifiNewActivity Act, Context context)
    {
        mActivity= Act;
        setupIntentFilter();
        mContext = context;
        mActivity.registerReceiver(this, intentFilter);
        ArrayList<ViewHolder> arrayOfUsers = new ArrayList<ViewHolder>();
        mAdapter = new UsersAdapter(mActivity, arrayOfUsers);

        ListView listView = (ListView) mActivity.findViewById(R.id.mobile_list);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ViewHolder entry= (ViewHolder) parent.getAdapter().getItem(position);
                mAddress = entry.deviceAddress;
                mDeviceName =entry.deviceName;
                Toast.makeText(mContext, mAddress, Toast.LENGTH_SHORT).show();
                connectToPeer (mAddress);
            }});

    }

    public void setupIntentFilter ()
    {
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    public void setupWifiP2p (Context context)
    {
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, context.getMainLooper(), null);
    }

    public void discoverPeers ()
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovfindNeighborsery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });
    }


    public void stopReceiver ()
    {
        mActivity.unregisterReceiver(this);

    }



    @Override
    public void onReceive(Context context, Intent intent) {

        mIntent = intent;
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            Toast.makeText(mContext, "WIFI_P2P_STATE_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            } else {

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed!  We should probably do something about
            // that.

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            Toast.makeText(mContext, "WIFI_P2P_PEERS_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
            if (mManager != null) {
                mManager.requestPeers(mChannel, this);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.
            Toast.makeText(mContext, "WIFI_P2P_CONNECTION_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
            if (mManager == null) {
                return;
            }
            Toast.makeText(mContext, "NetworkInfo", Toast.LENGTH_SHORT).show();
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, this);
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Toast.makeText(mContext, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION", Toast.LENGTH_SHORT).show();

        }

    }



    /*public void setNearbyListener (NearbyListener nearbyListener)
    {
        mNearbyListener = nearbyListener;
    }*/


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

        for ( WifiP2pDevice device: peers.getDeviceList()) {
            mAdapter.add(new ViewHolder(device.deviceAddress,device.deviceName));
            Toast.makeText(mContext, device.deviceAddress, Toast.LENGTH_SHORT).show();
            //connectToPeer (device.deviceAddress);

        }
    }


    private void connectToPeer (String deviceAddress/*WifiP2pDevice device*/)
    {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config,this); /*new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Toast.makeText(mContext, "connectToPeer Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(mContext, "connectToPeer Failure", Toast.LENGTH_SHORT).show();
            }
        });*/

    }



    @Override
    public void onSuccess() {
        Toast.makeText(mContext, "connectToPeer Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(int i) {
        //String reasonString = reason(i);

        Toast.makeText(mContext, "connectToPeer Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {


        InetAddress groupOwnerAddress = info.groupOwnerAddress;
        Toast.makeText(mContext, "onConnectionInfoAvailable", Toast.LENGTH_SHORT).show();
        Toast.makeText(mContext, groupOwnerAddress.getHostAddress(), Toast.LENGTH_SHORT).show();

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.
            Toast.makeText(mContext, "Server::onConnectionInfoAvailable", Toast.LENGTH_SHORT).show();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case,
            // you'll want to create a client thread that connects to the group
            // owner.

            //Neighbor neighbor = new Neighbor(groupOwnerAddress.getHostAddress(),groupOwnerAddress.getHostName(),Neighbor.TYPE_WIFI_P2P);
            //mNearbyListener.foundNeighbor(neighbor);
            Toast.makeText(mContext, "Client::onConnectionInfoAvailable", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onChannelDisconnected() {

    }


}



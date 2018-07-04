package com.example.root.securityalert;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WifiNewActivity extends AppCompatActivity {
    private Context mCntxt;
    private WifiNew Bdcst;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mCntxt = this.getApplicationContext();

        Bdcst = new WifiNew(this,mCntxt);
        Bdcst.setupWifiP2p(mCntxt);
        Bdcst.discoverPeers();
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bdcst.stopReceiver();
        //unregisterReceiver(mReceiver);
    }


}

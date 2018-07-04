package com.example.root.securityalert;

/**
 * Created by root on 8/1/18.
 */
/*
public class settings {
}
*/

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

//import android.widget.AdapterView;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class settings extends ListFragment /*Fragment*/implements OnItemClickListener {
    String[] title = {
            "Register",
            "Set Password",
            "Edit Password",
            "Delete Password",
            "Add location to turn-off place list",
            "Disable turn-off Places",
            "Enable turn-off Places",
            "Edit Distance",
            "Factory Reset"
    };

    List<String> mStrings = new ArrayList<String>();
    List<String> mDevices = new ArrayList<String>();

    String[] strings = new String[mStrings.size()];
    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return inflater.inflate(R.layout.fragment_pager_list, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        for(int i=0;i<title.length;i++) {
            mStrings.add(title[i]);

        }
        strings = mStrings.toArray(strings);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                mStrings));
        getListView().setOnItemClickListener(this);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        //Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        switch(position)
        {
            case 0:
                Intent intent = new Intent(getActivity().getApplicationContext(), WifiNewActivity.class/*DeviceScanActivity.class/*BlueTooth.class*/);
                startActivity(intent);
                /*BluetoothAdapter mBluetoothAdapter;
                BluetoothManager mBluetoothManager;
                mBluetoothManager =(BluetoothManager) this.getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = mBluetoothManager.getAdapter();
                // Checks if Bluetooth is supported on the device.
                Context ctxt=getContext();
                if (mBluetoothAdapter == null ) {
                    Toast.makeText(this.getContext(), R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                    //finish();
                    return;
                }
                startScanning( mBluetoothAdapter,ctxt);*/

                /*mDevices.add("Dev1");
                setListAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        mDevices));*/
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            default:
                break;



        }
    }
}
package com.example.kyc89_000.dronecommander.BluetoothModule;

import android.app.Activity;
import android.bluetooth.*;
import android.content.Intent;

import com.example.kyc89_000.dronecommander.MainActivity;

import java.util.Set;

/**
 * Created by kyc89_000 on 2016-08-02.
 */
public class DeviceFinder {
    private BluetoothAdapter btAdapter;

    private MainActivity mainActivity;

    // Constructor
    public DeviceFinder(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;

        // get bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothDevice GetPairedDeviceByName(String deviceName)
    {
        BluetoothDevice result = null;

        //EnableBluetooth();
        if(btAdapter.isEnabled() == false)
        {
            mainActivity.UpdateLog("Bluetooth is not on.");
            return null;
        }

        result = GetDevice(deviceName);
        if(result == null)
            mainActivity.UpdateLog("There's no device named " + deviceName);

        return result;
    }

    public boolean EnableBluetooth()
    {
        if(btAdapter.isEnabled())
        {
            mainActivity.UpdateLog("Bluetooth adapter is already enabled.");
            return true;
        }
        else {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(i, BluetoothConsts.REQUEST_ENABLE_BT);
            return false;
        }
    }

    private BluetoothDevice GetDevice(String deviceName)
    {
        BluetoothDevice result = null;

        Set<BluetoothDevice> pairedDevice = btAdapter.getBondedDevices();
        if(pairedDevice.size() == 0)
        {
            mainActivity.UpdateLog("There's no device paired");
            return null;
        }

        for(BluetoothDevice device : pairedDevice)
        {
            String tempName = device.getName();
            if(deviceName.equals(tempName)) {
                result = device;
                break;
            }
        }

        return result;
    }
}

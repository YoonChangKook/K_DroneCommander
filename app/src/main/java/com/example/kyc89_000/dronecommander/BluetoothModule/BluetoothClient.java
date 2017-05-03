package com.example.kyc89_000.dronecommander.BluetoothModule;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.kyc89_000.dronecommander.MainActivity;

import java.io.IOException;
import java.io.*;

/**
 * Created by kyc89_000 on 2016-08-02.
 */
public class BluetoothClient{
    private MainActivity mainActivity;

    private BluetoothDevice device;
    private BluetoothSocket socket;

    private InputStream iStream;
    private OutputStream oStream;

    // Constructors
    public BluetoothClient(MainActivity mainActivity, BluetoothDevice device)
    {
        this.mainActivity = mainActivity;
        this.device = device;
        this.socket = null;
        this.iStream = null;
        this.oStream = null;
    }

    public void Connect()
    {
        if(device == null)
        {
            mainActivity.UpdateLog("Can't connect. device is null");
            return;
        }

        // Get Socket
        try{
            socket = device.createRfcommSocketToServiceRecord(BluetoothConsts.MY_UUID);
        } catch(IOException e) {
            mainActivity.UpdateLog("Create socket failed. IOException occured.");
        }

        // Connect
        try{
            socket.connect();
            mainActivity.UpdateLog("Connect Success");
        } catch(IOException e){
            mainActivity.UpdateLog("Connect Fail");
            try{
                socket.close();
            }catch(IOException e2){
                mainActivity.UpdateLog("Close socket failed. IOException occured");
            }
            return;
        }

        // Get Stream
        try{
            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
        } catch(IOException e){
            mainActivity.UpdateLog("Get stream failed. IOException occured");
            return;
        }
    }

    public boolean isConnected()
    {
        if(socket == null)
            return false;
        else
            return socket.isConnected();
    }

    public void WriteBytes(byte[] data)
    {
        try{
            oStream.write(data);
        } catch(IOException e){
            mainActivity.UpdateLog("Write failed. IOException occured");
        }
    }

    public int ReadBytes(byte[] buffer)
    {
        //byte[] tempBuffer = new byte[BluetoothConsts.BLUETOOTH_BUFFER_SIZE];
        int readNum = 0;
        try{
            readNum = iStream.read(buffer);
        } catch(IOException e){
            mainActivity.UpdateLog("Read failed. IOException occured");
            return 0;
        }

        return readNum;
    }
}

package com.example.kyc89_000.dronecommander.DroneCommander;


import com.example.kyc89_000.dronecommander.BluetoothModule.*;
import com.example.kyc89_000.dronecommander.DroneCommander.Packet.*;
import com.example.kyc89_000.dronecommander.MainActivity;

import java.nio.ByteBuffer;

/**
 * Created by kyc89_000 on 2016-08-02.
 */
public class CommandReader extends Thread {
    public static final int BUFFER_SIZE = 1024;

    private MainActivity mainActivity;
    private BluetoothClient bluetoothClient;
    private byte[] buffer;
    private byte[] commandPacketBuffer;
    private int bufferStartIndex;

    public CommandReader(MainActivity mainActivity, BluetoothClient bluetoothClient)
    {
        this.mainActivity = mainActivity;
        this.bluetoothClient = bluetoothClient;
        this.buffer = new byte[BUFFER_SIZE];
        CommandPacket tempPacket = new CommandPacket();
        this.commandPacketBuffer = new byte[tempPacket.size()];
        this.bufferStartIndex = 0;

        if(this.bluetoothClient.isConnected() == false)
            this.bluetoothClient.Connect();
    }

    public void run()
    {
        //byte[] tempBuffer = new byte[BluetoothConsts.BLUETOOTH_BUFFER_SIZE];
        while(true)
        {
            int readNum = bluetoothClient.ReadBytes(buffer);
            byte[] tempBuffer = new byte[readNum];
            System.arraycopy(buffer, 0, tempBuffer, 0, readNum);
            sendPacketToActivity(tempBuffer);

            // echo
            //bluetoothClient.WriteBytes(tempBuffer);
        }
    }

    private void sendPacketToActivity(byte[] buffer)
    {
        int readByte = 0;
        while(readByte < buffer.length)
        {
            if(buffer.length - readByte >= commandPacketBuffer.length - bufferStartIndex)
            {
                System.arraycopy(buffer,
                        readByte,
                        commandPacketBuffer,
                        bufferStartIndex,
                        commandPacketBuffer.length - bufferStartIndex);
                readByte += commandPacketBuffer.length - bufferStartIndex;
                bufferStartIndex = 0;

                // Make Command Packet
                CommandPacket packet = new CommandPacket();
                packet.setByteBuffer(ByteBuffer.wrap(commandPacketBuffer), 0);
                mainActivity.CommandReceive(packet);
                commandPacketBuffer = new byte[packet.size()];
            }
            else
            {
                System.arraycopy(buffer,
                        readByte,
                        commandPacketBuffer,
                        bufferStartIndex,
                        buffer.length - readByte);
                bufferStartIndex += buffer.length - readByte;
                readByte += buffer.length - readByte;
            }
        }
    }
}

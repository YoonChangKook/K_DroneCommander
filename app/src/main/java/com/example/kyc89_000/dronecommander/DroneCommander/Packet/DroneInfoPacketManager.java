package com.example.kyc89_000.dronecommander.DroneCommander.Packet;

import java.nio.ByteBuffer;

/**
 * Created by kyc89_000 on 2016-08-08.
 */
public class DroneInfoPacketManager {
    public static DroneInfoPacket DroneInfoPack(byte flyingState, byte battery)
    {
        DroneInfoPacket packet = new DroneInfoPacket();
        packet.flyingState.set(flyingState);
        packet.battery.set(battery);

        return packet;
    }

    public static byte[] SerializeDroneInfo(DroneInfoPacket packet)
    {
        byte[] result = null;
        ByteBuffer byteBuffer = packet.getByteBuffer();
        int packetSize = packet.size();
        if(byteBuffer.hasArray())
        {
            result = new byte[packetSize];
            int offset = byteBuffer.arrayOffset() + packet.getByteBufferPosition();
            System.arraycopy(byteBuffer.array(), offset, result, 0, packetSize);
        }
        //result = byteBuffer.array();
        return result;
    }

    public static DroneInfoPacket DeserializeDroneInfo(byte[] packetBytes)
    {
        DroneInfoPacket packet = new DroneInfoPacket();
        packet.setByteBuffer(ByteBuffer.wrap(packetBytes), 0);
        return packet;
    }
}

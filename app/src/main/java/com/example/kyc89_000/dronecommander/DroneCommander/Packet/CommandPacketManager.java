package com.example.kyc89_000.dronecommander.DroneCommander.Packet;

import java.nio.ByteBuffer;

/**
 * Created by kyc89_000 on 2016-08-06.
 */
public abstract class CommandPacketManager {
    public static CommandPacket CommandPack(byte commandType, byte flyingMode)
    {
        byte zeroByte = 0;
        return CommandPack(commandType, flyingMode, zeroByte, zeroByte, zeroByte, zeroByte, zeroByte);
    }

    public static CommandPacket CommandPack(byte commandType, byte flyingMode,
                                            byte roll, byte pitch, byte yaw, byte gaz,
                                            short sustainTime)
    {
        CommandPacket packet = new CommandPacket();
        packet.commandType.set(commandType);
        packet.flyingMode.set(flyingMode);
        packet.roll.set(roll);
        packet.pitch.set(pitch);
        packet.yaw.set(yaw);
        packet.gaz.set(gaz);
        packet.sustainTime.set(sustainTime);
        return packet;
    }

    public static byte[] SerializeCommand(CommandPacket packet)
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
        return result;
    }

    public static CommandPacket DeserializeCommand(byte[] packetBytes)
    {
        CommandPacket packet = new CommandPacket();
        packet.setByteBuffer(ByteBuffer.wrap(packetBytes), 0);
        return packet;
    }
}
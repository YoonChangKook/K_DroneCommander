package com.example.kyc89_000.dronecommander.DroneCommander.Packet;

import javolution.io.Struct;

/**
 * Created by kyc89_000 on 2016-08-05.
 */

public class CommandPacket extends Struct {
    public final Unsigned8 commandType = new Unsigned8();
    public final Unsigned8 flyingMode = new Unsigned8();
    public final Signed8 roll = new Signed8();
    public final Signed8 pitch = new Signed8();
    public final Signed8 yaw = new Signed8();
    public final Signed8 gaz = new Signed8();
    public final Signed16 sustainTime = new Signed16();
}

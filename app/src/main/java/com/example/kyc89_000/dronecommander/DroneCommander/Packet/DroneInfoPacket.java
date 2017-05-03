package com.example.kyc89_000.dronecommander.DroneCommander.Packet;

import javolution.io.Struct;

/**
 * Created by kyc89_000 on 2016-08-08.
 */
public class DroneInfoPacket extends Struct{
    public final Unsigned8 flyingState = new Unsigned8();
    public final Signed8 battery = new Signed8();
}

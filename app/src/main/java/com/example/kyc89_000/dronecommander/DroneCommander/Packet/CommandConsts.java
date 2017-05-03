package com.example.kyc89_000.dronecommander.DroneCommander.Packet;

/**
 * Created by kyc89_000 on 2016-08-07.
 */
public class CommandConsts {
    public abstract class DroneCommandType{
        public static final byte DRONE_FLYING = 0;
        public static final byte DRONE_PILOTING = 1;
        public static final byte ACK = 100;
    }

    public abstract class DroneFlyingMode{
        public static final byte TAKE_OFF = 0;
        public static final byte LAND = 1;
        public static final byte NONE = 100;
    }
}

package com.example.kyc89_000.dronecommander.DroneCommander.Packet;

/**
 * Created by kyc89_000 on 2016-08-08.
 */
public class DroneInfoConsts {
    public abstract class DroneFlyingState{
        public static final byte LANDED = 0;
        public static final byte TAKING_OFF = 1;
        public static final byte HOVERING = 2;
        public static final byte FLYING = 3;
        public static final byte LANDING = 4;
        public static final byte EMERGENCY = 5;
        public static final byte USER_TAKING_OFF = 6;
        public static final byte ACK = 100;
        public static final byte NONE = 101;
    }
}

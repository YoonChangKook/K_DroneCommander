package com.example.kyc89_000.dronecommander.BluetoothModule;

import java.util.UUID;

/**
 * Created by kyc89_000 on 2016-08-02.
 */
public abstract class BluetoothConsts {
    public static final int REQUEST_ENABLE_BT = 1;
    public static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int BLUETOOTH_BUFFER_SIZE = 1024;
}

package com.example.kyc89_000.dronecommander.DroneCommander;

import com.example.kyc89_000.dronecommander.MainActivity;
import com.example.kyc89_000.dronecommander.DroneCommander.Packet.*;
import com.parrot.arsdk.arcontroller.*;
import com.parrot.arsdk.ardiscovery.*;
import com.parrot.arsdk.arcommands.*;

/**
 * Created by kyc89_000 on 2016-08-03.
 */
public class DroneListener implements ARDeviceControllerListener {
    private MainActivity mainActivity;

    public DroneListener(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onStateChanged(ARDeviceController deviceController,
                               ARCONTROLLER_DEVICE_STATE_ENUM newState,
                               ARCONTROLLER_ERROR_ENUM error)
    {
        switch (newState)
        {
            case ARCONTROLLER_DEVICE_STATE_RUNNING:
                break;
            case ARCONTROLLER_DEVICE_STATE_STOPPED:
                break;
            case ARCONTROLLER_DEVICE_STATE_STARTING:
                break;
            case ARCONTROLLER_DEVICE_STATE_STOPPING:
                break;

            default:
                break;
        }
    }

    @Override
    public void onExtensionStateChanged (ARDeviceController deviceController,
                                         ARCONTROLLER_DEVICE_STATE_ENUM newState,
                                         ARDISCOVERY_PRODUCT_ENUM product,
                                         String name,
                                         ARCONTROLLER_ERROR_ENUM error)
    {
        // NOPE
    }

    @Override
    public void onCommandReceived(ARDeviceController deviceController,
                                  ARCONTROLLER_DICTIONARY_KEY_ENUM commandKey,
                                  ARControllerDictionary elementDictionary)
    {
        if (elementDictionary != null)
        {
            // if the command received is a battery state changed
            if (commandKey == ARCONTROLLER_DICTIONARY_KEY_ENUM.ARCONTROLLER_DICTIONARY_KEY_COMMON_COMMONSTATE_BATTERYSTATECHANGED)
            {
                ARControllerArgumentDictionary<Object> args = elementDictionary.get(ARControllerDictionary.ARCONTROLLER_DICTIONARY_SINGLE_KEY);

                if (args != null)
                {
                    Integer batValue = (Integer) args.get(ARFeatureCommon.ARCONTROLLER_DICTIONARY_KEY_COMMON_COMMONSTATE_BATTERYSTATECHANGED_PERCENT);
                    // do what you want with the battery level
                    mainActivity.UpdateLog("Battery: " + batValue + "%");
                    DroneInfoPacket infoPacket = new DroneInfoPacket();
                    infoPacket.flyingState.set(DroneInfoConsts.DroneFlyingState.NONE);
                    infoPacket.battery.set(batValue.byteValue());
                    mainActivity.DroneInfoSend(infoPacket);
                }
            }
            if (commandKey == ARCONTROLLER_DICTIONARY_KEY_ENUM.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED)
            {
                ARControllerArgumentDictionary<Object> args = elementDictionary.get(ARControllerDictionary.ARCONTROLLER_DICTIONARY_SINGLE_KEY);
                if (args != null)
                {
                    Integer flyingStateInt = (Integer) args.get(ARFeatureARDrone3.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE);
                    ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM flyingState = ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM.getFromValue(flyingStateInt);
                    DroneInfoPacket infoPacket = new DroneInfoPacket();
                    infoPacket.flyingState.set((byte)flyingState.getValue());
                    infoPacket.battery.set((byte)-1);
                    mainActivity.DroneInfoSend(infoPacket);
                }
            }
            //if(commandKey)
        }
    }
}

package com.example.kyc89_000.dronecommander.DroneCommander;

import android.util.Log;

import com.example.kyc89_000.dronecommander.DroneCommander.Packet.*;
import com.example.kyc89_000.dronecommander.MainActivity;
import com.parrot.arsdk.arcommands.*;
import com.parrot.arsdk.arcontroller.*;
import com.parrot.arsdk.ardiscovery.*;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kyc89_000 on 2016-08-03.
 */
public class BebopDrone {
    private class PCMD_Thread extends Thread{
        private static final double AIR_BREAK_DIVIDE = 2.0;
        //private static final short breakTime = 100;

        private byte roll;
        private byte pitch;
        private byte yaw;
        private byte gaz;
        private short sustainTime;

        public PCMD_Thread(byte roll, byte pitch, byte yaw, byte gaz, short sustainTime) {
            this.roll = roll;
            this.pitch = pitch;
            this.yaw = yaw;
            this.gaz = gaz;
            this.sustainTime = sustainTime;
        }

        public void run()
        {
            // move
            ARCONTROLLER_ERROR_ENUM error = deviceController.getFeatureARDrone3().setPilotingPCMD(
                    (byte)1, roll, pitch, yaw, gaz, 0);
            if (!error.equals(ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK))
            {
                mainActivity.UpdateLog("Error while sending pcmd: " + error);
                return;
            }
            try{
                Thread.sleep(sustainTime);
            } catch(InterruptedException ex){
                return;
            }

            // break
            byte zeroByte = (byte)0;
            deviceController.getFeatureARDrone3().setPilotingPCMD((byte)1, (byte)-roll, (byte)-pitch, zeroByte, zeroByte, 0);
            try{
                long breakTime = (long)(sustainTime / AIR_BREAK_DIVIDE);
                Thread.sleep(breakTime);
                mainActivity.UpdateLog("BreakTime: " + breakTime);
            } catch(InterruptedException ex){
                return;
            }
            deviceController.getFeatureARDrone3().setPilotingPCMD(zeroByte, zeroByte, zeroByte, zeroByte, zeroByte, 0);
        }
    }
    private ARDiscoveryDevice device;
    private ARDeviceController deviceController;
    private DroneListener droneListener;
    private boolean isStarted;
    private PCMD_Thread pcmd_thread;

    private MainActivity mainActivity;

    // Constructors
    public BebopDrone(MainActivity mainActivity, String name, String ip, int port)
    {
        this.mainActivity = mainActivity;
        this.device = null;
        this.deviceController = null;
        this.droneListener = null;
        this.isStarted = false;
        this.pcmd_thread = null;

        // Get Device
        try {
            device = new ARDiscoveryDevice();
            //ARDis
            ARDISCOVERY_ERROR_ENUM error =
                    device.initWifi(ARDISCOVERY_PRODUCT_ENUM.ARDISCOVERY_PRODUCT_BEBOP_2, name, ip, port);
            if(error != ARDISCOVERY_ERROR_ENUM.ARDISCOVERY_OK)
                mainActivity.UpdateLog("Error while initiating wifi");
        } catch(ARDiscoveryException e){
            mainActivity.UpdateLog("Can't create drone device");
            return;
        }

        // Get Device Controller
        try{
            deviceController = new ARDeviceController(device);
        } catch(ARControllerException e){
            mainActivity.UpdateLog("Can't create drone device controller");
            return;
        }

        // Set Drone Listener
        droneListener = new DroneListener(this.mainActivity);
        deviceController.addListener(droneListener);
        deviceController.start();
        mainActivity.UpdateLog("---Drone Start---");
        isStarted = true;
    }

    // Methods
    public void CleanUp()
    {
        this.device.dispose();
        this.device = null;
        this.deviceController.stop();
        this.deviceController.dispose();
        this.deviceController = null;
        this.droneListener = null;
        this.isStarted = false;
    }

    public boolean IsStarted()
    {
        return isStarted;
    }

    public boolean isDroneConnected() {
        ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM state = getPilotingState();
        if(state == ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM.eARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_UNKNOWN_ENUM_VALUE)
            return false;
        else
            return true;
    }

    private ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM getPilotingState() {
        ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM flyingState =
                ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM.eARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_UNKNOWN_ENUM_VALUE;
        if(deviceController != null)
        {
            try{
                ARControllerDictionary dict = deviceController.getCommandElements(
                        ARCONTROLLER_DICTIONARY_KEY_ENUM.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED);
                if(dict != null)
                {
                    ARControllerArgumentDictionary<Object> args =  dict.get(ARControllerDictionary.ARCONTROLLER_DICTIONARY_SINGLE_KEY);
                    if(args != null) {
                        Integer flyingStateInt = (Integer)args.get(ARFeatureARDrone3.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE);
                        flyingState = ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM.getFromValue(flyingStateInt);
                    }
                }
            } catch(ARControllerException e){
                mainActivity.UpdateLog("Can't get command elements");
            }
        }

        return flyingState;
    }
    public boolean takeOff()
    {
        if (ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED.equals(getPilotingState()))
        {
            ARCONTROLLER_ERROR_ENUM error = deviceController.getFeatureARDrone3().sendPilotingTakeOff();

            if (!error.equals(ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK))
            {
                mainActivity.UpdateLog("Error while sending take off: " + error);
                return false;
            }
            else
                return true;
        }
        else
            return false;
    }
    public boolean land()
    {
        ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM flyingState = getPilotingState();
        if (ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_TAKINGOFF.equals(flyingState) ||
                ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING.equals(flyingState) ||
                ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING.equals(flyingState))
        {
            pcmdStop();
            ARCONTROLLER_ERROR_ENUM error = deviceController.getFeatureARDrone3().sendPilotingLanding();

            if (!error.equals(ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK))
            {
                mainActivity.UpdateLog("Error while sending land: " + error);
                return false;
            }
            else
                return true;
        }
        else
            return false;
    }
    public boolean pcmdMove(final CommandPacket packet)
    {
        ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM flyingState = getPilotingState();
        if(flyingState.getValue() == DroneInfoConsts.DroneFlyingState.HOVERING ||
                flyingState.getValue() == DroneInfoConsts.DroneFlyingState.FLYING)
        {
            if(pcmd_thread != null)
                pcmd_thread.interrupt();
            pcmd_thread = new PCMD_Thread(packet.roll.get(),
                    packet.pitch.get(),
                    packet.yaw.get(),
                    packet.gaz.get(),
                    packet.sustainTime.get());
            pcmd_thread.start();

            return true;
        }
        else
            return false;
    }
    public void pcmdStop()
    {
        if(pcmd_thread != null)
            pcmd_thread.interrupt();
    }
}

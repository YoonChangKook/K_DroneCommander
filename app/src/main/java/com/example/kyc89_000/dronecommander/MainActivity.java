package com.example.kyc89_000.dronecommander;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kyc89_000.dronecommander.BluetoothModule.*;
import com.example.kyc89_000.dronecommander.DroneCommander.*;
import com.example.kyc89_000.dronecommander.DroneCommander.Packet.CommandConsts;
import com.example.kyc89_000.dronecommander.DroneCommander.Packet.CommandPacket;
import com.example.kyc89_000.dronecommander.DroneCommander.Packet.DroneInfoConsts;
import com.example.kyc89_000.dronecommander.DroneCommander.Packet.DroneInfoPacket;
import com.example.kyc89_000.dronecommander.DroneCommander.Packet.DroneInfoPacketManager;
import com.parrot.arsdk.ARSDK;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceNetService;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // GUI members
    private Button connectBtn;
    private Button droneConnectBtn;
    private Button droneFindBtn;
    private EditText deviceNameTextBox;
    private EditText droneNameTextBox;
    private EditText droneIpTextBox;
    private EditText dronePortTextBox;
    private EditText logBox;
    // Bluetooth Members
    private DeviceFinder deviceFinder;
    private BluetoothClient bluetoothClient;
    private CommandReader commandReader;
    // BebopDrone Members
    private BebopDrone bebopDrone;
    private boolean isSearching;
    private ArrayList<ARDiscoveryDeviceService> droneList;
    private DroneDiscoverer droneDiscoverer;

    private final DroneDiscoverer.Listener mDiscovererListener = new  DroneDiscoverer.Listener() {

        @Override
        public void onDronesListUpdated(List<ARDiscoveryDeviceService> dronesList) {
            droneList.clear();
            droneList.addAll(dronesList);

            for(ARDiscoveryDeviceService drone : dronesList)
            {
                ARDiscoveryDeviceNetService netDevice = (ARDiscoveryDeviceNetService)drone.getDevice();
                UpdateLog(netDevice.getName() + ", " + netDevice.getIp() + ", " + netDevice.getPort());

                CreateDrone(netDevice.getName(),
                        netDevice.getIp(),
                        netDevice.getPort());

                // clean the drone discoverer object
                droneDiscoverer.stopDiscovering();
                droneDiscoverer.cleanup();
                droneDiscoverer.removeListener(mDiscovererListener);
                UpdateLog("---Finish Finding---");
                isSearching = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARSDK.loadSDKLibs();

        // GUI members
        connectBtn = (Button)findViewById(R.id.connectBtn);
        droneConnectBtn = (Button)findViewById(R.id.droneConnectBtn);
        droneFindBtn = (Button)findViewById(R.id.droneFindBtn);
        deviceNameTextBox = (EditText)findViewById(R.id.deviceNameTextBox);
        droneNameTextBox = (EditText)findViewById(R.id.droneNameTextBox);
        droneIpTextBox = (EditText)findViewById(R.id.droneIpTextBox);
        dronePortTextBox = (EditText)findViewById(R.id.dronePortTextBox);
        logBox = (EditText)findViewById(R.id.logBox);
        ControlInit();
        // Bluetooth Members
        deviceFinder = new DeviceFinder(this);
        bluetoothClient = null;
        commandReader = null;
        // BebopDrone Members
        bebopDrone = null;
        isSearching = false;
        droneList = new ArrayList<ARDiscoveryDeviceService>();
        droneDiscoverer = new DroneDiscoverer(this);
    }

    public void UpdateLog(final String log)
    {
        logBox.post(new Runnable() {
            @Override
            public void run() {
                logBox.append(log + '\n');
            }
        });
        //logBox.append(log + '\n');
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothConsts.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    CreateCommander(deviceNameTextBox.getText().toString());
                } else {
                    // 취소 눌렀을 때
                    UpdateLog("Bluetooth is not enabled");
                }
                break;
        }
    }

    private void CreateCommander(String deviceName)
    {
        // Bluetooth Connect
        BluetoothDevice device = deviceFinder.GetPairedDeviceByName(deviceName);
        bluetoothClient = new BluetoothClient(this, device);
        commandReader = new CommandReader(this, bluetoothClient);
        if(bluetoothClient.isConnected())
            commandReader.start();
    }
    private void CreateDrone(String deviceName, String ip, int port)
    {
        // Drone Connect
        if(commandReader != null && commandReader.isAlive())
        {
            bebopDrone = new BebopDrone(this, deviceName, ip, port);
        }
        else
            UpdateLog("You have to connect to server before connecting to drone.");
    }
    private void FindDrone()
    {
        if(isSearching == false)
        {
            // setup the drone discoverer and register as listener
            droneDiscoverer.setup();
            droneDiscoverer.addListener(mDiscovererListener);

            // start discovering
            droneDiscoverer.startDiscovering();
            UpdateLog("---Find Drone---");
            isSearching = true;
        }
        else
        {
            // clean the drone discoverer object
            droneDiscoverer.stopDiscovering();
            droneDiscoverer.cleanup();
            droneDiscoverer.removeListener(mDiscovererListener);
            UpdateLog("---Finish Finding---");
            isSearching = false;
        }
    }

    // GUI
    private void ControlInit()
    {
        connectBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                if(deviceFinder.EnableBluetooth())
                    CreateCommander(deviceNameTextBox.getText().toString());
                //UpdateLog("Button Click");
            }
        });

        droneConnectBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                CreateDrone(droneNameTextBox.getText().toString(),
                        droneIpTextBox.getText().toString(),
                        Integer.parseInt(dronePortTextBox.getText().toString()));
            }
        });

        droneFindBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                FindDrone();
            }
        });
    }

    // Methods
    public void CommandReceive(CommandPacket packet)
    {
        if(bebopDrone == null) {
            UpdateLog("Command receive. but drone is not connected.");
            return;
        }

        if(packet.commandType.get() == CommandConsts.DroneCommandType.DRONE_FLYING) {
            if (packet.flyingMode.get() == CommandConsts.DroneFlyingMode.TAKE_OFF)// UpdateLog("TAKE OFF")
            {
                if (bebopDrone.takeOff() == true)
                    SendACK();
            }
            else if (packet.flyingMode.get() == CommandConsts.DroneFlyingMode.LAND)// UpdateLog("LAND");
            {
                if (bebopDrone.land() == true)
                    SendACK();
            }
        }
        else if(packet.commandType.get() == CommandConsts.DroneCommandType.DRONE_PILOTING) {
            if (bebopDrone.pcmdMove(packet) == true)
                SendACK();
        }
        else if(packet.commandType.get() == CommandConsts.DroneCommandType.ACK) {
            if (bebopDrone.isDroneConnected() == true)
                SendACK();
        }
    }
    private void SendACK() {
        DroneInfoPacket infoPacket = new DroneInfoPacket();
        infoPacket.flyingState.set(DroneInfoConsts.DroneFlyingState.ACK);
        infoPacket.battery.set((byte)-1);
        DroneInfoSend(infoPacket);
    }

    public void DroneInfoSend(DroneInfoPacket packet)
    {
        byte[] packetBytes = DroneInfoPacketManager.SerializeDroneInfo(packet);
        bluetoothClient.WriteBytes(packetBytes);
    }
}

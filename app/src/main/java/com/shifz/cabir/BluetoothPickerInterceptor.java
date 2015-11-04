package com.shifz.cabir;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Map;

public class BluetoothPickerInterceptor extends Activity {

    private static final String X = BluetoothPickerInterceptor.class.getSimpleName();
    private String mLaunchPackage;
    private String mLaunchClass;

    public static final String EXTRA_LAUNCH_PACKAGE = "android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE";
    public static final String EXTRA_LAUNCH_CLASS = "android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS";

    public static final String ACTION_DEVICE_SELECTED = "android.bluetooth.devicepicker.action.DEVICE_SELECTED";

    /**
     * Ask device picker to show all kinds of BT devices
     */
    public static final int FILTER_TYPE_ALL = 0;
    /**
     * Ask device picker to show BT devices that support AUDIO profiles
     */
    public static final int FILTER_TYPE_AUDIO = 1;
    /**
     * Ask device picker to show BT devices that support Object Transfer
     */
    public static final int FILTER_TYPE_TRANSFER = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Cabir.deviceList.size() == 0) {
            Log.e("PRINT", "Failed to get selected bluetooth device!");
            finish();
            return;
        }

        Intent intent = getIntent();
        mLaunchPackage = intent.getStringExtra(EXTRA_LAUNCH_PACKAGE);
        mLaunchClass = intent.getStringExtra(EXTRA_LAUNCH_CLASS);
        for (final Map.Entry<String, BluetoothDevice> selectedDevice : Cabir.deviceList.entrySet()) {
            Log.d(X, "Pushing to " + selectedDevice.getValue().getName());
            sendDevicePickedIntent(selectedDevice.getValue());
        }
        finish();
    }

    private void sendDevicePickedIntent(BluetoothDevice device) {
        Intent intent = new Intent(ACTION_DEVICE_SELECTED);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);

        if (mLaunchPackage != null && mLaunchClass != null) {
            intent.setClassName(mLaunchPackage, mLaunchClass);
        }

        sendBroadcast(intent);
    }
}

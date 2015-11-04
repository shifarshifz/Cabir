package com.shifz.cabir.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shifz.cabir.BluetoothStarterActivity;
import com.shifz.cabir.Cabir;

/**
 * Created by Shifar Shifz on 11/3/2015 4:31 PM.
 */
public class OnBluetooth extends BroadcastReceiver {

    private static final String X = OnBluetooth.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(X, "Bluetooth settings changed");

        final String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

            final int currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            switch (currentState) {
                case BluetoothAdapter.STATE_ON:
                    Log.d(X, "State confirmed as CONNECTED");
                    context.startService(new Intent(context, Cabir.class));
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d(X, "State confirmed as CONNECTING");
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Log.d(X, "State confirmed as DISCONNECTED");
                    final Intent turnOnBluetooth = new Intent(context, BluetoothStarterActivity.class);
                    turnOnBluetooth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(turnOnBluetooth);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(X, "State confirmed as DISCONNECTING");
                    break;
                default:
                    Log.d(X, "Unhandled action " + currentState);
            }


        }
    }


}

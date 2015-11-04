package com.shifz.cabir;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.shifz.cabir.Cabir;

/**
 * Created by Shifar Shifz on 11/3/2015.
 */
public class BluetoothStarterActivity extends Activity {

    private static final int RQ_CODE_ON_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBluetoothSwitch();
    }

    private void showBluetoothSwitch() {
        final Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        turnOnBluetooth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(turnOnBluetooth, RQ_CODE_ON_BLUETOOTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RQ_CODE_ON_BLUETOOTH) {
            if (resultCode != RESULT_OK) {
                showBluetoothSwitch();
            } else {
                startService(new Intent(this, Cabir.class));
            }
        }
    }
}

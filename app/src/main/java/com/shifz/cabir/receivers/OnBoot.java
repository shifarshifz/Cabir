package com.shifz.cabir.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shifz.cabir.Cabir;

/**
 * Created by Shifar Shifz on 11/3/2015.
 */
public class OnBoot extends BroadcastReceiver {
    private static final String X = OnBoot.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(X, "Boot finished");
        context.startService(new Intent(context, Cabir.class));
    }
}

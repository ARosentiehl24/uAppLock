package com.arrg.app.uapplock.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arrg.app.uapplock.interfaces.UAppLockServiceView;

public class ScreenReceiver extends BroadcastReceiver {

    private UAppLockServiceView uAppLockServiceView;

    public ScreenReceiver(UAppLockServiceView uAppLockServiceView) {
        this.uAppLockServiceView = uAppLockServiceView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("Screen", "ON");

            uAppLockServiceView.startMonitor();
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("Screen", "OFF");

            uAppLockServiceView.stopMonitor();
        }
    }
}

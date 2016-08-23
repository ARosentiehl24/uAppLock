package com.arrg.app.uapplock.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.model.service.UAppLockService;

public class RestartServiceReceiver extends BroadcastReceiver {

    public RestartServiceReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("RestartServiceReceiver", "Restarting");

        if (intent.getAction().equals(UAppLock.ACTION_RESTART_SERVICE)){
            if (!UAppLockService.isRunning(context, UAppLockService.class)) {

                context.startService(new Intent(context, UAppLockService.class));
            }
        }
    }
}

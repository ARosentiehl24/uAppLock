package com.arrg.app.uapplock.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.model.service.UAppLockService;

public class NotificationReceiver extends BroadcastReceiver {

    private UAppLockService uAppLockService;

    public NotificationReceiver(UAppLockService uAppLockService) {
        this.uAppLockService = uAppLockService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(UAppLock.ACTION_SHOW_NOTIFICATION)) {
            uAppLockService.startForeground();
        }

        if (intent.getAction().equals(UAppLock.ACTION_HIDE_NOTIFICATION)) {
            uAppLockService.stopForeground(true);
        }
    }
}

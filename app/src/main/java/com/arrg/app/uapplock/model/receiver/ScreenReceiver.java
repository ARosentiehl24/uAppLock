package com.arrg.app.uapplock.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.model.service.UAppLockService;
import com.shawnlin.preferencesmanager.PreferencesManager;

public class ScreenReceiver extends BroadcastReceiver {

    private UAppLockService uAppLockService;

    public ScreenReceiver(UAppLockService uAppLockService) {
        this.uAppLockService = uAppLockService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("Screen", "ON");
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Boolean lockAppsAfterScreenOff = PreferencesManager.getBoolean(context.getString(R.string.block_apps_after_screen_off));

            if (lockAppsAfterScreenOff) {
                uAppLockService.lockAllApps();
            }

            Log.d("Screen", "OFF");
        }
    }
}

package com.arrg.app.uapplock.model.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.SyncStateContract;

import com.arrg.app.uapplock.UAppLock;

public class IconOnAppDrawerReceiver extends BroadcastReceiver {

    public IconOnAppDrawerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName componentName = new ComponentName(context.getApplicationContext(), UAppLock.ALIAS_CLASSNAME);

        if (intent.getAction().equals(UAppLock.ACTION_HIDE_APPLICATION)) {
            int setting = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

            int current = context.getPackageManager().getComponentEnabledSetting(componentName);

            if (current != setting) {
                context.getPackageManager().setComponentEnabledSetting(componentName, setting, PackageManager.DONT_KILL_APP);
            }
        }

        if (intent.getAction().equals(UAppLock.ACTION_SHOW_APPLICATION)) {
            int setting = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

            int current = context.getPackageManager().getComponentEnabledSetting(componentName);

            if (current != setting) {
                context.getPackageManager().setComponentEnabledSetting(componentName, setting, PackageManager.DONT_KILL_APP);
            }
        }
    }
}

package com.arrg.app.uapplock.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.view.activity.ApplicationListActivity;
import com.arrg.app.uapplock.view.activity.SplashScreenActivity;
import com.shawnlin.preferencesmanager.PreferencesManager;

public class OutgoingCallReceiver extends BroadcastReceiver {
    public OutgoingCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean showIconOnAppDrawer = PreferencesManager.getBoolean(context.getString(R.string.icon_on_app_drawer), true);
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        if (!showIconOnAppDrawer && number.equals("*#12345#*")) {
            setResultData(null);

            Intent startHomeScreen = new Intent(Intent.ACTION_MAIN);
            startHomeScreen.addCategory(Intent.CATEGORY_HOME);
            startHomeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startHomeScreen);

            Intent launchSplashScreen = new Intent(context, ApplicationListActivity.class);
            launchSplashScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchSplashScreen);
        }
    }
}

package com.arrg.app.uapplock.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.util.kisstools.utils.LogUtil;

public class BootDeviceReceiver extends BroadcastReceiver {
    public BootDeviceReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("BootDeviceReceiver", intent.getAction());

        context.sendBroadcast(new Intent(UAppLock.ACTION_RESTART_SERVICE));
    }
}

package com.arrg.app.uapplock.model.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;

import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.LockScreenServiceView;

public class LockScreenService extends Service implements View.OnKeyListener, LockScreenServiceView {

    public LockScreenService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    public static Intent lockPackage(Context context, String packageName) {
        Intent lockIntent = new Intent(context, LockScreenService.class);
        lockIntent.putExtra(UAppLock.EXTRA_PACKAGE_NAME, packageName);
        lockIntent.setAction(UAppLock.ACTION_COMPARE);

        return lockIntent;
    }
}

package com.arrg.app.uapplock.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LockScreenService extends Service {
    public LockScreenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

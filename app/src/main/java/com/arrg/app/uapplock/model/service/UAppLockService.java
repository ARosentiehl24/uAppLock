package com.arrg.app.uapplock.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UAppLockService extends Service {
    public UAppLockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

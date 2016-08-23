package com.arrg.app.uapplock.model.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.arrg.app.uapplock.interfaces.UAppLockServiceView;
import com.arrg.app.uapplock.presenter.IUAppLockServicePresenter;

public class UAppLockService extends Service implements UAppLockServiceView {

    public static UAppLockService SERVICE;

    private IUAppLockServicePresenter iuAppLockServicePresenter;

    public UAppLockService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(getClass().getCanonicalName(), "onCreate()");

        SERVICE = this;

        iuAppLockServicePresenter = new IUAppLockServicePresenter(this);
        iuAppLockServicePresenter.onCreate(SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getClass().getName(), "onStartCommand()");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onLowMemory() {
        Log.i(getClass().getName(), "OnLowMemory()");

        super.onLowMemory();

        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(getClass().getName(), "onTaskRemoved()");

        super.onTaskRemoved(rootIntent);

        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    @Override
    public void onDestroy() {
        Log.i(getClass().getName(), "onDestroy()");

        super.onDestroy();

        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    public static boolean isRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

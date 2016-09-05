package com.arrg.app.uapplock.presenter;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.UAppLockServicePresenter;
import com.arrg.app.uapplock.interfaces.UAppLockServiceView;
import com.arrg.app.uapplock.model.receiver.IconOnAppDrawerReceiver;
import com.arrg.app.uapplock.model.receiver.NotificationReceiver;
import com.arrg.app.uapplock.model.receiver.ScreenReceiver;
import com.arrg.app.uapplock.model.service.UAppLockService;

public class IUAppLockServicePresenter implements UAppLockServicePresenter {

    private IconOnAppDrawerReceiver iconOnAppDrawerReceiver;
    private NotificationReceiver notificationReceiver;
    private ScreenReceiver screenReceiver;
    private UAppLockService uAppLockService;
    private UAppLockServiceView uAppLockServiceView;

    public IUAppLockServicePresenter(UAppLockServiceView uAppLockServiceView) {
        this.uAppLockServiceView = uAppLockServiceView;
    }

    @Override
    public void onCreate(UAppLockService uAppLockService) {
        this.uAppLockService = uAppLockService;

        uAppLockServiceView.init();

        registerIconOnAppDrawerReceiver();
        registerNotificationReceiver();
        registerScreenReceiver();
    }

    @Override
    public void registerIconOnAppDrawerReceiver() {
        iconOnAppDrawerReceiver = new IconOnAppDrawerReceiver();
        IntentFilter applicationFilter = new IntentFilter();

        applicationFilter.addAction(UAppLock.ACTION_HIDE_APPLICATION);
        applicationFilter.addAction(UAppLock.ACTION_SHOW_APPLICATION);

        try {
            uAppLockService.registerReceiver(iconOnAppDrawerReceiver, applicationFilter);

            Log.e("registerIconReceiver", "Done");
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage() + " - Unregister Receiver");

            uAppLockService.unregisterReceiver(iconOnAppDrawerReceiver);
        }
    }

    @Override
    public void registerNotificationReceiver() {
        notificationReceiver = new NotificationReceiver(uAppLockService);
        IntentFilter notificationFilter = new IntentFilter();

        notificationFilter.addAction(UAppLock.ACTION_SHOW_NOTIFICATION);
        notificationFilter.addAction(UAppLock.ACTION_HIDE_NOTIFICATION);

        try {
            uAppLockService.registerReceiver(notificationReceiver, notificationFilter);
            uAppLockServiceView.notificationHandler();

            Log.e("registerNotReceiver", "Done");
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage() + " -  Unregister Receiver");

            uAppLockService.unregisterReceiver(iconOnAppDrawerReceiver);
        }
    }

    @Override
    public void registerScreenReceiver() {
        screenReceiver = new ScreenReceiver(uAppLockService);
        IntentFilter screenFilter = new IntentFilter();

        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);

        try {
            uAppLockService.registerReceiver(screenReceiver, screenFilter);

            Log.e("registerScreenReceiver", "Done");
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage() + " -  Unregister Receiver");

            uAppLockService.unregisterReceiver(screenReceiver);
        }
    }

    @Override
    public void restartServiceIfNeeded() {
        uAppLockService.sendBroadcast(new Intent(UAppLock.ACTION_RESTART_SERVICE));
    }

    @Override
    public void unregisterReceivers() {
        try {
            uAppLockService.unregisterReceiver(iconOnAppDrawerReceiver);
            uAppLockService.unregisterReceiver(notificationReceiver);
            uAppLockService.unregisterReceiver(screenReceiver);
        } catch (IllegalArgumentException e) {
            Log.e("Exception", e.getMessage());
        }
    }
}

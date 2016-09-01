package com.arrg.app.uapplock.presenter;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.UAppLockServicePresenter;
import com.arrg.app.uapplock.interfaces.UAppLockServiceView;
import com.arrg.app.uapplock.model.receiver.IconOnAppDrawerReceiver;
import com.arrg.app.uapplock.model.receiver.NotificationReceiver;
import com.arrg.app.uapplock.model.service.UAppLockService;

public class IUAppLockServicePresenter implements UAppLockServicePresenter{

    private IconOnAppDrawerReceiver iconOnAppDrawerReceiver;
    private NotificationReceiver notificationReceiver;
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
    }

    @Override
    public void registerIconOnAppDrawerReceiver() {
        iconOnAppDrawerReceiver = new IconOnAppDrawerReceiver();
        IntentFilter applicationFilter = new IntentFilter();

        applicationFilter.addAction(UAppLock.ACTION_HIDE_APPLICATION);
        applicationFilter.addAction(UAppLock.ACTION_SHOW_APPLICATION);

        uAppLockService.registerReceiver(iconOnAppDrawerReceiver, applicationFilter);

        Log.e("registerIconReceiver", "Done");
    }

    @Override
    public void registerNotificationReceiver() {
        notificationReceiver = new NotificationReceiver(uAppLockService);
        IntentFilter notificationFilter = new IntentFilter();

        notificationFilter.addAction(UAppLock.ACTION_SHOW_NOTIFICATION);
        notificationFilter.addAction(UAppLock.ACTION_HIDE_NOTIFICATION);

        uAppLockService.registerReceiver(notificationReceiver, notificationFilter);

        uAppLockServiceView.notificationHandler();

        Log.e("registerNotReceiver", "Done");
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
        } catch (IllegalArgumentException e){
            Log.e("Exception", e.getMessage());
        }
    }
}

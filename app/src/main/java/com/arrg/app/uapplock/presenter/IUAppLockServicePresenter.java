package com.arrg.app.uapplock.presenter;

import android.content.Intent;
import android.content.IntentFilter;

import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.UAppLockServicePresenter;
import com.arrg.app.uapplock.interfaces.UAppLockServiceView;
import com.arrg.app.uapplock.model.receiver.IconOnAppDrawerReceiver;
import com.arrg.app.uapplock.model.receiver.ScreenReceiver;
import com.arrg.app.uapplock.model.service.UAppLockService;

public class IUAppLockServicePresenter implements UAppLockServicePresenter{

    private UAppLockService uAppLockService;
    private UAppLockServiceView uAppLockServiceView;

    public IUAppLockServicePresenter(UAppLockServiceView uAppLockServiceView) {
        this.uAppLockServiceView = uAppLockServiceView;
    }

    @Override
    public void onCreate(UAppLockService uAppLockService) {
        this.uAppLockService = uAppLockService;

        registerScreenReceiver();
        registerIconOnAppDrawerReceiver();
    }

    @Override
    public void registerScreenReceiver() {
        ScreenReceiver screenReceiver = new ScreenReceiver();
        IntentFilter screenFilter = new IntentFilter();

        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);

        uAppLockService.registerReceiver(screenReceiver, screenFilter);
    }

    @Override
    public void registerIconOnAppDrawerReceiver() {
        IconOnAppDrawerReceiver iconOnAppDrawerReceiver = new IconOnAppDrawerReceiver();
        IntentFilter applicationFilter = new IntentFilter();

        applicationFilter.addAction(UAppLock.ACTION_HIDE_APPLICATION);
        applicationFilter.addAction(UAppLock.ACTION_SHOW_APPLICATION);

        uAppLockService.registerReceiver(iconOnAppDrawerReceiver, applicationFilter);
    }
}

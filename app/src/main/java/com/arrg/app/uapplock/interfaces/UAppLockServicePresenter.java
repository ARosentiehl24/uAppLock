package com.arrg.app.uapplock.interfaces;

import com.arrg.app.uapplock.model.service.UAppLockService;

public interface UAppLockServicePresenter {

    void onCreate(UAppLockService uAppLockService);

    void registerIconOnAppDrawerReceiver();

    void registerNotificationReceiver();

    void registerScreenReceiver();

    void restartServiceIfNeeded();

    void unregisterReceivers();
}

package com.arrg.app.uapplock.interfaces;

public interface UAppLockServiceView {
    void init();

    void notificationHandler();

    void startForeground();

    void run();

    String getTopPackage();

    void handlePackageOnTop(String packageOnTop, Boolean update, Boolean close);

    void unlockApp(String packageOnTop);

    void lockApp(String packageOnTop);

    void lockAllApps();
}

package com.arrg.app.uapplock.interfaces;

public interface UAppLockServiceView {
    void init();

    void notificationHandler();

    void startForeground();

    void startMonitor();

    void stopMonitor();

    void run();

    void handlePackageOnTop(String packageOnTop);

    String getTopPackageName();
}

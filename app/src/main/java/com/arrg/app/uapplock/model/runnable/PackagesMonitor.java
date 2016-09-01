package com.arrg.app.uapplock.model.runnable;

import com.arrg.app.uapplock.interfaces.UAppLockServiceView;

public class PackagesMonitor implements Runnable {

    private UAppLockServiceView uAppLockServiceView;

    public PackagesMonitor(UAppLockServiceView uAppLockServiceView) {
        this.uAppLockServiceView = uAppLockServiceView;
    }

    @Override
    public void run() {

    }
}

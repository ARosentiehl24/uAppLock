package com.arrg.app.uapplock.model.runnable;

import com.arrg.app.uapplock.interfaces.UAppLockServiceView;

public class UpdatesMonitor implements Runnable {

    private UAppLockServiceView uAppLockServiceView;

    public UpdatesMonitor(UAppLockServiceView uAppLockServiceView) {
        this.uAppLockServiceView = uAppLockServiceView;
    }

    @Override
    public void run() {

    }
}

package com.arrg.app.uapplock.interfaces;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public interface LockScreenServiceView {

    void beforeInflate();

    View inflateRootView();

    void launchHomeScreen();

    void finish();
}

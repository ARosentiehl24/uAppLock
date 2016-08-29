package com.arrg.app.uapplock.interfaces;

import android.content.Intent;
import android.view.View;

public interface LockScreenServiceView {

    void beforeInflate();

    View inflateRootView(Intent intent);

    void showLockScreen();

    void hideLockScreen();

    void launchHomeScreen();

    void finish();

    void showPrevious();

    void showNext();
}

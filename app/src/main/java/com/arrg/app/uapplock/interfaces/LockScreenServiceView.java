package com.arrg.app.uapplock.interfaces;

import android.content.Intent;
import android.view.View;

public interface LockScreenServiceView {

    void beforeInflate();

    View inflateRootView(Intent intent);

    void configViews();

    void configLockScreen(String packageOnTop);

    void setListener();

    void showLockScreen();

    void hideLockScreen();

    void launchHomeScreen();

    void playUnlockSound();

    void finish();

    void showPrevious();

    void showNext();

    void handlePosition(int id);
}

package com.arrg.app.uapplock.interfaces;

import android.content.Context;

public interface SplashScreenView {

    boolean allSettingsAreComplete();
    void setupViews();
    void launchActivity(Class classDestination, Integer duration);
    void defaultUnlockMethodChosen(Integer unlockMethod);
    Context getContext();
}

package com.arrg.app.uapplock.presenter;

import com.arrg.app.uapplock.interfaces.SplashScreenPresenter;
import com.arrg.app.uapplock.interfaces.SplashScreenView;
import com.arrg.app.uapplock.view.activity.IntroActivity;

public class ISplashScreenPresenter implements SplashScreenPresenter {

    private SplashScreenView splashScreenView;

    public ISplashScreenPresenter(SplashScreenView splashScreenView) {
        this.splashScreenView = splashScreenView;
    }

    @Override
    public void onCreate() {
        launch(splashScreenView.allSettingsAreComplete());
    }

    @Override
    public void launch(boolean allSettingsAreComplete) {
        if (allSettingsAreComplete) {
            splashScreenView.launchActivity(IntroActivity.class, 200);
        } else {
            splashScreenView.launchActivity(IntroActivity.class, 2000);
        }
    }
}

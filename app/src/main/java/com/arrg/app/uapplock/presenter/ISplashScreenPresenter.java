package com.arrg.app.uapplock.presenter;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.SplashScreenPresenter;
import com.arrg.app.uapplock.interfaces.SplashScreenView;
import com.arrg.app.uapplock.model.service.UAppLockService;
import com.arrg.app.uapplock.view.activity.ApplicationListActivity;

import static com.arrg.app.uapplock.UAppLock.PATTERN;
import static com.arrg.app.uapplock.UAppLock.PIN;

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
            splashScreenView.launchActivity(ApplicationListActivity.class, 200);
        } else {
            splashScreenView.setupViews();
        }
    }

    @Override
    public void onClick(int id) {
        switch (id) {
            case R.id.btnSetPattern:
                splashScreenView.defaultUnlockMethodChosen(PATTERN);
                break;
            case R.id.btnSetPin:
                splashScreenView.defaultUnlockMethodChosen(PIN);
                break;
        }
    }
}

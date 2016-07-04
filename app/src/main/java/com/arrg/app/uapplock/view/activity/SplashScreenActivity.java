package com.arrg.app.uapplock.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.SplashScreenView;
import com.arrg.app.uapplock.presenter.ISplashScreenPresenter;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import org.fingerlinks.mobile.android.navigator.Navigator;

public class SplashScreenActivity extends AppCompatActivity implements SplashScreenView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        TypefaceHelper.typeface(this);

        ISplashScreenPresenter iSplashScreenPresenter = new ISplashScreenPresenter(this);
        iSplashScreenPresenter.onCreate();
    }

    @Override
    public boolean allSettingsAreComplete() {
        return PreferencesManager.getBoolean(getString(R.string.all_settings_are_complete));
    }

    @Override
    public void launchActivity(final Class classDestination, Integer duration) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Navigator.with(SplashScreenActivity.this).build().goTo(classDestination).animation(android.R.anim.fade_in, android.R.anim.fade_out).commit();
                finish();
            }
        }, duration);
    }
}

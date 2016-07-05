package com.arrg.app.uapplock.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.SplashScreenView;
import com.arrg.app.uapplock.presenter.ISplashScreenPresenter;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import org.fingerlinks.mobile.android.navigator.Navigator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashScreenActivity extends AppCompatActivity implements SplashScreenView {

    @Bind(R.id.divider)
    View divider;
    @Bind(R.id.view)
    AppCompatImageView view;
    @Bind(R.id.tvWelcomeMessage)
    AppCompatTextView tvWelcomeMessage;
    @Bind(R.id.btnSetPattern)
    AppCompatButton btnSetPattern;
    @Bind(R.id.btnSetPin)
    AppCompatButton btnSetPin;

    private ISplashScreenPresenter iSplashScreenPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);

        iSplashScreenPresenter = new ISplashScreenPresenter(this);
        iSplashScreenPresenter.onCreate();
    }

    @Override
    public boolean allSettingsAreComplete() {
        return PreferencesManager.getBoolean(getString(R.string.all_settings_are_complete));
    }

    @Override
    public void setupViews() {
        //Glide.with(this).load(R.drawable.ic_launcher_hd).asBitmap().into(view);

        btnSetPattern.setVisibility(View.VISIBLE);
        btnSetPin.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

        tvWelcomeMessage.setText(R.string.welcome_message);
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

    @Override
    public void defaultUnlockMethodChosen(String unlockMethod) {
        PreferencesManager.putString(getString(R.string.unlock_method), unlockMethod);

        Navigator.with(SplashScreenActivity.this).build().goTo(IntroActivity.class).animation(android.R.anim.fade_in, android.R.anim.fade_out).commit();
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @OnClick({R.id.btnSetPattern, R.id.btnSetPin})
    public void onClick(View view) {
        iSplashScreenPresenter.onClick(view.getId());
    }
}

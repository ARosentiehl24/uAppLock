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
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.FadeInAnimation;
import com.easyandroidanimations.library.ParallelAnimator;
import com.easyandroidanimations.library.SlideInAnimation;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import org.fingerlinks.mobile.android.navigator.Navigator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashScreenActivity extends AppCompatActivity implements SplashScreenView {

    @Bind(R.id.divider)
    View divider;
    @Bind(R.id.fingerprint)
    AppCompatImageView fingerprint;
    @Bind(R.id.tvWelcomeMessage)
    AppCompatTextView tvWelcomeMessage;
    @Bind(R.id.btnSetPattern)
    AppCompatButton btnSetPattern;
    @Bind(R.id.btnSetPin)
    AppCompatButton btnSetPin;
    @Bind(R.id.pin)
    AppCompatImageView pin;
    @Bind(R.id.pattern)
    AppCompatImageView pattern;

    private static final int SPLASH_SCREEN_ANIMATION = 1000;
    public static SplashScreenActivity splashScreenActivity;
    private ISplashScreenPresenter iSplashScreenPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);

        splashScreenActivity = this;

        iSplashScreenPresenter = new ISplashScreenPresenter(this);
        iSplashScreenPresenter.onCreate();
    }

    @Override
    public boolean allSettingsAreComplete() {
        return PreferencesManager.getBoolean(getString(R.string.all_settings_are_complete));
    }

    @Override
    public void setupViews() {
        //Glide.with(this).load(R.drawable.ic_icon_background).asBitmap().into(view);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ParallelAnimator()
                        .add(new SlideInAnimation(fingerprint).setDirection(Animation.DIRECTION_UP))
                        .add(new FadeInAnimation(fingerprint))
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();

                new ParallelAnimator()
                        .add(new SlideInAnimation(pattern).setDirection(Animation.DIRECTION_LEFT))
                        .add(new FadeInAnimation(pattern))
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();

                new ParallelAnimator()
                        .add(new SlideInAnimation(pin).setDirection(Animation.DIRECTION_RIGHT))
                        .add(new FadeInAnimation(pin))
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();

                new ParallelAnimator()
                        .add(new SlideInAnimation(tvWelcomeMessage).setDirection(Animation.DIRECTION_DOWN))
                        .add(new FadeInAnimation(tvWelcomeMessage))
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();

                new FadeInAnimation(btnSetPattern)
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();

                new FadeInAnimation(btnSetPin)
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();

                new FadeInAnimation(divider)
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();
            }
        }, 125);

        /**/

        tvWelcomeMessage.setText(R.string.welcome_message);
    }

    @Override
    public void launchActivity(final Class classDestination, Integer duration) {
        fingerprint.setVisibility(View.VISIBLE);
        pattern.setVisibility(View.VISIBLE);
        pin.setVisibility(View.VISIBLE);
        tvWelcomeMessage.setVisibility(View.VISIBLE);

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

        Navigator.with(SplashScreenActivity.this).build().goTo(IntroActivity.class).animation().commit();
        //finish();
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

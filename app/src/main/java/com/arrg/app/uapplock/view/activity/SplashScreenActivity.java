package com.arrg.app.uapplock.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

    private static final int SPLASH_SCREEN_ANIMATION = 750;
    public static SplashScreenActivity splashScreenActivity;
    private ISplashScreenPresenter iSplashScreenPresenter;

    @Bind(R.id.icon)
    RelativeLayout icon;
    @Bind(R.id.tvWelcomeMessage)
    AppCompatTextView tvWelcomeMessage;
    @Bind(R.id.buttonBarContainer)
    LinearLayout buttonBarContainer;
    @Bind(R.id.divider)
    View divider;

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
    public void onBackPressed() {
        Navigator.with(this).utils().finishWithAnimation();
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
                        .add(new SlideInAnimation(tvWelcomeMessage).setDirection(Animation.DIRECTION_DOWN))
                        .add(new FadeInAnimation(tvWelcomeMessage))
                        .add(new SlideInAnimation(icon).setDirection(Animation.DIRECTION_UP))
                        .add(new FadeInAnimation(icon))
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();

                new FadeInAnimation(buttonBarContainer)
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();

                new FadeInAnimation(divider)
                        .setDuration(SPLASH_SCREEN_ANIMATION)
                        .animate();
            }
        }, 125);

        tvWelcomeMessage.setText(R.string.welcome_message);
    }

    @Override
    public void launchActivity(final Class classDestination, Integer duration) {
        icon.setVisibility(View.VISIBLE);
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

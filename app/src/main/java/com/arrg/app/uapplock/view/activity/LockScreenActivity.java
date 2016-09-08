package com.arrg.app.uapplock.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.LockScreenServiceView;
import com.arrg.app.uapplock.view.ui.MaterialLockView;
import com.nvanbenschoten.motion.ParallaxImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LockScreenActivity extends AppCompatActivity  implements LockScreenServiceView, PinLockListener {

    @BindView(R.id.revealView)
    FrameLayout revealView;
    @BindView(R.id.unlockingMethods)
    ViewFlipper unlockingMethods;
    @BindView(R.id.initialView)
    View initialView;
    /*@BindView(R.id.missView)
    MissView missView;*/
    @BindView(R.id.parallaxImageView)
    ParallaxImageView parallaxImageView;
    @BindView(R.id.appIcon)
    ImageView appIcon;
    @BindView(R.id.tvFingerprintMessage)
    AppCompatTextView tvFingerprintMessage;
    @BindView(R.id.materialLockView)
    MaterialLockView materialLockView;
    @BindView(R.id.indicatorDots)
    IndicatorDots indicatorDots;
    @BindView(R.id.pinLockView)
    PinLockView pinLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);
        ButterKnife.bind(this);

        Bundle lockScreenIntent = getIntent().getExtras();

        Log.d("UAppLockService", lockScreenIntent.getString(UAppLock.EXTRA_PACKAGE_NAME));
    }

    @Override
    public void onBackPressed() {
        Intent startHomeScreen = new Intent(Intent.ACTION_MAIN);
        startHomeScreen.addCategory(Intent.CATEGORY_HOME);
        startHomeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startHomeScreen);
        finish();
    }

    @Override
    public void beforeInflate() {

    }

    @Override
    public View inflateRootView(Intent intent) {
        return null;
    }

    @Override
    public void configViews() {

    }

    @Override
    public void configLockScreen(String packageOnTop) {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void showLockScreen() {

    }

    @Override
    public void hideLockScreen() {

    }

    @Override
    public void launchHomeScreen() {

    }

    @Override
    public void playUnlockSound() {

    }

    @Override
    public void showPrevious() {

    }

    @Override
    public void showNext() {

    }

    @Override
    public void handlePosition(int id) {

    }

    @Override
    public void onComplete(String pin) {

    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onPinChange(int pinLength, String intermediatePin) {

    }
}

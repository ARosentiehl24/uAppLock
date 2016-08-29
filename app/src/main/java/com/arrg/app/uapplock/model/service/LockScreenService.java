package com.arrg.app.uapplock.model.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockView;
import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.LockScreenServiceView;
import com.arrg.app.uapplock.model.listener.SwipeGestureDetector;
import com.arrg.app.uapplock.view.ui.MaterialLockView;
import com.jaouan.revealator.Revealator;
import com.nvanbenschoten.motion.ParallaxImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LockScreenService extends Service implements View.OnKeyListener, LockScreenServiceView {

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
    @BindView(R.id.appName)
    AppCompatTextView appName;
    @BindView(R.id.tvFingerprintMessage)
    AppCompatTextView tvFingerprintMessage;
    @BindView(R.id.materialLockView)
    MaterialLockView materialLockView;
    @BindView(R.id.indicatorDots)
    IndicatorDots indicatorDots;
    @BindView(R.id.pinLockView)
    PinLockView pinLockView;

    private GestureDetector gestureDetector;
    private View rootView;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;

    public LockScreenService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        beforeInflate();

        rootView = inflateRootView(intent);

        windowManager.addView(rootView, layoutParams);

        showLockScreen();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        switch (i) {
            case KeyEvent.KEYCODE_BACK:
                launchHomeScreen();

                hideLockScreen();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, UAppLock.DURATIONS_OF_ANIMATIONS);
                return true;
        }
        return true;
    }

    public static Intent lockPackage(Context context, String packageName) {
        Intent lockIntent = new Intent(context, LockScreenService.class);
        lockIntent.putExtra(UAppLock.EXTRA_PACKAGE_NAME, packageName);
        lockIntent.setAction(UAppLock.ACTION_COMPARE);

        return lockIntent;
    }

    @Override
    public void beforeInflate() {
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        gestureDetector = new GestureDetector(this, new SwipeGestureDetector(this));
    }

    @Override
    public View inflateRootView(Intent intent) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        setTheme(R.style.AppTheme);

        View lockScreen = layoutInflater.inflate(R.layout.lock_screen, null);
        ButterKnife.bind(this, lockScreen);

        lockScreen.setOnKeyListener(this);
        lockScreen.setFocusable(true);
        lockScreen.setFocusableInTouchMode(true);

        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(this);
        if (!fingerprintManagerCompat.isHardwareDetected()) {
            unlockingMethods.removeViewAt(0);
        }

        appName.setTypeface(UAppLock.typeface());
        pinLockView.attachIndicatorDots(indicatorDots);
        tvFingerprintMessage.setTypeface(UAppLock.typeface());
        unlockingMethods.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !gestureDetector.onTouchEvent(event);
            }
        });

        String packageOnTop = intent.getStringExtra(UAppLock.EXTRA_PACKAGE_NAME);

        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(packageOnTop, 0);
            appIcon.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));
            appName.setText(applicationInfo.loadLabel(getPackageManager()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Palette.from(((BitmapDrawable) appIcon.getDrawable()).getBitmap()).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                Integer iconColor = palette.getDominantColor(ContextCompat.getColor(LockScreenService.this, R.color.colorAccent));
                revealView.setBackgroundColor(iconColor);
            }
        });

        return lockScreen;
    }

    @Override
    public void showLockScreen() {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                Revealator.reveal(revealView).from(initialView).withRevealDuration(UAppLock.DURATIONS_OF_ANIMATIONS).withChildsAnimation().start();
            }
        });
    }

    @Override
    public void hideLockScreen() {
        Revealator.unreveal(revealView).to(initialView).withUnrevealDuration(UAppLock.DURATIONS_OF_ANIMATIONS).start();
    }

    @Override
    public void showPrevious() {
        unlockingMethods.setInAnimation(this, R.anim.view_flipper_transition_in_right);
        unlockingMethods.setOutAnimation(this, R.anim.view_flipper_transition_out_right);
        unlockingMethods.showPrevious();
    }

    @Override
    public void showNext() {
        unlockingMethods.setInAnimation(this, R.anim.view_flipper_transition_in_left);
        unlockingMethods.setOutAnimation(this, R.anim.view_flipper_transition_out_left);
        unlockingMethods.showNext();
    }

    @Override
    public void launchHomeScreen() {
        Intent startHomeScreen = new Intent(Intent.ACTION_MAIN);
        startHomeScreen.addCategory(Intent.CATEGORY_HOME);
        startHomeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startHomeScreen);
    }

    @Override
    public void finish() {
        try {
            windowManager.removeView(rootView);
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), e.getMessage());
        }

        stopSelf();
    }
}

package com.arrg.app.uapplock.model.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
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
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.LockScreenServiceView;
import com.arrg.app.uapplock.model.listener.SwipeGestureDetector;
import com.arrg.app.uapplock.view.ui.MaterialLockView;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.ShakeAnimation;
import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.jaouan.revealator.Revealator;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.shawnlin.preferencesmanager.PreferencesManager;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.arrg.app.uapplock.UAppLock.DURATIONS_OF_ANIMATIONS;

public class LockScreenService extends Service implements LockScreenServiceView, PinLockListener, View.OnKeyListener {

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

    public static LockScreenService LOCK_SCREEN;

    private ActivityManager activityManager;
    private GestureDetector gestureDetector;
    private Runnable finish = new Runnable() {
        @Override
        public void run() {
            try {
                windowManager.removeView(rootView);
            } catch (IllegalArgumentException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }

            stopSelf();
        }
    };
    private String packageOnTop;
    private Vibrator vibrator;
    private View rootView;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;

    public LockScreenService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LOCK_SCREEN = this;
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
    public void onDestroy() {
        Reprint.cancelAuthentication();

        super.onDestroy();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        switch (i) {
            case KeyEvent.KEYCODE_BACK:
                launchHomeScreen();
                finish();

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
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

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
        packageOnTop = intent.getStringExtra(UAppLock.EXTRA_PACKAGE_NAME);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        setTheme(R.style.AppTheme);

        View lockScreen = layoutInflater.inflate(R.layout.lock_screen, null);
        ButterKnife.bind(this, lockScreen);

        lockScreen.setOnKeyListener(this);
        lockScreen.setFocusable(true);
        lockScreen.setFocusableInTouchMode(true);

        configViews();

        configLockScreen(packageOnTop);

        handlePosition(unlockingMethods.getCurrentView().getId());

        setListener();

        return lockScreen;
    }

    @Override
    public void configViews() {
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(this);

        Boolean isPatternConfigured = PreferencesManager.getString(getString(R.string.user_pattern)).length() != 0;
        Boolean isPinConfigured = PreferencesManager.getString(getString(R.string.user_pin)).length() != 0;
        Boolean stealthMode = PreferencesManager.getBoolean(getString(R.string.stealth_mode_key));

        Integer lastUnlockingMethod = PreferencesManager.getInt(getString(R.string.unlock_method));

        if (fingerprintManagerCompat.isHardwareDetected()) {
            unlockingMethods.setDisplayedChild(lastUnlockingMethod);
        } else {
            unlockingMethods.removeViewAt(0);
            unlockingMethods.setDisplayedChild(lastUnlockingMethod - 1);
        }

        appName.setTypeface(UAppLock.typeface());
        materialLockView.setEnabled(isPatternConfigured);
        materialLockView.setInStealthMode(stealthMode);
        pinLockView.attachIndicatorDots(isPinConfigured ? indicatorDots : null);
        pinLockView.setEnabled(isPinConfigured);
        pinLockView.setShowDeleteButton(true);
        tvFingerprintMessage.setTypeface(UAppLock.typeface());
        unlockingMethods.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public void configLockScreen(String packageOnTop) {
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
    }

    @Override
    public void setListener() {
        materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) {
                String storedPattern = PreferencesManager.getString(getString(R.string.user_pattern));

                if (SimplePattern.equals(storedPattern)) {
                    playUnlockSound();
                    finish();
                } else {
                    new ShakeAnimation(materialLockView).setNumOfShakes(2).setDuration(Animation.DURATION_SHORT).animate();

                    materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);

                    vibrator.vibrate(DURATIONS_OF_ANIMATIONS);
                }
            }
        });

        pinLockView.setPinLockListener(this);

        tvFingerprintMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reprint.authenticate(new AuthenticationListener() {
                    @Override
                    public void onSuccess(int moduleTag) {
                        playUnlockSound();
                        finish();
                    }

                    @Override
                    public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
                        tvFingerprintMessage.setText(errorMessage);
                    }
                });
            }
        });
    }

    @Override
    public void showLockScreen() {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                Revealator
                        .reveal(revealView)
                        .from(initialView)
                        .withRevealDuration(UAppLock.DURATIONS_OF_ANIMATIONS)
                        .withChildsAnimation()
                        .withChildAnimationDuration(UAppLock.DURATIONS_OF_ANIMATIONS)
                        .start();
            }
        });
    }

    @Override
    public void hideLockScreen() {
        Revealator
                .unreveal(revealView)
                .to(initialView)
                .withUnrevealDuration(UAppLock.DURATIONS_OF_ANIMATIONS)
                .start();
    }

    @Override
    public void showPrevious() {
        unlockingMethods.setInAnimation(this, R.anim.view_flipper_transition_in_right);
        unlockingMethods.setOutAnimation(this, R.anim.view_flipper_transition_out_right);
        unlockingMethods.showPrevious();

        handlePosition(unlockingMethods.getCurrentView().getId());
    }

    @Override
    public void showNext() {
        unlockingMethods.setInAnimation(this, R.anim.view_flipper_transition_in_left);
        unlockingMethods.setOutAnimation(this, R.anim.view_flipper_transition_out_left);
        unlockingMethods.showNext();

        handlePosition(unlockingMethods.getCurrentView().getId());
    }

    @Override
    public void handlePosition(int id) {
        Reprint.cancelAuthentication();

        Reprint.initialize(this);

        materialLockView.clearPattern();
        pinLockView.resetPinLockView();
        tvFingerprintMessage.setText(R.string.use_your_fingerprint_to_unlock);

        switch (id) {
            case R.id.fingerprintLockScreen:
                Reprint.authenticate(new AuthenticationListener() {
                    @Override
                    public void onSuccess(int moduleTag) {
                        playUnlockSound();
                        finish();
                    }

                    @Override
                    public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
                        tvFingerprintMessage.setText(errorMessage);
                    }
                });
                break;
        }
    }

    @Override
    public void launchHomeScreen() {
        if (!packageOnTop.equals(getPackageName())) {
            Log.i(getClass().getSimpleName(), "Killing: " + packageOnTop);
            activityManager.killBackgroundProcesses(packageOnTop);
        }

        Intent startHomeScreen = new Intent(Intent.ACTION_MAIN);
        startHomeScreen.addCategory(Intent.CATEGORY_HOME);
        startHomeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startHomeScreen);
    }

    @Override
    public void playUnlockSound() {
        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd("sounds/unlock.ogg");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        Reprint.cancelAuthentication();

        int id = unlockingMethods.getCurrentView().getId();

        switch (id) {
            case R.id.fingerprintLockScreen:
                PreferencesManager.putInt(getString(R.string.unlock_method), UAppLock.FINGERPRINT);
                break;
            case R.id.patternLockScreen:
                PreferencesManager.putInt(getString(R.string.unlock_method), UAppLock.PATTERN);
                break;
            case R.id.pinLockScreen:
                PreferencesManager.putInt(getString(R.string.unlock_method), UAppLock.PIN);
                break;
        }

        hideLockScreen();

        new Handler().postDelayed(finish, 500);
    }

    @Override
    public void onComplete(String pin) {
        String storedPin = PreferencesManager.getString(getString(R.string.user_pin));

        if (pin.equals(storedPin)) {
            playUnlockSound();
            finish();
        } else {
            new ShakeAnimation(indicatorDots).setNumOfShakes(2).setDuration(Animation.DURATION_SHORT).setListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    pinLockView.resetPinLockView();
                }
            }).animate();

            vibrator.vibrate(DURATIONS_OF_ANIMATIONS);
        }
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onPinChange(int pinLength, String intermediatePin) {

    }
}

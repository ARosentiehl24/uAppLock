package com.arrg.app.uapplock.model.service;

import android.animation.Animator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.LockScreenServiceView;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.FadeInAnimation;
import com.easyandroidanimations.library.ScaleInAnimation;
import com.easyandroidanimations.library.ScaleOutAnimation;
import com.jaouan.revealator.Revealator;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import io.codetail.widget.RevealFrameLayout;

public class LockScreenService extends Service implements View.OnKeyListener, LockScreenServiceView {

    @BindView(R.id.revealView)
    FrameLayout revealView;

    @BindView(R.id.initialView)
    View initialView;

    private View mRootView;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;

    public LockScreenService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        beforeInflate();

        mRootView = inflateRootView();

        mWindowManager.addView(mRootView, mLayoutParams);

        //new ScaleInAnimation(mRootView).setDuration(250).animate();

        mRootView.post(new Runnable() {
            @Override
            public void run() {
                Revealator.reveal(revealView).from(initialView).withRevealDuration(250).withChildsAnimation().start();
            }
        });

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

                Revealator.unreveal(revealView).to(initialView).withUnrevealDuration(250).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }).start();

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
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        mLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    public View inflateRootView() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View root = layoutInflater.inflate(R.layout.lock_screen, null);
        ButterKnife.bind(this, root);

        root.setOnKeyListener(this);
        root.setFocusable(true);
        root.setFocusableInTouchMode(true);

        return root;
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
        if (mRootView.getWindowToken() != null) {
            mWindowManager.removeView(mRootView);
        }

        stopSelf();
    }
}

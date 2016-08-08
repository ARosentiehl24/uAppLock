package com.arrg.app.uapplock.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.util.Util;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.ShakeAnimation;
import com.shawnlin.preferencesmanager.PreferencesManager;

import org.fingerlinks.mobile.android.navigator.Navigator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.arrg.app.uapplock.UAppLock.DURATIONS_OF_ANIMATIONS;

public class PinSettingsActivity extends UAppLockActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvMessage)
    AppCompatTextView tvMessage;
    @BindView(R.id.indicatorDots)
    IndicatorDots indicatorDots;
    @BindView(R.id.pinLockView)
    PinLockView pinLockView;
    @BindView(R.id.btnSetPin)
    AppCompatButton btnSetPin;

    private String pin = "";
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Util.modifyToolbar(this, R.string.title_activity_pin_settings, true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        pinLockView.attachIndicatorDots(indicatorDots);
        pinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String userPin) {
                if (pin.length() == 0) {
                    pin = userPin;

                    resetPin();
                    updateText(R.string.message_to_confirm_pin);
                } else {
                    if (pin.equals(userPin)) {
                        btnSetPin.setEnabled(true);
                        pinLockView.attachIndicatorDots(null);

                        updateText(R.string.correct_configuration_message_for_pin);

                        PreferencesManager.putString(getString(R.string.user_pin), pin);
                    } else {
                        new ShakeAnimation(indicatorDots).setNumOfShakes(2).setDuration(Animation.DURATION_SHORT).setListener(new AnimationListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                pinLockView.resetPinLockView();
                            }
                        }).animate();

                        vibrator.vibrate(DURATIONS_OF_ANIMATIONS);

                        updateText(R.string.message_to_repeat_pattern);
                    }
                }
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick({R.id.btnResetPin, R.id.btnSetPin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnResetPin:
                pin = "";

                pinLockView.resetPinLockView();
                pinLockView.attachIndicatorDots(indicatorDots);

                PreferencesManager.putString(getString(R.string.user_pin), pin);

                updateText(R.string.message_to_request_pattern);
                break;
            case R.id.btnSetPin:
                Navigator.with(this).utils().finishWithAnimation();
                break;
        }
    }

    public void resetPin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pinLockView.resetPinLockView();
            }
        }, DURATIONS_OF_ANIMATIONS);
    }

    public void updateText(int text) {
        tvMessage.setText(text);
    }
}

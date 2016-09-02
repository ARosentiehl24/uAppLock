package com.arrg.app.uapplock.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.arrg.app.uapplock.R;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.ShakeAnimation;
import com.shawnlin.preferencesmanager.PreferencesManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arrg.app.uapplock.UAppLock.DURATIONS_OF_ANIMATIONS;

public class RequestPinFragment extends Fragment {

    @BindView(R.id.tvMessage)
    AppCompatTextView tvMessage;
    @BindView(R.id.indicatorDots)
    IndicatorDots indicatorDots;
    @BindView(R.id.pinLockView)
    PinLockView pinLockView;
    private String pin = "";
    private Vibrator vibrator;

    public RequestPinFragment() {
    }

    public static RequestPinFragment newInstance() {
        return new RequestPinFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_pin, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.btnResetPin)
    public void onClick() {
        pin = "";

        pinLockView.resetPinLockView();
        pinLockView.attachIndicatorDots(indicatorDots);

        PreferencesManager.putString(getString(R.string.user_pin), pin);

        updateText(R.string.message_to_request_pin);
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

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

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.view.ui.MaterialLockView;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.ShakeAnimation;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arrg.app.uapplock.UAppLock.DURATIONS_OF_ANIMATIONS;

public class RequestPatternFragment extends Fragment {

    private String pattern = "";
    private Vibrator vibrator;

    @Bind(R.id.tvMessage)
    AppCompatTextView tvMessage;
    @Bind(R.id.materialLockView)
    MaterialLockView materialLockView;

    public RequestPatternFragment() {
    }

    public static RequestPatternFragment newInstance() {
        return new RequestPatternFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_pattern, container, false);
        ButterKnife.bind(this, view);
        TypefaceHelper.typeface(view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> cells, String simplePattern) {
                super.onPatternDetected(cells, simplePattern);

                if (pattern.length() == 0) {
                    pattern = simplePattern;

                    resetPattern();
                    updateText(R.string.message_to_confirm_pattern);
                } else {
                    if (pattern.equals(simplePattern)) {
                        materialLockView.setEnabled(false);

                        updateText(R.string.correct_configuration_message_for_pattern);

                        PreferencesManager.putString(getString(R.string.user_pattern), pattern);
                    } else {
                        new ShakeAnimation(materialLockView).setNumOfShakes(2).setDuration(Animation.DURATION_SHORT).setListener(new AnimationListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                materialLockView.clearPattern();
                            }
                        }).animate();

                        materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);
                        vibrator.vibrate(DURATIONS_OF_ANIMATIONS);

                        updateText(R.string.message_to_repeat_pattern);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnResetPattern)
    public void onClick() {
        pattern = "";

        materialLockView.clearPattern();
        materialLockView.setEnabled(true);

        PreferencesManager.putString(getString(R.string.user_pattern), pattern);

        updateText(R.string.message_to_request_pattern);
    }

    public void resetPattern() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                materialLockView.clearPattern();
            }
        }, DURATIONS_OF_ANIMATIONS);
    }

    public void updateText(int text) {
        tvMessage.setText(text);
    }
}

package com.arrg.app.uapplock.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.util.Util;
import com.arrg.app.uapplock.view.ui.MaterialLockView;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.ShakeAnimation;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import org.fingerlinks.mobile.android.navigator.Navigator;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arrg.app.uapplock.UAppLock.DURATIONS_OF_ANIMATIONS;

public class PatternSettingsActivity extends UAppLockActivity {

    @Bind(R.id.btnSetPattern)
    AppCompatButton btnSetPattern;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvMessage)
    AppCompatTextView tvMessage;
    @Bind(R.id.materialLockView)
    MaterialLockView materialLockView;

    private String pattern = "";
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_settings);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);

        setSupportActionBar(toolbar);

        Util.modifyToolbar(this, R.string.title_activity_pattern_settings, true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> cells, String simplePattern) {
                super.onPatternDetected(cells, simplePattern);

                if (pattern.length() == 0) {
                    pattern = simplePattern;

                    updateText(R.string.message_to_confirm_pattern);
                } else {
                    if (pattern.equals(simplePattern)) {
                        btnSetPattern.setEnabled(true);
                        materialLockView.setEnabled(false);

                        updateText(R.string.correct_configuration_message_for_pattern);

                        PreferencesManager.putString(getString(R.string.user_pattern), pattern);
                    } else {
                        new ShakeAnimation(materialLockView).setNumOfShakes(2).setDuration(Animation.DURATION_SHORT).animate();

                        materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);
                        vibrator.vibrate(DURATIONS_OF_ANIMATIONS);

                        updateText(R.string.message_to_repeat_pattern);
                    }
                }
            }
        });
    }

    @OnClick({R.id.btnResetPattern, R.id.btnSetPattern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnResetPattern:
                pattern = "";

                materialLockView.clearPattern();
                materialLockView.setEnabled(true);

                PreferencesManager.putString(getString(R.string.user_pattern), pattern);

                updateText(R.string.message_to_request_pattern);
                break;
            case R.id.btnSetPattern:
                Navigator.with(this).utils().finishWithAnimation();
                break;
        }
    }

    public void updateText(int text) {
        tvMessage.setText(text);
    }

    @OnClick(R.id.btnSetPattern)
    public void onClick() {
    }
}

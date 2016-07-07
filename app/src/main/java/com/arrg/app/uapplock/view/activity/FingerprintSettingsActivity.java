package com.arrg.app.uapplock.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.util.Util;
import com.arrg.app.uapplock.view.fragment.FingerprintSettingsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FingerprintSettingsActivity extends UAppLockActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Util.modifyToolbar(this, R.string.title_activity_fingerprint_settings);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FingerprintSettingsFragment()).commit();
    }
}

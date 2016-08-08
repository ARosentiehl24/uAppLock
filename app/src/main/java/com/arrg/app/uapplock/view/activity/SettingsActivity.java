package com.arrg.app.uapplock.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.util.Util;
import com.arrg.app.uapplock.view.fragment.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends UAppLockActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Util.modifyToolbar(this, R.string.title_activity_settings, true);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}

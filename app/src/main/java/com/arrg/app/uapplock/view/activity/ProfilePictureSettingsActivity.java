package com.arrg.app.uapplock.view.activity;

import android.os.Bundle;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.util.Util;

import butterknife.ButterKnife;

public class ProfilePictureSettingsActivity extends UAppLockActivity {

    /*@Bind(R.id.toolbar)
    Toolbar toolbar;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_settings);
        ButterKnife.bind(this);

        //setSupportActionBar(toolbar);

        Util.modifyToolbar(this, R.string.title_activity_profile_picture_settings, true);
    }
}

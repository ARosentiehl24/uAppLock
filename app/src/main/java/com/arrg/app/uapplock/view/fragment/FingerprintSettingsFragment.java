package com.arrg.app.uapplock.view.fragment;

import android.os.Bundle;

import com.arrg.app.uapplock.R;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class FingerprintSettingsFragment extends PreferenceFragmentCompatDividers {

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fingerprint_settings);
        setDividerPreferences(DIVIDER_PREFERENCE_AFTER_LAST);
    }
}

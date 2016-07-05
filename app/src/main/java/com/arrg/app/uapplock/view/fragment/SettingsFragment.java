package com.arrg.app.uapplock.view.fragment;

import android.os.Bundle;
import android.support.v7.preference.Preference;

import com.arrg.app.uapplock.R;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class SettingsFragment extends PreferenceFragmentCompatDividers {

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        setDividerPreferences(DIVIDER_PREFERENCE_AFTER_LAST);

    }
}

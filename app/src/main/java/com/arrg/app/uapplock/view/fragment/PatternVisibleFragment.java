package com.arrg.app.uapplock.view.fragment;


import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;

import com.arrg.app.uapplock.R;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class PatternVisibleFragment extends PreferenceFragmentCompatDividers {

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pattern_settings);
        setDividerPreferences(DIVIDER_PADDING_PARENT);

        SwitchPreference stealthMode = (SwitchPreference) findPreference(getString(R.string.stealth_mode_key));
        stealthMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean isInStealthMode = Boolean.parseBoolean(String.valueOf(newValue));

                PreferencesManager.putBoolean(getString(R.string.stealth_mode_key), isInStealthMode);

                return true;
            }
        });
    }
}

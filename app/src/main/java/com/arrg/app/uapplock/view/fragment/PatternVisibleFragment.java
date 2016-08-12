package com.arrg.app.uapplock.view.fragment;


import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.util.kisstools.utils.StringUtil;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;
import com.takisoft.fix.support.v7.preference.SwitchPreferenceCompat;

public class PatternVisibleFragment extends PreferenceFragmentCompatDividers {

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pattern_settings);
        setDividerPreferences(DIVIDER_PADDING_PARENT);

        SwitchPreference stealthMode = (SwitchPreference) findPreference(getString(R.string.stealth_mode));
        stealthMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean isInStealthMode = Boolean.parseBoolean(String.valueOf(newValue));

                PreferencesManager.putBoolean(getString(R.string.stealth_mode), isInStealthMode);

                return true;
            }
        });
    }
}

package com.arrg.app.uapplock.view.fragment;

import android.os.Bundle;
import android.support.v7.preference.Preference;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.view.activity.FingerprintSettingsActivity;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import org.fingerlinks.mobile.android.navigator.Navigator;

public class SettingsFragment extends PreferenceFragmentCompatDividers {

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        setDividerPreferences(DIVIDER_PREFERENCE_AFTER_LAST);

        Preference preference = findPreference(getString(R.string.fingerprint_settings));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Navigator.with(getActivity()).build().goTo(FingerprintSettingsActivity.class).animation().commit();
                return false;
            }
        });
    }
}

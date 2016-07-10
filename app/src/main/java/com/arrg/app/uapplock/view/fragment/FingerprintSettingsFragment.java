package com.arrg.app.uapplock.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class FingerprintSettingsFragment extends PreferenceFragmentCompatDividers {

    private Integer initialUnlockMethod;

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fingerprint_settings);
        setDividerPreferences(DIVIDER_PREFERENCE_AFTER_LAST);

        initialUnlockMethod = PreferencesManager.getInt(getString(R.string.unlock_method));

        SwitchPreference switchPreferenceCompat = (SwitchPreference) findPreference(getString(R.string.enable_fingerprint));
        switchPreferenceCompat.setChecked(isFingerPrintActivated());
        switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean sw = Boolean.valueOf(o.toString());

                PreferencesManager.putBoolean(getString(R.string.fingerprint_recognition_activated), sw);

                if (sw) {
                    PreferencesManager.putInt(getString(R.string.unlock_method), UAppLock.FINGERPRINT);
                } else {
                    PreferencesManager.putInt(getString(R.string.unlock_method), initialUnlockMethod);
                }

                return true;
            }
        });
        UAppLock.uAppLock.setFontTo(switchPreferenceCompat);

        Preference preference = findPreference(getString(R.string.security_settings));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                return false;
            }
        });
        UAppLock.uAppLock.setFontTo(preference);
    }

    public Boolean isFingerPrintActivated() {
        return PreferencesManager.getBoolean(getString(R.string.fingerprint_recognition_activated));
    }
}

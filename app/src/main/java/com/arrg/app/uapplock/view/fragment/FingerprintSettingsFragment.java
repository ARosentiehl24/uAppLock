package com.arrg.app.uapplock.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class FingerprintSettingsFragment extends PreferenceFragmentCompatDividers {

    private FingerprintManagerCompat fingerprintManagerCompat;

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fingerprint_settings);
        setDividerPreferences(DIVIDER_PADDING_PARENT);

        fingerprintManagerCompat = FingerprintManagerCompat.from(getContext());

        SwitchPreference switchPreferenceCompat = (SwitchPreference) findPreference(getString(R.string.enable_fingerprint));
        switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean sw = Boolean.parseBoolean(String.valueOf(newValue));

                if (fingerprintManagerCompat.hasEnrolledFingerprints()) {
                    PreferencesManager.putInt(getString(R.string.unlock_method), UAppLock.FINGERPRINT);

                    PreferencesManager.putBoolean(getString(R.string.fingerprint_recognition_activated), sw);
                } else {
                    ((SwitchPreference) preference).setChecked(false);

                    Toast.makeText(getActivity(), R.string.add_fingerprint_message, Toast.LENGTH_SHORT).show();

                    lunchSecuritySettings();
                }

                return true;
            }
        });

        Preference preference = findPreference(getString(R.string.security_settings));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                lunchSecuritySettings();

                return true;
            }
        });
    }

    public void lunchSecuritySettings() {
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}

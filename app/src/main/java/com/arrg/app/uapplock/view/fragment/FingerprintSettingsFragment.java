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
import com.arrg.app.uapplock.util.kisstools.utils.StringUtil;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class FingerprintSettingsFragment extends PreferenceFragmentCompatDividers {

    private FingerprintManagerCompat fingerprintManagerCompat;
    private Integer initialUnlockMethod;
    private SwitchPreference switchPreferenceCompat;

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fingerprint_settings);
        setDividerPreferences(DIVIDER_PADDING_PARENT);

        fingerprintManagerCompat = FingerprintManagerCompat.from(getContext());
        initialUnlockMethod = PreferencesManager.getInt(getString(R.string.unlock_method));

        switchPreferenceCompat = (SwitchPreference) findPreference(getString(R.string.enable_fingerprint));
        switchPreferenceCompat.setChecked(isFingerPrintActivated());
        switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean sw = Boolean.parseBoolean(String.valueOf(o));

                if (sw) {
                    if (fingerprintManagerCompat.hasEnrolledFingerprints()) {
                        PreferencesManager.putInt(getString(R.string.unlock_method), UAppLock.FINGERPRINT);
                    } else {
                        ((SwitchPreference) preference).setChecked(false);

                        Toast.makeText(getActivity(), R.string.add_fingerprint_message, Toast.LENGTH_SHORT).show();

                        lunchSecuritySettings();
                    }
                }

                PreferencesManager.putBoolean(getString(R.string.fingerprint_recognition_activated), sw);

                return true;
            }
        });

        Preference preference = findPreference(getString(R.string.security_settings));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                lunchSecuritySettings();

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        switchPreferenceCompat.setChecked(fingerprintManagerCompat.hasEnrolledFingerprints());
    }

    public Boolean isFingerPrintActivated() {
        return PreferencesManager.getBoolean(getString(R.string.fingerprint_recognition_activated));
    }

    public void lunchSecuritySettings() {
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}

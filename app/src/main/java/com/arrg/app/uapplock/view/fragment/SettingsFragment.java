package com.arrg.app.uapplock.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.util.kisstools.utils.ToastUtil;
import com.arrg.app.uapplock.view.activity.FingerprintSettingsActivity;
import com.arrg.app.uapplock.view.activity.PatternSettingsActivity;
import com.arrg.app.uapplock.view.activity.PinSettingsActivity;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import org.fingerlinks.mobile.android.navigator.Navigator;

import me.a7madev.androidglobalutils.GlobalUtils;

public class SettingsFragment extends PreferenceFragmentCompatDividers {

    private ListPreference unlockMethod;
    private FingerprintManagerCompat fingerprintManagerCompat;
    private String[] unlockMethodChosen;

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        setDividerPreferences(DIVIDER_PREFERENCE_AFTER_LAST);

        fingerprintManagerCompat = FingerprintManagerCompat.from(getActivity());

        Preference fingerprintSettings = findPreference(getString(R.string.fingerprint_settings));
        if (fingerprintManagerCompat.isHardwareDetected()) {
            fingerprintSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Navigator.with(getActivity()).build().goTo(FingerprintSettingsActivity.class).animation().commit();
                    return false;
                }
            });
        } else {
            fingerprintSettings.setEnabled(false);
            fingerprintSettings.setPersistent(false);
            fingerprintSettings.setSelectable(false);
            fingerprintSettings.setSummary(getString(R.string.fingerprint_not_supported_message));
        }

        Preference patternSettings = findPreference(getString(R.string.pattern_settings));
        patternSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Navigator.with(getActivity()).build().goTo(PatternSettingsActivity.class).animation().commit();
                return false;
            }
        });

        Preference pinSettings = findPreference(getString(R.string.pin_settings));
        pinSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Navigator.with(getActivity()).build().goTo(PinSettingsActivity.class).animation().commit();
                return false;
            }
        });

        unlockMethod = (ListPreference) findPreference(getString(R.string.unlock_method));
        unlockMethodChosen = GlobalUtils.getStringArray(getActivity(), R.array.unlock_methods);
    }

    @Override
    public void onResume() {
        super.onResume();

        Integer unlockMethodIndex = PreferencesManager.getInt(getString(R.string.unlock_method));

        if (unlockMethodIndex.equals(UAppLock.FINGERPRINT)) {
            if (!isFingerPrintActivated()) {
                if (patternWasConfigured()) {
                    unlockMethodIndex = UAppLock.PATTERN;
                } else {
                    unlockMethodIndex = UAppLock.PIN;
                }
            }
        }

        unlockMethod.setSummary(String.format(getString(R.string.unlock_method_chosen), unlockMethodChosen[unlockMethodIndex]));
        unlockMethod.setValueIndex(unlockMethodIndex);

        unlockMethod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                setupUnlockMethodIfIsNecessary(Integer.valueOf(o.toString()));
                return true;
            }
        });
    }

    private void setupUnlockMethodIfIsNecessary(Integer index) {
        if (index.equals(UAppLock.FINGERPRINT)) {
            if (fingerprintManagerCompat.isHardwareDetected()) {
                if (fingerprintManagerCompat.hasEnrolledFingerprints()) {
                    PreferencesManager.putBoolean(getString(R.string.fingerprint_recognition_activated), true);

                    update(index);
                } else {
                    ToastUtil.show(R.string.add_fingerprint_message);

                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                }
            } else {
                ToastUtil.show(R.string.fingerprint_not_supported_message);
            }
        } else if (index.equals(UAppLock.PATTERN)) {
            if (patternWasConfigured()) {
                update(index);
            } else {
                Navigator.with(getActivity()).build().goTo(PatternSettingsActivity.class).animation().commit();
            }
        } else {
            if (pinWasConfigured()){
                update(index);
            } else {
                Navigator.with(getActivity()).build().goTo(PinSettingsActivity.class).animation().commit();
            }
        }
    }

    public Boolean pinWasConfigured() {
        return userPin().length() != 0;
    }

    public String userPin() {
        return PreferencesManager.getString(getString(R.string.user_pin), "");
    }

    public Boolean patternWasConfigured() {
        return userPattern().length() != 0;
    }

    public String userPattern() {
        return PreferencesManager.getString(getString(R.string.user_pattern), "");
    }

    public Boolean isFingerPrintActivated() {
        return PreferencesManager.getBoolean(getString(R.string.fingerprint_recognition_activated));
    }

    public void update(Integer index) {
        unlockMethod.setSummary(String.format(getString(R.string.unlock_method_chosen), unlockMethodChosen[index]));

        PreferencesManager.putInt(getString(R.string.unlock_method), index);
    }
}

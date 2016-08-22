package com.arrg.app.uapplock.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.util.PackageUtils;
import com.arrg.app.uapplock.util.kisstools.utils.ToastUtil;
import com.arrg.app.uapplock.view.activity.FingerprintSettingsActivity;
import com.arrg.app.uapplock.view.activity.FontSettingsActivity;
import com.arrg.app.uapplock.view.activity.LicensesActivity;
import com.arrg.app.uapplock.view.activity.PatternSettingsActivity;
import com.arrg.app.uapplock.view.activity.PinSettingsActivity;
import com.arrg.app.uapplock.view.activity.WallpaperSettingsActivity;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.takisoft.fix.support.v7.preference.PreferenceCategory;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import org.fingerlinks.mobile.android.navigator.Navigator;

import de.cketti.mailto.EmailIntentBuilder;
import me.a7madev.androidglobalutils.GlobalUtils;

public class SettingsFragment extends PreferenceFragmentCompatDividers {

    private ListPreference unlockMethod;
    private FingerprintManagerCompat fingerprintManagerCompat;
    private Integer unlockMethodIndex;
    private String[] unlockMethodChosen;

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        setDividerPreferences(DIVIDER_PADDING_PARENT);

        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().getPreference(0);

        fingerprintManagerCompat = FingerprintManagerCompat.from(getActivity());

        unlockMethod = (ListPreference) findPreference(getString(R.string.unlock_method));
        unlockMethodChosen = GlobalUtils.getStringArray(getActivity(), R.array.unlock_methods);

        if (fingerprintManagerCompat.isHardwareDetected()) {
            Preference fingerprintSettings = findPreference(getString(R.string.fingerprint_settings));
            fingerprintSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Navigator.with(getActivity()).build().goTo(FingerprintSettingsActivity.class).animation().commit();
                    return false;
                }
            });
        } else {
            preferenceCategory.removePreference(preferenceCategory.getPreference(0));

            unlockMethod.setEntries(R.array.unlock_methods_without_fingerprint);
            unlockMethod.setEntryValues(R.array.unlock_methods_values_without_fingerprint);
        }

        final Preference patternSettings = findPreference(getString(R.string.pattern_settings));
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

        SwitchPreference blockAppsAfterScreenOff = (SwitchPreference) findPreference(getString(R.string.block_apps_after_screen_off));
        blockAppsAfterScreenOff.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean blockApps = Boolean.parseBoolean(String.valueOf(newValue));

                PreferencesManager.putBoolean(getString(R.string.block_apps_after_screen_off), blockApps);

                return true;
            }
        });

        SwitchPreference enableSwipeOnLockScreen = (SwitchPreference) findPreference(getString(R.string.enable_swipe_on_lock_screen));
        enableSwipeOnLockScreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean enableSwipe = Boolean.parseBoolean(String.valueOf(newValue));

                PreferencesManager.putBoolean(getString(R.string.enable_swipe_on_lock_screen), enableSwipe);

                return true;
            }
        });

        SwitchPreference iconOnAppDrawer = (SwitchPreference) findPreference(getString(R.string.icon_on_app_drawer));
        iconOnAppDrawer.setIcon(iconOnAppDrawer.isChecked() ? R.drawable.ic_visibility : R.drawable.ic_visibility_off);
        iconOnAppDrawer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean showIcon = Boolean.parseBoolean(String.valueOf(newValue));

                preference.setIcon(showIcon ? R.drawable.ic_visibility : R.drawable.ic_visibility_off);

                return true;
            }
        });

        SwitchPreference notificationOnStatusBar = (SwitchPreference) findPreference(getString(R.string.notification_on_status_bar));
        notificationOnStatusBar.setIcon(notificationOnStatusBar.isChecked() ? R.drawable.ic_notifications_active : R.drawable.ic_notifications_off);
        notificationOnStatusBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean showNotification = Boolean.parseBoolean(String.valueOf(newValue));

                preference.setIcon(showNotification ? R.drawable.ic_notifications_active : R.drawable.ic_notifications_off);

                return true;
            }
        });

        Preference fontSettings = findPreference(getString(R.string.font_settings));
        fontSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Navigator.with(getActivity()).build().goTo(FontSettingsActivity.class).animation().commit();
                return false;
            }
        });

        Preference wallPaperSettings = findPreference(getString(R.string.wallpaper_settings));
        wallPaperSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Navigator.with(getActivity()).build().goTo(WallpaperSettingsActivity.class).animation().commit();
                return false;
            }
        });

        Preference openSourceLic = findPreference(getString(R.string.open_source_lic));
        openSourceLic.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Navigator.with(getActivity()).build().goTo(LicensesActivity.class).animation().commit();
                return false;
            }
        });

        Preference sendBugReport = findPreference(getString(R.string.send_bug_report));
        sendBugReport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                EmailIntentBuilder.from(getActivity())
                        .to(getString(R.string.support_email))
                        .subject(String.format(getString(R.string.bug_report_subject), getString(R.string.app_name), PackageUtils.getAppVersionName((getActivity()))))
                        .start();
                return false;
            }
        });

        Preference aboutMe = findPreference(getString(R.string.about_me));
        aboutMe.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri webPage = Uri.parse("https://plus.google.com/u/0/108168960305991028461/posts");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
                startActivity(webIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                return false;
            }
        });

        Preference checkForUpdate = findPreference(getString(R.string.check_for_update));
        checkForUpdate.setSummary("v" + PackageUtils.getAppVersionName(getActivity()));
        checkForUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        unlockMethodIndex = PreferencesManager.getInt(getString(R.string.unlock_method));

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

        if (fingerprintManagerCompat.isHardwareDetected()) {
            unlockMethod.setValueIndex(unlockMethodIndex);
        } else {
            unlockMethod.setValueIndex(unlockMethodIndex - 1);
        }

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
                unlockMethod.setValueIndex(unlockMethodIndex);
            }
        } else if (index.equals(UAppLock.PATTERN)) {
            if (patternWasConfigured()) {
                update(index);
            } else {
                Navigator.with(getActivity()).build().goTo(PatternSettingsActivity.class).animation().commit();
            }
        } else {
            if (pinWasConfigured()) {
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
        return PreferencesManager.getString(getString(R.string.user_pin));
    }

    public Boolean patternWasConfigured() {
        return userPattern().length() != 0;
    }

    public String userPattern() {
        return PreferencesManager.getString(getString(R.string.user_pattern));
    }

    public Boolean isFingerPrintActivated() {
        return PreferencesManager.getBoolean(getString(R.string.fingerprint_recognition_activated));
    }

    public void update(Integer index) {
        unlockMethod.setSummary(String.format(getString(R.string.unlock_method_chosen), unlockMethodChosen[index]));

        PreferencesManager.putInt(getString(R.string.unlock_method), index);
    }
}

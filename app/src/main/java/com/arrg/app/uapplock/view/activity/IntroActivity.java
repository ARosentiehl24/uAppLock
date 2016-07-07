package com.arrg.app.uapplock.view.activity;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.IntroActivityView;
import com.arrg.app.uapplock.presenter.IIntroActivityPresenter;
import com.arrg.app.uapplock.util.UsageStatsUtil;
import com.arrg.app.uapplock.view.adapter.SectionsPagerAdapter;
import com.arrg.app.uapplock.view.adapter.SmartFragmentStatePagerAdapter;
import com.arrg.app.uapplock.view.fragment.EnableFingerprintSupportFragment;
import com.arrg.app.uapplock.view.fragment.RequestPatternFragment;
import com.arrg.app.uapplock.view.fragment.RequestPermissionsFragment;
import com.arrg.app.uapplock.view.fragment.RequestPinFragment;
import com.arrg.app.uapplock.view.ui.LockableViewPager;
import com.commit451.inkpageindicator.InkPageIndicator;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.fingerlinks.mobile.android.navigator.Navigator;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroActivity extends UAppLockActivity implements IntroActivityView, ViewPager.OnPageChangeListener {

    public static final int USAGE_STATS_RC = 100;
    public static final int OVERLAY_PERMISSION_RC = 101;
    public static final int WRITE_SETTINGS_RC = 102;

    @Bind(R.id.viewPager)
    LockableViewPager viewPager;
    @Bind(R.id.btnPrevious)
    MaterialIconView btnPrevious;
    @Bind(R.id.btnNext)
    MaterialIconView btnNext;
    @Bind(R.id.inkPageIndicator)
    InkPageIndicator inkPageIndicator;

    private ArrayList<Boolean> list;
    private FingerprintManagerCompat fingerprintManagerCompat;
    private IIntroActivityPresenter iIntroActivityPresenter;
    private SmartFragmentStatePagerAdapter smartFragmentStatePagerAdapter;
    private Integer unlockMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);

        iIntroActivityPresenter = new IIntroActivityPresenter(this);
        iIntroActivityPresenter.onCreate();
    }

    @Override
    public void onBackPressed() {
        Navigator.with(this).utils().finishWithAnimation();
    }

    @Override
    public void setupViews() {
        fingerprintManagerCompat = FingerprintManagerCompat.from(this);

        ArrayList<Fragment> fragments = new ArrayList<>();
        list = new ArrayList<>();

        unlockMethod = PreferencesManager.getInt(getString(R.string.unlock_method));

        if (unlockMethod.equals(UAppLock.PATTERN)) {
            fragments.add(RequestPatternFragment.newInstance());
        } else {
            fragments.add(RequestPinFragment.newInstance());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fingerprintManagerCompat.isHardwareDetected()) {
                fragments.add(EnableFingerprintSupportFragment.newInstance());
            }

            if (!usageStatsIsNotEmpty()) {
                fragments.add(RequestPermissionsFragment.newInstance(R.string.usage_stats_permission, R.drawable.ic_usage_stats, R.string.usage_stats_permission_description, USAGE_STATS_RC));
            }

            if (!overlayPermissionGranted()) {
                fragments.add(RequestPermissionsFragment.newInstance(R.string.overlay_permission, R.drawable.ic_overlay_permission, R.string.overlay_permission_description, OVERLAY_PERMISSION_RC));
            }

            if (!writeSettingsPermissionGranted()) {
                fragments.add(RequestPermissionsFragment.newInstance(R.string.write_settings_permission, R.drawable.ic_write_settings_permission, R.string.write_settings_permission_description, WRITE_SETTINGS_RC));
            }
        } else {
            if (isHardwareDetected()) {
                fragments.add(EnableFingerprintSupportFragment.newInstance());
            }
        }

        smartFragmentStatePagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(smartFragmentStatePagerAdapter);
        viewPager.setSwipeable(false);

        inkPageIndicator.setViewPager(viewPager);
    }

    @Override
    public void next() {
        if (viewPager.getCurrentItem() == smartFragmentStatePagerAdapter.getCount() - 1) {
            if (allSettingsAndPermissionsAreReady()) {
                SplashScreenActivity.splashScreenActivity.finish();

                PreferencesManager.putBoolean(getString(R.string.all_settings_are_complete), true);

                Navigator.with(this).build().goTo(AppListActivity.class).animation().commit();
                finish();
            } else {
                if ((!patternWasConfigured() && unlockMethod.equals(UAppLock.PATTERN)) || (!pinWasConfigured() && unlockMethod.equals(UAppLock.PIN))) {
                    viewPager.setCurrentItem(0);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (isHardwareDetected()) {
                            if (!hasEnrolledFingerprints()) {
                                viewPager.setCurrentItem(1);
                            } else if (!usageStatsIsNotEmpty()) {
                                viewPager.setCurrentItem(2);
                            } else if (!overlayPermissionGranted()) {
                                viewPager.setCurrentItem(3);
                            } else if (!writeSettingsPermissionGranted()) {
                                viewPager.setCurrentItem(4);
                            }
                        } else {
                            if (!usageStatsIsNotEmpty()) {
                                viewPager.setCurrentItem(1);
                            } else if (!overlayPermissionGranted()) {
                                viewPager.setCurrentItem(2);
                            } else if (!writeSettingsPermissionGranted()) {
                                viewPager.setCurrentItem(3);
                            }
                        }
                    } else {
                        if (!hasEnrolledFingerprints()) {
                            viewPager.setCurrentItem(1);
                        }
                    }
                }
            }
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    @Override
    public void previous() {
        if (viewPager.getCurrentItem() >= 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void hideNext() {
        btnNext.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hidePrevious() {
        btnPrevious.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNext() {
        btnNext.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPrevious() {
        btnPrevious.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isHardwareDetected() {
        return fingerprintManagerCompat.isHardwareDetected();
    }

    @Override
    public boolean hasEnrolledFingerprints() {
        return fingerprintManagerCompat.hasEnrolledFingerprints();
    }

    @Override
    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.btnPrevious, R.id.btnNext})
    public void onClick(View view) {
        iIntroActivityPresenter.onButtonBarClick(view.getId());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        iIntroActivityPresenter.configureTheViewsAccordingPageSelected(position, viewPager.getChildCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void checkForValues() {
        list.clear();

        if (unlockMethod.equals(UAppLock.PATTERN)) {
            list.add(patternWasConfigured());
            Log.e("Values", "patternWasConfigured: " + patternWasConfigured());
        } else {
            list.add(pinWasConfigured());
            Log.e("Values", "pinWasConfigured: " + pinWasConfigured());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isHardwareDetected() && !hasEnrolledFingerprints()) {
                list.add(hasEnrolledFingerprints());
                Log.e("Values", "hasEnrolledFingerprints: " + hasEnrolledFingerprints());
            }
            list.add(usageStatsIsNotEmpty());
            Log.e("Values", "usageStatsIsNotEmpty: " + usageStatsIsNotEmpty());

            list.add(overlayPermissionGranted());
            Log.e("Values", "overlayPermissionGranted: " + overlayPermissionGranted());

            list.add(writeSettingsPermissionGranted());
            Log.e("Values", "writeSettingsPermissionGranted: " + writeSettingsPermissionGranted());
        } else {
            if (fingerprintManagerCompat.isHardwareDetected()) {
                if (!hasEnrolledFingerprints()) {
                    list.add(hasEnrolledFingerprints());
                    Log.e("Values", "hasEnrolledFingerprints: " + hasEnrolledFingerprints());
                }
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

    public Boolean usageStatsIsNotEmpty() {
        return !UsageStatsUtil.getUsageStatsList(this).isEmpty();
    }

    public Boolean overlayPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (Settings.canDrawOverlays(this));
        } else {
            return true;
        }
    }

    public Boolean writeSettingsPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(this);
        } else {
            return true;
        }
    }

    public Boolean allSettingsAndPermissionsAreReady() {
        checkForValues();

        return !list.contains(false);
    }
}

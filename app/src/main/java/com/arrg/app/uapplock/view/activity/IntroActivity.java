package com.arrg.app.uapplock.view.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.IntroActivityView;
import com.arrg.app.uapplock.model.service.UAppLockService;
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
import com.shawnlin.preferencesmanager.PreferencesManager;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.fingerlinks.mobile.android.navigator.Navigator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroActivity extends UAppLockActivity implements IntroActivityView, ViewPager.OnPageChangeListener {

    public static final int USAGE_STATS_RC = 100;
    public static final int OVERLAY_PERMISSION_RC = 101;
    public static final int WRITE_SETTINGS_RC = 102;
    public static final int ACCESSIBILITY_SERVICES_RC = 103;

    @BindView(R.id.viewPager)
    LockableViewPager viewPager;
    @BindView(R.id.btnPrevious)
    MaterialIconView btnPrevious;
    @BindView(R.id.btnNext)
    MaterialIconView btnNext;
    @BindView(R.id.inkPageIndicator)
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

        iIntroActivityPresenter = new IIntroActivityPresenter(this);
        iIntroActivityPresenter.onCreate();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

        if (unlockMethod.equals(UAppLock.PATTERN) && !patternWasConfigured()) {
            fragments.add(RequestPatternFragment.newInstance());
        } else if (unlockMethod.equals(UAppLock.PIN) && !pinWasConfigured()){
            fragments.add(RequestPinFragment.newInstance());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasEnrolledFingerprints()) {
                fragments.add(EnableFingerprintSupportFragment.newInstance());
            }

            if (!usageStatsIsNotEmpty()) {
                fragments.add(RequestPermissionsFragment.newInstance(R.string.usage_stats_permission, R.drawable.ic_picture_usage_stats, R.string.usage_stats_permission_description, USAGE_STATS_RC));
            }

            if (!overlayPermissionGranted()) {
                fragments.add(RequestPermissionsFragment.newInstance(R.string.overlay_permission, R.drawable.ic_picture_overlay_permission, R.string.overlay_permission_description, OVERLAY_PERMISSION_RC));
            }

            if (!isAccessibilitySettingsOn(this)) {
                fragments.add(RequestPermissionsFragment.newInstance(R.string.accessibility_service, R.drawable.ic_picture_accessibility_service, R.string.accessibility_service_description, ACCESSIBILITY_SERVICES_RC));
            }
        } else {
            if (!hasEnrolledFingerprints()) {
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

                Navigator.with(this).build().goTo(ApplicationListActivity.class).animation().commit();

                finish();
            } else {
                if ((!patternWasConfigured() && unlockMethod.equals(UAppLock.PATTERN)) || (!pinWasConfigured() && unlockMethod.equals(UAppLock.PIN))) {
                    viewPager.setCurrentItem(0);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    } else {

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
                Log.e("Values", "hasEnrolledFingerprints: " + hasEnrolledFingerprints());
            }
            list.add(usageStatsIsNotEmpty());
            Log.e("Values", "usageStatsIsNotEmpty: " + usageStatsIsNotEmpty());

            list.add(overlayPermissionGranted());
            Log.e("Values", "overlayPermissionGranted: " + overlayPermissionGranted());

            list.add(isAccessibilitySettingsOn(this));
            Log.e("Values", "isAccessibilitySettingsOn: " + isAccessibilitySettingsOn(this));
        } else {
            if (fingerprintManagerCompat.isHardwareDetected()) {
                if (!hasEnrolledFingerprints()) {
                    Log.e("Values", "hasEnrolledFingerprints: " + hasEnrolledFingerprints());
                }
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

    private boolean isAccessibilitySettingsOn(Context context) {
        Integer accessibilityEnabled = 0;
        String service = String.format("%s/%s", getPackageName(), UAppLockService.class.getCanonicalName());

        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(getClass().getSimpleName(), "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.v(getClass().getSimpleName(), "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(getClass().getSimpleName(), "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                simpleStringSplitter.setString(settingValue);

                while (simpleStringSplitter.hasNext()) {
                    String accessibilityService = simpleStringSplitter.next();

                    Log.v(getClass().getSimpleName(), "-------------- > accessibilityService :: " + accessibilityService);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(getClass().getSimpleName(), "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(getClass().getSimpleName(), "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    public Boolean allSettingsAndPermissionsAreReady() {
        checkForValues();

        return !list.contains(false);
    }
}

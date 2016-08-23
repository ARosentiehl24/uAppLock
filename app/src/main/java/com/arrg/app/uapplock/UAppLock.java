package com.arrg.app.uapplock;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.arrg.app.uapplock.util.kisstools.KissTools;
import com.arrg.app.uapplock.util.kisstools.utils.ResourceUtil;
import com.shawnlin.preferencesmanager.PreferencesManager;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UAppLock extends Application {


    public static UAppLock uAppLock;
    public static Integer DURATIONS_OF_ANIMATIONS = 250;

    public static String PACKAGE_NAME;

    public static String ALIAS_CLASSNAME;

    public static String LOCKED_APPS_PREFERENCES;
    public static String PACKAGES_APPS_PREFERENCES;
    public static String SETTINGS_PREFERENCES;

    public static String ACTION_SHOW_APPLICATION;
    public static String ACTION_HIDE_APPLICATION;
    public static String ACTION_SHOW_NOTIFICATION;
    public static String ACTION_HIDE_NOTIFICATION;
    public static String ACTION_RESTART_SERVICE;

    public static Integer FINGERPRINT = 0;
    public static Integer PATTERN = 1;
    public static Integer PIN = 2;

    private PreferencesManager preferencesManager;

    @Override
    public void onCreate() {
        super.onCreate();

        uAppLock = this;

        PACKAGE_NAME = getPackageName().toUpperCase();

        ALIAS_CLASSNAME = getPackageName() + ".view.activity.SplashScreenActivityAlias";

        LOCKED_APPS_PREFERENCES = PACKAGE_NAME + ".LOCKED_APPS";
        PACKAGES_APPS_PREFERENCES = PACKAGE_NAME + ".PACKAGES_APPS";
        SETTINGS_PREFERENCES = PACKAGE_NAME + ".SETTINGS";

        ACTION_SHOW_APPLICATION = PACKAGE_NAME + ".SHOW_APPLICATION";
        ACTION_HIDE_APPLICATION = PACKAGE_NAME + ".HIDE_APPLICATION";
        ACTION_SHOW_NOTIFICATION = PACKAGE_NAME + ".SHOW_NOTIFICATION";
        ACTION_HIDE_NOTIFICATION = PACKAGE_NAME + ".HIDE_NOTIFICATION";
        ACTION_RESTART_SERVICE = PACKAGE_NAME + ".RESTART_SERVICE";

        preferencesManager = new PreferencesManager(this);
        setPreferencesManager(SETTINGS_PREFERENCES);

        String fontPath = PreferencesManager.getString(getString(R.string.font_path), "fonts/Raleway.ttf");
        setAppFontTo(fontPath);

        KissTools.setContext(getApplicationContext());
    }

    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    public void setPreferencesManager(String name) {
        preferencesManager.setName(name);
        preferencesManager.setMode(Context.MODE_PRIVATE);
        preferencesManager.init();
    }

    public void setAppFontTo(String fontPath) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(fontPath)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public static String fontPath() {
        return PreferencesManager.getString(ResourceUtil.getString(R.string.font_path), "fonts/Raleway.ttf");
    }

    public static Typeface typeface() {
        return Typeface.createFromAsset(uAppLock.getAssets(), fontPath());
    }
}

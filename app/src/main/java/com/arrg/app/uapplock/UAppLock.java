package com.arrg.app.uapplock;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.preference.Preference;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.arrg.app.uapplock.model.UTypefaceSpan;
import com.arrg.app.uapplock.util.kisstools.KissTools;
import com.arrg.app.uapplock.util.kisstools.utils.ResourceUtil;
import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import java.util.ArrayList;

public class UAppLock extends Application {
    public static UAppLock uAppLock;
    public static Integer DURATIONS_OF_ANIMATIONS = 250;

    public static String PACKAGE_NAME;
    public static String LOCKED_APPS_PREFERENCES;
    public static String PACKAGES_APPS_PREFERENCES;
    public static String SETTINGS_PREFERENCES;

    public static Integer FINGERPRINT = 0;
    public static Integer PATTERN = 1;
    public static Integer PIN = 2;

    private ArrayList<TypefaceCollection> typefaceCollections;
    private PreferencesManager preferencesManager;

    @Override
    public void onCreate() {
        super.onCreate();

        uAppLock = this;

        PACKAGE_NAME = getPackageName().toUpperCase();

        LOCKED_APPS_PREFERENCES = PACKAGE_NAME + ".LOCKED_APPS";
        PACKAGES_APPS_PREFERENCES = PACKAGE_NAME + ".PACKAGES_APPS";
        SETTINGS_PREFERENCES = PACKAGE_NAME + ".SETTINGS";

        preferencesManager = new PreferencesManager(this);
        setPreferencesManager(SETTINGS_PREFERENCES);

        typefaceCollections = new ArrayList<>();
        typefaceCollections.add(new TypefaceCollection.Builder().set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/Raleway.ttf")).create());
        typefaceCollections.add(new TypefaceCollection.Builder().set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/LazySpringDay.ttf")).create());

        int fontPosition = PreferencesManager.getInt(getString(R.string.font_position), 0);

        initTypeFace(getTypeface(fontPosition));

        KissTools.setContext(getApplicationContext());
    }

    public void setPreferencesManager(String name) {
        preferencesManager.setName(name);
        preferencesManager.setMode(Context.MODE_PRIVATE);
        preferencesManager.init();
    }

    public TypefaceCollection getTypeface(int index) {
        return typefaceCollections.get(index);
    }

    public void initTypeFace(TypefaceCollection typefaceCollection) {
        TypefaceHelper.init(typefaceCollection);
    }

    public void setFontTo(Preference preference) {
        String fontPath = PreferencesManager.getString(ResourceUtil.getString(R.string.font_path), "fonts/Raleway.ttf");

        Typeface typeface = Typeface.createFromAsset(getAssets(), fontPath);

        UTypefaceSpan customTypefaceSpan = new UTypefaceSpan("", typeface);

        SpannableStringBuilder spannableStringBuilder;

        if (preference.getTitle() != null) {
            spannableStringBuilder = new SpannableStringBuilder(preference.getTitle().toString());
            spannableStringBuilder.setSpan(customTypefaceSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            preference.setTitle(spannableStringBuilder);
        }

        if (preference.getSummary() != null) {
            spannableStringBuilder = new SpannableStringBuilder(preference.getSummary().toString());
            spannableStringBuilder.setSpan(customTypefaceSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            preference.setSummary(spannableStringBuilder);
        }
    }
}

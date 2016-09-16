package com.arrg.app.uapplock.model.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.UAppLockServiceView;
import com.arrg.app.uapplock.model.runnable.PackagesMonitor;
import com.arrg.app.uapplock.model.runnable.UpdatesMonitor;
import com.arrg.app.uapplock.presenter.IUAppLockServicePresenter;
import com.arrg.app.uapplock.util.SharedPreferencesUtil;
import com.arrg.app.uapplock.view.activity.SplashScreenActivity;
import com.shawnlin.preferencesmanager.PreferencesManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class UAppLockService extends AccessibilityService implements UAppLockServiceView {

    public static UAppLockService SERVICE;
    public static final Integer NOTIFICATION_ID = 150;

    private Boolean lockAppsAfterScreenOff;
    private Handler packagesHandler;
    private Handler updatesHandler;
    private IUAppLockServicePresenter iuAppLockServicePresenter;
    private HashMap<String, Boolean> lockedPackages;
    private HashMap<String, Runnable> unlockMap;
    private Integer lastEventType = 0;
    private PackagesMonitor packagesMonitor;
    private UpdatesMonitor updatesMonitor;
    private SharedPreferences lockedAppsPreferences;
    private SharedPreferences packagesAppsPreferences;
    private SharedPreferencesUtil preferencesUtil;
    private String lastPackageOnTop = "";

    public UAppLockService() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (lastEventType != accessibilityEvent.getEventType()) {

            String className;
            String packageName;

            try {
                packageName = (String) accessibilityEvent.getPackageName();
                className = (String) accessibilityEvent.getClassName();

                ActivityInfo activityInfo = tryToGetActivity(new ComponentName(packageName, className));
                Boolean isActivity = activityInfo != null;

                if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    if (isActivity) {
                        handlePackageOnTop(packageName, false, false);
                    }
                } else if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                    if (appIsLocked(packageName) && !packageName.equals("com.android.systemui") && className.equals("android.widget.FrameLayout") && !packageName.equals(lastPackageOnTop)) {
                        if (!isActivity) {
                            if (isRunning(this, LockScreenService.class)) {
                                handlePackageOnTop(packageName, true, false);
                            }
                        }
                    } else if (!appIsLocked(packageName) && !packageName.equals("com.android.systemui") && className.equals("android.widget.FrameLayout") && !packageName.equals(getPackageName()) && !packageName.equals(lastPackageOnTop)) {
                        if (!isActivity) {
                            if (isRunning(this, LockScreenService.class)) {
                                handlePackageOnTop(packageName, false, true);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }

            lastEventType = accessibilityEvent.getEventType();
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(getClass().getSimpleName(), "onInterrupt()");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        accessibilityServiceInfo.notificationTimeout = 0;
        setServiceInfo(accessibilityServiceInfo);
    }

    private ActivityInfo tryToGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(getClass().getSimpleName(), "onCreate()");

        SERVICE = this;

        iuAppLockServicePresenter = new IUAppLockServicePresenter(this);
        iuAppLockServicePresenter.onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getClass().getSimpleName(), "onStartCommand()");

        loopPreferences();

        return START_STICKY /*super.onStartCommand(intent, flags, startId)*/;
    }

    @Override
    public void onLowMemory() {
        Log.i(getClass().getSimpleName(), "OnLowMemory()");

        super.onLowMemory();

        //iuAppLockServicePresenter.unregisterReceivers();
        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(getClass().getSimpleName(), "onTaskRemoved()");

        super.onTaskRemoved(rootIntent);

        //iuAppLockServicePresenter.unregisterReceivers();
        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    @Override
    public void onDestroy() {
        Log.i(getClass().getSimpleName(), "onDestroy()");

        super.onDestroy();

        //iuAppLockServicePresenter.unregisterReceivers();
        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    @Override
    public void init() {
        preferencesUtil = new SharedPreferencesUtil(this);
        lockedAppsPreferences = getSharedPreferences(UAppLock.LOCKED_APPS_PREFERENCES, Context.MODE_PRIVATE);
        packagesAppsPreferences = getSharedPreferences(UAppLock.PACKAGES_APPS_PREFERENCES, Context.MODE_PRIVATE);

        lockAppsAfterScreenOff = PreferencesManager.getBoolean(getString(R.string.block_apps_after_screen_off));

        lockedPackages = new HashMap<>();
        unlockMap = new HashMap<>();

        updatesHandler = new Handler();
        updatesMonitor = new UpdatesMonitor(this);
        updatesMonitor.run();
    }

    @Override
    public void notificationHandler() {
        Boolean showNotification = PreferencesManager.getBoolean(getString(R.string.notification_on_status_bar), true);

        if (showNotification) {
            Intent intent = new Intent(UAppLock.ACTION_SHOW_NOTIFICATION);
            sendBroadcast(intent);
        } else {
            Intent intent = new Intent(UAppLock.ACTION_HIDE_NOTIFICATION);
            sendBroadcast(intent);
        }
    }

    @Override
    public void startForeground() {
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        Uri uri = Uri.fromParts("package", UAppLock.PACKAGE_NAME.toLowerCase(), null);
        notificationIntent.setData(uri);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashScreenActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat
                .Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_fingerprint_white))
                .setSmallIcon(R.drawable.ic_fingerprint_white)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTitle(getString(R.string.title_notification))
                .setContentText(getString(R.string.text_notification))
                .setContentIntent(notificationPendingIntent)
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void run() {

    }

    @Override
    public String getTopPackage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

            long time = System.currentTimeMillis();

            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, (long) (time - 1000 * 2.5), time);

            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }

                if (!mySortedMap.isEmpty()) {
                    Log.e("TAG", mySortedMap.get(mySortedMap.lastKey()).getPackageName());

                    return mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcesses) {
                if (appProcessInfo.pkgList.length == 1) {
                    return appProcessInfo.pkgList[0];
                }
            }
        }

        return "";
    }

    @Override
    public void handlePackageOnTop(String packageOnTop, Boolean update, Boolean close) {
        if (packageOnTop.length() != 0) {
            if (!lastPackageOnTop.equals(packageOnTop)) {
                lockAppsAfterScreenOff = PreferencesManager.getBoolean(getString(R.string.block_apps_after_screen_off));

                Log.e("packageOnTop", "---------------------------------------------------------");
                Log.d("packageOnTop", packageOnTop);
                Log.d("packageOnTop", "Close: " + lastPackageOnTop + " to Open: " + packageOnTop);

                lockApp(lastPackageOnTop);

                if (update) {
                    LockScreenService.LOCK_SCREEN.configLockScreen(packageOnTop);
                } else if (close) {
                    LockScreenService.LOCK_SCREEN.finish();
                } else {
                    if (appIsLocked(packageOnTop) || packageOnTop.equals(getPackageName())) {
                        startService(LockScreenService.lockPackage(this, packageOnTop));
                    /*Intent lockScreenIntent = new Intent(this, LockScreenActivity.class);
                    lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lockScreenIntent.putExtra(UAppLock.EXTRA_PACKAGE_NAME, packageOnTop);
                    getApplicationContext().startActivity(lockScreenIntent);*/
                    }
                }


                lastPackageOnTop = packageOnTop;

                Log.e("packageOnTop", "---------------------------------------------------------");
            }
        }
    }

    @Override
    public void unlockApp(String packageOnTop) {
        if (lockedAppsPreferences.contains(packageOnTop)) {
            lockedPackages.put(packageOnTop, false);
        }
    }

    @Override
    public void lockApp(String packageOnTop) {
        if (!lockAppsAfterScreenOff) {
            if (lockedAppsPreferences.contains(packageOnTop)) {
                lockedPackages.put(packageOnTop, true);
            }
        }
    }

    @Override
    public void lockAllApps() {
        for (Map.Entry<String, Boolean> entry : lockedPackages.entrySet()) {
            if (lockedAppsPreferences.contains(entry.getKey())) {
                entry.setValue(true);

                Log.d("packageOnTop", "Package: " + entry.getKey());
            }
        }
    }

    public void loopPreferences() {
        Map<String, ?> keys = lockedAppsPreferences.getAll();

        lockedPackages.clear();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            lockedPackages.put(entry.getKey(), (Boolean) entry.getValue());

            Log.d(getClass().getSimpleName(), entry.getKey() + " : " + entry.getValue().toString());
        }
    }

    public boolean appIsLocked(String appPackage) {
        return preferencesUtil.getBoolean(lockedAppsPreferences, appPackage, false);
    }

    public static boolean isRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

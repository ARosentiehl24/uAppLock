package com.arrg.app.uapplock.model.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.util.SortedMap;
import java.util.TreeMap;

public class UAppLockService extends AccessibilityService implements UAppLockServiceView {

    public static UAppLockService SERVICE;
    public static final Integer NOTIFICATION_ID = 150;

    private Handler packagesHandler;
    private Handler updatesHandler;
    private IUAppLockServicePresenter iuAppLockServicePresenter;
    private HashMap<String, Boolean> lockedPackages;
    private HashMap<String, Runnable> unlockMap;
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

        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            stopMonitor();
            Log.e("AccessibilityEvent", String.valueOf(accessibilityEvent.getPackageName()));
            startMonitor();
        }

        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (accessibilityEvent.getPackageName().equals("com.facebook.orca")) {
                Log.e("AccessibilityEvent", String.valueOf(accessibilityEvent.getPackageName()));
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        accessibilityServiceInfo.notificationTimeout = 50;
        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(getClass().getCanonicalName(), "onCreate()");

        SERVICE = this;

        iuAppLockServicePresenter = new IUAppLockServicePresenter(this);
        iuAppLockServicePresenter.onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getClass().getName(), "onStartCommand()");

        return START_STICKY /*super.onStartCommand(intent, flags, startId)*/;
    }

    @Override
    public void onLowMemory() {
        Log.i(getClass().getName(), "OnLowMemory()");

        super.onLowMemory();

        //iuAppLockServicePresenter.unregisterReceivers();
        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(getClass().getName(), "onTaskRemoved()");

        super.onTaskRemoved(rootIntent);

        //iuAppLockServicePresenter.unregisterReceivers();
        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    @Override
    public void onDestroy() {
        Log.i(getClass().getName(), "onDestroy()");

        super.onDestroy();

        //iuAppLockServicePresenter.unregisterReceivers();
        iuAppLockServicePresenter.restartServiceIfNeeded();
    }

    @Override
    public void init() {
        preferencesUtil = new SharedPreferencesUtil(this);
        lockedAppsPreferences = getSharedPreferences(UAppLock.LOCKED_APPS_PREFERENCES, Context.MODE_PRIVATE);
        packagesAppsPreferences = getSharedPreferences(UAppLock.PACKAGES_APPS_PREFERENCES, Context.MODE_PRIVATE);

        lockedPackages = new HashMap<>();
        unlockMap = new HashMap<>();

        packagesHandler = new Handler();
        packagesMonitor = new PackagesMonitor(this);
        packagesMonitor.run();

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
    public void startMonitor() {
        packagesHandler.post(packagesMonitor);
    }

    @Override
    public void stopMonitor() {
        packagesHandler.removeCallbacks(packagesMonitor);
    }

    @Override
    public void run() {
        handlePackageOnTop(getTopPackageName());
        packagesHandler.postDelayed(packagesMonitor, 100);
    }

    @Override
    public void handlePackageOnTop(String packageOnTop) {
        if (packageOnTop.length() != 0) {
            if (!lastPackageOnTop.equals(packageOnTop)) {
                Log.d("packageOnTop", packageOnTop);

                lastPackageOnTop = packageOnTop;
            }
        }
    }

    @Override
    public String getTopPackageName() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        long time = System.currentTimeMillis();

        List<UsageStats> statsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, (long) (time - 1000 * 2.5), time);

        if (statsList != null) {
            SortedMap<Long, UsageStats> usageStatsTreeMap = new TreeMap<>();
            for (UsageStats usageStats : statsList) {
                usageStatsTreeMap.put(usageStats.getLastTimeUsed(), usageStats);
            }

            if (!usageStatsTreeMap.isEmpty()) {
                return usageStatsTreeMap.get(usageStatsTreeMap.lastKey()).getPackageName();
            }
        }

        return "";
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

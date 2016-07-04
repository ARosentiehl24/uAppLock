package com.arrg.app.uapplock.util;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UsageStatsUtil {
    public static final String TAG = UsageStatsUtil.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss", Locale.US);

    @SuppressWarnings("ResourceType")
    public static void getStats(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");

        int interval = UsageStatsManager.INTERVAL_YEARLY;

        Calendar calendar = Calendar.getInstance();

        long endTime = calendar.getTimeInMillis();

        calendar.add(Calendar.YEAR, -1);

        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        UsageEvents uEvents = usm.queryEvents(startTime, endTime);

        while (uEvents.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);

            Log.d(TAG, "Event: " + e.getPackageName() + "\t" + e.getTimeStamp());
        }
    }

    public static List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();

        long endTime = calendar.getTimeInMillis();

        calendar.add(Calendar.YEAR, -1);

        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
    }

    public static void printUsageStats(List<UsageStats> usageStatsList) {
        for (UsageStats usageStats : usageStatsList) {
            Log.d(TAG, "Pkg: " + usageStats.getPackageName() + "\t" + "ForegroundTime: " + usageStats.getTotalTimeInForeground());
        }
    }

    public static void printCurrentUsageStatus(Context context) {
        printUsageStats(getUsageStatsList(context));
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context) {
        return (UsageStatsManager) context.getSystemService("usagestats");
    }
}

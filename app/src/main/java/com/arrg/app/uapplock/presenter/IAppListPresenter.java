package com.arrg.app.uapplock.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.AppListPresenter;
import com.arrg.app.uapplock.interfaces.AppListView;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.util.SharedPreferencesUtil;
import com.arrg.app.uapplock.util.kisstools.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.arrg.app.uapplock.UAppLock.LOCKED_APPS_PREFERENCES;

public class IAppListPresenter implements AppListPresenter {

    private AppListView appListView;
    private SharedPreferences lockedAppsPreferences;
    private SharedPreferencesUtil preferencesUtil;

    public IAppListPresenter(AppListView appListView) {
        this.appListView = appListView;
    }

    @Override
    public void onCreate() {
        preferencesUtil = new SharedPreferencesUtil(getContext());
        lockedAppsPreferences = getContext().getSharedPreferences(LOCKED_APPS_PREFERENCES, Context.MODE_PRIVATE);

        appListView.setupViews();
    }

    @Override
    public void onClick() {
        appListView.hideSearch();
    }

    @Override
    public ArrayList<App> lockedApps(ArrayList<App> apps) {
        ArrayList<App> lockedApps = new ArrayList<>();

        for (App app : apps) {
            if (preferencesUtil.getBoolean(lockedAppsPreferences, app.getAppPackage(), false)) {
                lockedApps.add(app);
            }
        }

        return lockedApps;
    }

    @Override
    public ArrayList<App> unlockedApps(ArrayList<App> apps) {
        ArrayList<App> unlockedApps = new ArrayList<>();

        for (App app : apps) {
            if (!preferencesUtil.getBoolean(lockedAppsPreferences, app.getAppPackage(), false)) {
                unlockedApps.add(app);
            }
        }

        return unlockedApps;
    }

    @Override
    public Context getContext() {
        return appListView.getContext();
    }

    @Override
    public void updateAppWith(ArrayList<App> apps, App newApp, boolean checked) {
        Integer position = 0;

        if (checked) {
            if (apps.size() == 0) {
                appListView.add(newApp, apps.size());
            } else {
                Boolean sw = false;

                for (App app : apps) {
                    if (app.getAppName().compareToIgnoreCase(newApp.getAppName()) > 1) {
                        if (position - 1 >= 0) {
                            LogUtil.e("OnClick", "Add App > 1 in: " + apps.get(position - 1).getAppName() + " --> " + newApp.getAppName() + " <-- " + apps.get(position).getAppName());
                        } else {
                            LogUtil.e("OnClick", "Add App > 1 in: " + newApp.getAppName() + " <-- " + apps.get(position).getAppName());
                        }

                        appListView.add(newApp, position);

                        break;
                    } else {
                        LogUtil.e("OnClick", "App in: " + apps.get(position).getAppName());

                        sw = true;
                    }

                    position++;
                }

                if (sw) {
                    if (!apps.contains(newApp)) {
                        appListView.add(newApp, apps.size());
                    }
                }
            }
        } else {
            for (App app : apps) {
                if (app.getAppPackage().equals(newApp.getAppPackage())) {
                    LogUtil.e("OnClick", "Removing: " + apps.get(position).getAppName());

                    appListView.remove(position);

                    break;
                } else {
                    LogUtil.e("OnClick", "App in: " + apps.get(position).getAppName());
                }

                position++;
            }
        }
    }

    @Override
    public ArrayList<App> getInstalledApplications(List<ApplicationInfo> applicationInfoList) {
        ArrayList<App> apps = new ArrayList<>();

        for (ApplicationInfo applicationInfo : applicationInfoList) {
            try {
                if (getContext().getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                    App app = new App();
                    app.setAppIcon(applicationInfo.loadIcon(getContext().getPackageManager()));
                    app.setAppName(applicationInfo.loadLabel(getContext().getPackageManager()).toString());
                    app.setAppPackage(applicationInfo.packageName);

                    if (!app.getAppName().equals(getContext().getString(R.string.app_name))) {
                        apps.add(app);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(apps, new Comparator<App>() {
            @Override
            public int compare(App lhs, App rhs) {
                return lhs.getAppName().compareToIgnoreCase(rhs.getAppName());
            }
        });

        return apps;
    }

    @Override
    public void makeQuery(ArrayList<App> apps, CharSequence charSequence) {
        if (apps != null) {
            ArrayList<App> appArrayList = new ArrayList<>();

            for (App app : apps) {
                if (app.getAppName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                    appArrayList.add(app);
                }
            }

            appListView.setNewData(appArrayList);
        }
    }
}

package com.arrg.app.uapplock.presenter;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.AppListPresenter;
import com.arrg.app.uapplock.interfaces.AppListView;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.view.activity.AppListActivity;

import java.util.ArrayList;
import java.util.List;

public class IAppListPresenter implements AppListPresenter {

    private AppListView appListView;

    public IAppListPresenter(AppListView appListView) {
        this.appListView = appListView;
    }

    @Override
    public void onCreate() {
        appListView.setupViews();
    }

    @Override
    public void onClick() {
        appListView.hideSearch();
    }

    @Override
    public Context getContext() {
        return appListView.getContext();
    }

    @Override
    public void onItemClick(int id) {
        if (id == R.id.nav_apps) {
            appListView.setFragment(AppListActivity.ALL_APPS, R.string.title_activity_app_list);
        } else if (id == R.id.nav_locked_apps) {
            appListView.setFragment(AppListActivity.LOCKED_APPS, R.string.locked_apps);
        } else if (id == R.id.nav_unlocked_apps) {
            appListView.setFragment(AppListActivity.UNLOCKED_APPS, R.string.unlocked_apps);
        } else if (id == R.id.nav_settings) {
            appListView.launchSettingsActivity();
        }/* else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_about_me) {

        }*/

        appListView.closeDrawer();
    }

    @Override
    public void onMenuItemClick(int id) {
        appListView.showSearch();
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

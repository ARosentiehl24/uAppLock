package com.arrg.app.uapplock.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.arrg.app.uapplock.interfaces.AppListFragmentPresenter;
import com.arrg.app.uapplock.interfaces.AppListFragmentView;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.model.receiver.PackageReceiver;
import com.arrg.app.uapplock.util.kisstools.utils.LogUtil;

import java.util.ArrayList;

public class IAppListFragmentPresenter implements AppListFragmentPresenter {

    private AppListFragmentView appListFragmentView;
    private PackageReceiver packageReceiver;

    public IAppListFragmentPresenter(AppListFragmentView appListFragmentView) {
        this.appListFragmentView = appListFragmentView;
    }

    @Override
    public void onCreate() {
        appListFragmentView.configFragment();
        registerReceiver(this);
    }

    @Override
    public void setAdapter(Bundle args) {
        ArrayList<App> apps = (ArrayList<App>) args.getSerializable("apps");

        Boolean animate = args.getBoolean("animate", false);

        Integer index = args.getInt("index", 0);

        appListFragmentView.setAdapter(apps, index, animate);
    }

    @Override
    public void registerReceiver(IAppListFragmentPresenter iAppListFragmentPresenter) {
        packageReceiver = new PackageReceiver(iAppListFragmentPresenter);

        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        packageFilter.addDataScheme("package");

        getContext().registerReceiver(packageReceiver, packageFilter);
    }

    @Override
    public void unregisterReceiver() {
        getContext().unregisterReceiver(packageReceiver);
    }

    @Override
    public Context getContext() {
        return appListFragmentView.getFragmentContext();
    }

    @Override
    public void add(String appPackage) {
        try {
            ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(appPackage, 0);

            App newApp = new App();
            newApp.setAppName(applicationInfo.loadLabel(getPackageManager()).toString());
            newApp.setAppIcon(applicationInfo.loadIcon(getPackageManager()));
            newApp.setAppPackage(appPackage);

            ArrayList<App> appArrayList = appListFragmentView.getApps();
            Integer position = 0;

            for (App app : appArrayList) {
                if (app.getAppName().compareToIgnoreCase(newApp.getAppName()) > 1) {
                    LogUtil.e("PackageReceiver", "Add App in: " + appArrayList.get(position - 1).getAppName() + " --> " + newApp.getAppName() + " <-- " + appArrayList.get(position).getAppName());

                    appListFragmentView.add(newApp, position);

                    break;
                } else {
                    LogUtil.e("Receiver", "App in: " + appArrayList.get(position).getAppName());
                }

                position++;
            }
        } catch (PackageManager.NameNotFoundException e) {
            appListFragmentView.showErrorMessage(e.getMessage());
        }
    }

    @Override
    public void remove(String appPackage) {
        ArrayList<App> appArrayList = appListFragmentView.getApps();
        Integer position = 0;

        for (App app : appArrayList) {
            if (app.getAppPackage().equals(appPackage)) {
                LogUtil.e("PackageReceiver", "Remove: " + app.getAppName());

                appListFragmentView.removeAppIn(position);
                break;
            }

            position++;
        }
    }

    @Override
    public PackageManager getPackageManager() {
        return getContext().getPackageManager();
    }
}

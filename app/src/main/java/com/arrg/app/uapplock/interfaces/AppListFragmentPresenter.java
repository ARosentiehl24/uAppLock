package com.arrg.app.uapplock.interfaces;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.arrg.app.uapplock.presenter.IAppListFragmentPresenter;

public interface AppListFragmentPresenter {
    void onCreate();

    void setAdapter(Bundle args);

    void registerReceiver(IAppListFragmentPresenter iAppListFragmentPresenter);

    void unregisterReceiver();

    Context getContext();

    void add(String appPackage);

    void remove(String appPackage);

    PackageManager getPackageManager();
}

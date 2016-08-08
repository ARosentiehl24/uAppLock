package com.arrg.app.uapplock.interfaces;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.arrg.app.uapplock.model.entity.App;
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

    void updateListWith(App app, boolean checked, FragmentActivity fragmentActivity);

    void resetFragments(FragmentActivity fragmentActivity);
}

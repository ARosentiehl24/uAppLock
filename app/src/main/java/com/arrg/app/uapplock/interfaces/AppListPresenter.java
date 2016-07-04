package com.arrg.app.uapplock.interfaces;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.arrg.app.uapplock.model.entity.App;

import java.util.ArrayList;
import java.util.List;

public interface AppListPresenter {

    void onCreate();
    void onClick();
    void onItemClick(int id);
    void onMenuItemClick(int id);
    ArrayList<App> getInstalledApplications(List<ApplicationInfo> applicationInfoList);
    void makeQuery(ArrayList<App> apps, CharSequence charSequence);
    Context getContext();
}

package com.arrg.app.uapplock.interfaces;

import android.app.Activity;

import com.arrg.app.uapplock.model.entity.App;

import java.util.ArrayList;

public interface AppListView {
    void setupViews();
    void closeDrawer();
    void openDrawer();
    void hideSearch();
    void showSearch();
    void toast(String message);
    void setNewData(ArrayList<App> newData);
    void setFragment(int index, int title);
    Activity getContext();
    void launchSettingsActivity();
}

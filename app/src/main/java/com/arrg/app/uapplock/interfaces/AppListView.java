package com.arrg.app.uapplock.interfaces;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.arrg.app.uapplock.model.entity.App;

import java.util.ArrayList;

public interface AppListView {
    void setupViews();

    void showKeyboard(View view, Context context);

    void hideKeyboard(View view, Context context);

    void hideSearch();

    void showSearch();

    void toast(String message);

    void setNewData(ArrayList<App> newData);

    Activity getContext();

    void launchSettingsActivity();

    void resetFragment(int fragmentPosition);

    void updateListWith(App app, boolean checked);

    void add(App app, Integer position);

    void remove(Integer position);
}

package com.arrg.app.uapplock.interfaces;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.arrg.app.uapplock.model.entity.App;

import java.util.ArrayList;

public interface AppListFragmentView {
    void configFragment();

    void setAdapter(ArrayList<App> apps);

    FragmentActivity getFragmentActivity();

    Context getFragmentContext();

    Integer getIndex();

    ArrayList<App> getApps();

    void add(App app, Integer position);

    void removeAppIn(Integer position);

    void showErrorMessage(String message);
}

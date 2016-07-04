package com.arrg.app.uapplock.view.adapter;

import android.content.SharedPreferences;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.util.SharedPreferencesUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class AppAdapter extends BaseQuickAdapter<App>{

    private SharedPreferences lockedAppsPreferences;
    private SharedPreferencesUtil preferencesUtil;

    public AppAdapter(int layoutResId, List<App> data, SharedPreferences lockedAppsPreferences, SharedPreferencesUtil preferencesUtil) {
        super(layoutResId, data);
        this.lockedAppsPreferences = lockedAppsPreferences;
        this.preferencesUtil = preferencesUtil;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, App app) {
        baseViewHolder.setText(R.id.appName, app.getAppName());
        baseViewHolder.setImageDrawable(R.id.appIcon, app.getAppIcon());
        baseViewHolder.setChecked(R.id.switchCompat, preferencesUtil.getBoolean(lockedAppsPreferences, app.getAppPackage(), false));
    }
}

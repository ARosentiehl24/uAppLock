package com.arrg.app.uapplock.view.adapter;

import android.content.SharedPreferences;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.util.SharedPreferencesUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends BaseQuickAdapter<App> implements INameableAdapter {

    private ArrayList<App> apps;
    private SharedPreferences lockedAppsPreferences;
    private SharedPreferencesUtil preferencesUtil;

    public AppAdapter(int layoutResId, List<App> data, SharedPreferences lockedAppsPreferences, SharedPreferencesUtil preferencesUtil) {
        super(layoutResId, data);
        this.apps = (ArrayList<App>) data;
        this.lockedAppsPreferences = lockedAppsPreferences;
        this.preferencesUtil = preferencesUtil;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, App app) {
        baseViewHolder.setImageDrawable(R.id.appIcon, app.getAppIcon());
        baseViewHolder.setText(R.id.appName, app.getAppName());
        baseViewHolder.setChecked(R.id.switchCompat, preferencesUtil.getBoolean(lockedAppsPreferences, app.getAppPackage(), false));

        TypefaceHelper.typeface(baseViewHolder.getView(R.id.appName));
    }

    @Override
    public App getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public Character getCharacterForElement(int element) {
        Character character = apps.get(element).getAppName().charAt(0);
        if (Character.isDigit(character)) {
            character = '#';
        }
        return character;
    }
}

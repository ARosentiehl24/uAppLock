package com.arrg.app.uapplock.view.adapter;

import android.content.SharedPreferences;
import android.support.v7.widget.AppCompatTextView;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.util.SharedPreferencesUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kyleduo.switchbutton.SwitchButton;
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
    protected void convert(BaseViewHolder baseViewHolder, final App app) {
        baseViewHolder.setImageDrawable(R.id.appIcon, app.getAppIcon());

        AppCompatTextView textView = baseViewHolder.getView(R.id.appName);
        textView.setText(app.getAppName());
        textView.setTypeface(UAppLock.typeface());

        SwitchButton switchButton = baseViewHolder.getView(R.id.switchCompat);
        switchButton.setCheckedImmediately(preferencesUtil.getBoolean(lockedAppsPreferences, app.getAppPackage(), false));
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

    public int getLetterPosition(String letter) {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getAppName().substring(0, 1).equals(letter)) {
                return i;
            }
        }
        return -1;
    }
}

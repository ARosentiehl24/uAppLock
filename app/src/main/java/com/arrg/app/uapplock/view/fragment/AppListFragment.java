package com.arrg.app.uapplock.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.util.SharedPreferencesUtil;
import com.arrg.app.uapplock.view.adapter.AppAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kyleduo.switchbutton.SwitchButton;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.arrg.app.uapplock.UAppLock.LOCKED_APPS_PREFERENCES;
import static com.arrg.app.uapplock.view.activity.AppListActivity.ALL_APPS;
import static com.arrg.app.uapplock.view.activity.AppListActivity.LOCKED_APPS;
import static com.arrg.app.uapplock.view.activity.AppListActivity.UNLOCKED_APPS;

;
;

public class AppListFragment extends Fragment implements BaseQuickAdapter.OnRecyclerViewItemClickListener, BaseQuickAdapter.OnRecyclerViewItemLongClickListener {

    @Bind(R.id.dragScrollBar)
    DragScrollBar dragScrollBar;
    private AppAdapter appAdapter;
    private ArrayList<App> appArrayList;
    private Integer index;
    private SharedPreferences lockedAppsPreferences;
    private SharedPreferencesUtil preferencesUtil;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    public AppListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesUtil = new SharedPreferencesUtil(getActivity());
        lockedAppsPreferences = getActivity().getSharedPreferences(LOCKED_APPS_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        ArrayList<App> apps = (ArrayList<App>) bundle.getSerializable("apps");

        Boolean animate = bundle.getBoolean("animate", false);

        index = bundle.getInt("index", 0);

        setAdapter(apps, index, animate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setAdapter(ArrayList<App> apps, Integer index, boolean withAnimation) {
        appAdapter = null;

        appArrayList = new ArrayList<>();

        switch (index) {
            case ALL_APPS:
                appArrayList = apps;

                appAdapter = new AppAdapter(R.layout.app_item, appArrayList, lockedAppsPreferences, preferencesUtil);
                break;
            case LOCKED_APPS:
                for (App app : apps) {
                    if (preferencesUtil.getBoolean(lockedAppsPreferences, app.getAppPackage(), false)) {
                        appArrayList.add(app);
                    }
                }

                appAdapter = new AppAdapter(R.layout.app_item, appArrayList, lockedAppsPreferences, preferencesUtil);
                break;
            case UNLOCKED_APPS:
                for (App app : apps) {
                    if (!preferencesUtil.getBoolean(lockedAppsPreferences, app.getAppPackage(), false)) {
                        appArrayList.add(app);
                    }
                }

                appAdapter = new AppAdapter(R.layout.app_item, appArrayList, lockedAppsPreferences, preferencesUtil);
                break;
        }

        assert appAdapter != null;

        if (withAnimation) {
            appAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        }

        recyclerView.setAdapter(appAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        appAdapter.setOnRecyclerViewItemClickListener(this);
        appAdapter.setOnRecyclerViewItemLongClickListener(this);

        dragScrollBar.addIndicator(new AlphabetIndicator(getActivity()), true);
    }

    @Override
    public void onItemClick(View view, int i) {
        App app = appArrayList.get(i);

        SwitchButton switchCompat = (SwitchButton) view.findViewById(R.id.switchCompat);
        switchCompat.toggle();

        app.setChecked(switchCompat.isChecked());

        preferencesUtil.putValue(lockedAppsPreferences, app.getAppPackage(), switchCompat.isChecked());

        if (index != ALL_APPS) {
            appAdapter.remove(i);
        }
    }

    @Override
    public boolean onItemLongClick(View view, int i) {
        final App app = appArrayList.get(i);

        Drawable drawable = app.getAppIcon();

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 96, 96, true));

        new MaterialDialog.Builder(getActivity())
                .title(app.getAppName())
                .icon(drawable)
                .content(getString(R.string.long_click_message))
                .positiveText(getString(R.string.open))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getAppPackage());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    }
                })
                .negativeText(getString(R.string.uninstall))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Snackbar.make(getView(), R.string.uninstalling_app_message, Snackbar.LENGTH_LONG)
                                .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        Log.e("Values", "Dismissed");
                                    }
                                }).show();
                    }
                })
                .build()
                .show();

        return false;
    }
}

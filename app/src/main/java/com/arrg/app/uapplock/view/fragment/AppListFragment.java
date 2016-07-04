package com.arrg.app.uapplock.view.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.util.SharedPreferencesUtil;
import com.arrg.app.uapplock.view.adapter.AppAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;

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

        index = bundle.getInt("index", 0);

        setAdapter(apps, index, false);
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
    }

    @Override
    public void onItemClick(View view, int i) {
        App app = appArrayList.get(i);

        boolean state = preferencesUtil.getBoolean(lockedAppsPreferences, app.getAppPackage(), false);

        app.setChecked(!state);

        SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.switchCompat);
        switchCompat.setChecked(!state);

        preferencesUtil.putValue(lockedAppsPreferences, app.getAppPackage(), !state);

        if (index != ALL_APPS) {
            appAdapter.remove(i);
        }
    }

    @Override
    public boolean onItemLongClick(View view, int i) {
        return false;
    }
}

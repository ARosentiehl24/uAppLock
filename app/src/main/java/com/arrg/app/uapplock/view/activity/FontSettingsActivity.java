package com.arrg.app.uapplock.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.model.entity.Font;
import com.arrg.app.uapplock.util.Util;
import com.arrg.app.uapplock.view.adapter.FontAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FontSettingsActivity extends UAppLockActivity implements BaseQuickAdapter.OnRecyclerViewItemClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_settings);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);

        setSupportActionBar(toolbar);

        Util.modifyToolbar(this, R.string.title_activity_font_settings, true);

        ArrayList<Font> fonts = new ArrayList<>();

        fonts.add(new Font("Raleway", "fonts/Raleway.ttf"));
        fonts.add(new Font("LazySpringDay", "fonts/LazySpringDay.ttf"));

        FontAdapter fontAdapter = new FontAdapter(R.layout.font_item, fonts);

        recyclerView.setAdapter(fontAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fontAdapter.setOnRecyclerViewItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int i) {
        UAppLock uAppLock = (UAppLock) getApplication();
        uAppLock.initTypeFace(uAppLock.getTypeface(i));

        PreferencesManager.putInt(getString(R.string.font_position), i);

        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
        finish();
    }
}

package com.arrg.app.uapplock.view.activity;

import android.content.Context;
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
import com.shawnlin.preferencesmanager.PreferencesManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FontSettingsActivity extends UAppLockActivity implements BaseQuickAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FontAdapter fontAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Util.modifyToolbar(this, R.string.title_activity_font_settings, true);

        ArrayList<Font> fonts = new ArrayList<>();

        fonts.add(new Font("Raleway", "fonts/Raleway.ttf"));
        fonts.add(new Font("LazySpringDay", "fonts/LazySpringDay.ttf"));

        fontAdapter = new FontAdapter(R.layout.font_item, fonts);

        recyclerView.setAdapter(fontAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fontAdapter.setOnRecyclerViewItemClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onItemClick(View view, int i) {
        UAppLock uAppLock = (UAppLock) getApplication();
        uAppLock.setAppFontTo(fontAdapter.getItem(i).getPath());

        PreferencesManager.putString(getString(R.string.font_path), fontAdapter.getItem(i).getPath());

        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
        finish();
    }
}

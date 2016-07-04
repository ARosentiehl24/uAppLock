package com.arrg.app.uapplock.view.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.AppListView;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.presenter.IAppListPresenter;
import com.arrg.app.uapplock.view.fragment.AppListFragment;
import com.jaouan.revealator.Revealator;
import com.norbsoft.typefacehelper.ActionBarHelper;
import com.norbsoft.typefacehelper.TypefaceHelper;

import org.fingerlinks.mobile.android.navigator.Navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arrg.app.uapplock.UAppLock.DURATIONS_OF_ANIMATIONS;

public class AppListActivity extends AppCompatActivity implements AppListView, NavigationView.OnNavigationItemSelectedListener {

    public static final int ALL_APPS = 0;
    public static final int LOCKED_APPS = 1;
    public static final int UNLOCKED_APPS = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.initialView)
    View initialView;
    @Bind(R.id.searchInput)
    AppCompatEditText searchInput;
    @Bind(R.id.revealView)
    LinearLayout revealView;
    @Bind(R.id.navigationView)
    NavigationView navigationView;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawer;

    private ArrayList<App> appsArrayList;
    private Boolean closeSearchWithBackButton = false;
    private Boolean isSearchInputOpen = false;
    private IAppListPresenter iAppListPresenter;
    private Integer selectedList = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);

        iAppListPresenter = new IAppListPresenter(this);
        iAppListPresenter.onCreate();

        new LoadApplications().execute();
    }

    @Override
    public void onBackPressed() {
        if (isSearchInputOpen) {
            hideSearch();
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            iAppListPresenter.onMenuItemClick(id);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        iAppListPresenter.onItemClick(id);

        return true;
    }

    @OnClick(R.id.btnBack)
    public void onClick() {
        closeSearchWithBackButton = true;
        iAppListPresenter.onClick();
    }

    @Override
    public void showSearch() {
        Revealator.reveal(revealView)
                .from(initialView)
                .withRevealDuration(DURATIONS_OF_ANIMATIONS)
                .withChildAnimationDuration(DURATIONS_OF_ANIMATIONS)
                .withTranslateDuration(DURATIONS_OF_ANIMATIONS)
                .withChildsAnimation()
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        isSearchInputOpen = true;

                        searchInput.requestFocus();
                        toggleKeyboard();
                    }
                })
                .start();
    }

    @Override
    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setNewData(ArrayList<App> newData) {
        AppListFragment appListFragment = (AppListFragment) getFragment(AppListFragment.class.getName());
        appListFragment.setAdapter(newData, selectedList, false);
    }

    public Fragment getFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    public void hideSearch() {
        Revealator.unreveal(revealView)
                .withDuration(DURATIONS_OF_ANIMATIONS)
                .withEndAction(new TimerTask() {
                    @Override
                    public void run() {
                        isSearchInputOpen = false;

                        searchInput.getText().clear();

                        if (closeSearchWithBackButton) {
                            closeSearchWithBackButton = false;

                            toggleKeyboard();
                        }
                    }
                })
                .start();
    }

    public void toggleKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (inputMethodManager.isActive()) {
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } else {
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void setupViews() {
        setSupportActionBar(toolbar);

        ActionBarHelper.setTitle(getSupportActionBar(), TypefaceHelper.typeface(this, R.string.title_activity_app_list));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        TypefaceHelper.typeface(view);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                iAppListPresenter.makeQuery(appsArrayList, charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    public void setFragment(int index, int title) {
        if (selectedList != index) {
            selectedList = index;

            ActionBarHelper.setTitle(getSupportActionBar(), TypefaceHelper.typeface(this, title));

            Bundle bundle = new Bundle();
            bundle.putSerializable("apps", appsArrayList);
            bundle.putInt("index", index);

            Navigator.with(AppListActivity.this)
                    .build()
                    .goTo(new AppListFragment(), bundle, R.id.container)
                    .tag(AppListFragment.class.getName())
                    .replace()
                    .commit();
        }
    }

    public class LoadApplications extends AsyncTask<Void, Void, ArrayList<App>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<App> doInBackground(Void... voids) {
            ArrayList<App> apps = iAppListPresenter.getInstalledApplications(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA));

            Collections.sort(apps, new Comparator<App>() {
                @Override
                public int compare(App lhs, App rhs) {
                    return lhs.getAppName().compareTo(rhs.getAppName());
                }
            });

            return apps;
        }

        @Override
        protected void onPostExecute(ArrayList<App> apps) {
            super.onPostExecute(apps);

            appsArrayList = apps;
            navigationView.getMenu().getItem(0).setChecked(true);

            setFragment(ALL_APPS, R.string.title_activity_app_list);
        }
    }
}

package com.arrg.app.uapplock.view.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.model.entity.App;
import com.jaouan.revealator.Revealator;
import com.norbsoft.typefacehelper.ActionBarHelper;
import com.norbsoft.typefacehelper.TypefaceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arrg.app.uapplock.UAppLock.DURATIONS_OF_ANIMATIONS;

public class AppListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);

        setSupportActionBar(toolbar);

        ActionBarHelper.setTitle(getSupportActionBar(), TypefaceHelper.typeface(this, R.string.title_activity_app_list));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Revealator.reveal(revealView)
                    .from(initialView)
                    .withRevealDuration(DURATIONS_OF_ANIMATIONS)
                    .withChildAnimationDuration(DURATIONS_OF_ANIMATIONS)
                    .withTranslateDuration(DURATIONS_OF_ANIMATIONS)
                    .withChildsAnimation()
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            searchInput.requestFocus();
                            toggleKeyboard();
                        }
                    })
                    .start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_apps) {
            // Handle the camera action
        } else if (id == R.id.nav_locked_apps) {

        } else if (id == R.id.nav_unlocked_apps) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_about_me) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @OnClick(R.id.btnBack)
    public void onClick() {
        Revealator.unreveal(revealView)
                .withDuration(DURATIONS_OF_ANIMATIONS)
                .withEndAction(new TimerTask() {
                    @Override
                    public void run() {
                        searchInput.getText().clear();
                        toggleKeyboard();
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

    public ArrayList<App> getInstalledApplications(List<ApplicationInfo> applicationInfoList) {
        ArrayList<App> apps = new ArrayList<>();

        for (ApplicationInfo applicationInfo : applicationInfoList) {
            try {
                if (getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                    App app = new App();
                    app.setAppIcon(applicationInfo.loadIcon(getPackageManager()));
                    app.setAppName(applicationInfo.loadLabel(getPackageManager()).toString());
                    app.setAppPackage(applicationInfo.packageName);
                    apps.add(app);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return apps;
    }

    public class LoadApplications extends AsyncTask<Void, Void, ArrayList<App>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<App> doInBackground(Void... voids) {
            ArrayList<App> apps = getInstalledApplications(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA));

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
        }
    }
}

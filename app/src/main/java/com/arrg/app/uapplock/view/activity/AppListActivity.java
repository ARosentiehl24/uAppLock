package com.arrg.app.uapplock.view.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.AppListView;
import com.arrg.app.uapplock.model.UTypefaceSpan;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.presenter.IAppListPresenter;
import com.arrg.app.uapplock.util.PackageUtils;
import com.arrg.app.uapplock.util.kisstools.utils.BitmapUtil;
import com.arrg.app.uapplock.util.kisstools.utils.FileUtil;
import com.arrg.app.uapplock.util.kisstools.utils.ResourceUtil;
import com.arrg.app.uapplock.view.fragment.AppListFragment;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.FadeOutAnimation;
import com.jaouan.revealator.Revealator;
import com.norbsoft.typefacehelper.ActionBarHelper;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import org.fingerlinks.mobile.android.navigator.Navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

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
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<App> appsArrayList;
    private Boolean closeSearchWithBackButton = false;
    private Boolean isSearchInputOpen = false;
    private View headerView;
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
    protected void onResume() {
        super.onResume();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                CircleImageView profilePicture = (CircleImageView) headerView.findViewById(R.id.profilePicture);
                AppCompatImageView container = (AppCompatImageView) headerView.findViewById(R.id.container);

                String profilePicturePath = PreferencesManager.getString(getString(R.string.profile_picture));
                String headerWallpaper = PreferencesManager.getString(getString(R.string.wallpaper));

                if (FileUtil.exists(profilePicturePath)) {
                    if (profilePicturePath.length() != 0) {
                        Bitmap profile = BitmapUtil.getImage(profilePicturePath);

                        profilePicture.setBackground(null);
                        profilePicture.setImageBitmap(profile);
                    }
                } else {
                    profilePicture.setBackgroundResource(R.drawable.dot_empty_background);
                    profilePicture.setImageBitmap(null);
                }

                if (FileUtil.exists(headerWallpaper)) {
                    if (headerWallpaper.length() != 0) {
                        Bitmap wallpaper = BitmapUtil.getImage(headerWallpaper);

                        container.setImageBitmap(wallpaper);
                    }
                } else {
                    container.setImageBitmap(null);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isSearchInputOpen) {
            hideSearch();
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                Navigator.with(this).utils().finishWithAnimation();
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

        Menu menu = navigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);

            SubMenu subMenu = menuItem.getSubMenu();

            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(menuItem);
        }

        headerView = navigationView.getHeaderView(0);
        TypefaceHelper.typeface(headerView);

        AppCompatTextView appVersion = (AppCompatTextView) headerView.findViewById(R.id.tvAppVersion);
        appVersion.setText(String.format(getString(R.string.current_version), PackageUtils.getAppVersionName(this)));

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
    public void launchSettingsActivity() {
        Navigator.with(this).build().goTo(SettingsActivity.class).animation().commit();
    }

    @Override
    public void setFragment(int index, int title, boolean animate) {
        if (selectedList != index) {
            selectedList = index;

            ActionBarHelper.setTitle(getSupportActionBar(), TypefaceHelper.typeface(this, title));

            Bundle bundle = new Bundle();
            bundle.putSerializable("apps", appsArrayList);
            bundle.putInt("index", index);
            bundle.putBoolean("animate", animate);

            Navigator.with(AppListActivity.this)
                    .build()
                    .goTo(new AppListFragment(), bundle, R.id.container)
                    .tag(AppListFragment.class.getName())
                    .replace()
                    .commit();
        }
    }

    @Override
    public Boolean isSearchInputOpen() {
        return isSearchInputOpen;
    }

    private void applyFontToMenuItem(MenuItem menuItem) {
        String fontPath = PreferencesManager.getString(ResourceUtil.getString(R.string.font_path), "fonts/Raleway.ttf");

        Typeface typeface = Typeface.createFromAsset(getAssets(), fontPath);

        SpannableString mNewTitle = new SpannableString(menuItem.getTitle());

        mNewTitle.setSpan(new UTypefaceSpan("", typeface), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        menuItem.setTitle(mNewTitle);
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
                    return lhs.getAppName().compareToIgnoreCase(rhs.getAppName());
                }
            });

            return apps;
        }

        @Override
        protected void onPostExecute(ArrayList<App> apps) {
            super.onPostExecute(apps);

            appsArrayList = apps;
            navigationView.getMenu().getItem(0).setChecked(true);

            setFragment(ALL_APPS, R.string.title_activity_app_list, true);

            new FadeOutAnimation(progressBar).setDuration(Animation.DURATION_SHORT).setListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    progressBar.setVisibility(View.GONE);
                }
            }).animate();
        }
    }
}

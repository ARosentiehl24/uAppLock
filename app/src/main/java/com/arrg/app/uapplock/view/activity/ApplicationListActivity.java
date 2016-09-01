package com.arrg.app.uapplock.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.arrg.app.uapplock.interfaces.AppListView;
import com.arrg.app.uapplock.model.entity.App;
import com.arrg.app.uapplock.presenter.IAppListPresenter;
import com.arrg.app.uapplock.util.kisstools.utils.ToastUtil;
import com.arrg.app.uapplock.view.fragment.AppListFragment;
import com.arrg.app.uapplock.view.fragment.LockedAppsFragment;
import com.arrg.app.uapplock.view.fragment.UnlockedAppsFragment;
import com.jaouan.revealator.Revealator;
import com.sbrukhanda.fragmentviewpager.FragmentViewPager;
import com.sbrukhanda.fragmentviewpager.adapters.FragmentStatePagerAdapter;

import org.fingerlinks.mobile.android.navigator.Navigator;

import java.util.ArrayList;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.arrg.app.uapplock.UAppLock.DURATIONS_OF_ANIMATIONS;

public class ApplicationListActivity extends AppCompatActivity implements AppListView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.initialView)
    View initialView;
    @BindView(R.id.searchInput)
    AppCompatEditText searchInput;
    @BindView(R.id.revealView)
    LinearLayout revealView;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.container)
    FragmentViewPager mViewPager;

    public static final int ALL_APPS = 0;
    public static final int LOCKED_APPS = 1;
    public static final int UNLOCKED_APPS = 2;

    private ArrayList<App> apps;
    private Boolean isSearchInputOpen = false;
    private IAppListPresenter iAppListPresenter;
    private SectionsPagerAdapter mSectionsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);
        ButterKnife.bind(this);

        iAppListPresenter = new IAppListPresenter(this);
        iAppListPresenter.onCreate();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if (isSearchInputOpen) {
            hideSearch();
        } else {
            Navigator.with(this).utils().finishWithAnimation(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_application_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                launchSettingsActivity();
                return true;
            case R.id.action_search:
                showSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mViewPager.notifyPagerVisible();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewPager.notifyPagerInvisible();
    }

    @Override
    public void setupViews() {
        apps = iAppListPresenter.getInstalledApplications(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA));

        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setPagingEnabled(false);

        tabLayout.setupWithViewPager(mViewPager);

        Typeface typeface = UAppLock.typeface();

        ViewGroup viewGroup = (ViewGroup) tabLayout.getChildAt(0);

        int tabsCount = viewGroup.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup viewGroupChildAt = (ViewGroup) viewGroup.getChildAt(j);

            int childCount = viewGroupChildAt.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View tabViewChild = viewGroupChildAt.getChildAt(i);

                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int pagerPosition = mViewPager.getCurrentItem();

                switch (pagerPosition) {
                    case ALL_APPS:
                        iAppListPresenter.makeQuery(apps, charSequence);
                        break;
                    case LOCKED_APPS:
                        iAppListPresenter.makeQuery(iAppListPresenter.lockedApps(apps), charSequence);
                        break;
                    case UNLOCKED_APPS:
                        iAppListPresenter.makeQuery(iAppListPresenter.unlockedApps(apps), charSequence);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void showKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void hideSearch() {
        Revealator.unreveal(revealView)
                .to(initialView)
                .withUnrevealDuration(DURATIONS_OF_ANIMATIONS)
                .withEndAction(new TimerTask() {
                    @Override
                    public void run() {
                        isSearchInputOpen = false;

                        searchInput.getText().clear();

                        hideKeyboard(searchInput, getContext());

                        resetFragment(mViewPager.getCurrentItem());
                    }
                })
                .start();
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

                        showKeyboard(searchInput, getContext());
                    }
                })
                .start();
    }

    @Override
    public void toast(String message) {
        ToastUtil.show(message);
    }

    @Override
    public void setNewData(ArrayList<App> newData) {
        int pagerPosition = mViewPager.getCurrentItem();

        switch (pagerPosition) {
            case ALL_APPS:
                AppListFragment appListFragment = (AppListFragment) mSectionsPagerAdapter.getFragment(ALL_APPS);
                if (appListFragment != null) {
                    appListFragment.setAdapter(newData);
                }
                break;
            case LOCKED_APPS:
                LockedAppsFragment lockedAppsFragment = (LockedAppsFragment) mSectionsPagerAdapter.getFragment(LOCKED_APPS);
                if (lockedAppsFragment != null) {
                    lockedAppsFragment.setAdapter(newData);
                }
                break;
            case UNLOCKED_APPS:
                UnlockedAppsFragment unlockedAppsFragment = (UnlockedAppsFragment) mSectionsPagerAdapter.getFragment(UNLOCKED_APPS);
                if (unlockedAppsFragment != null) {
                    unlockedAppsFragment.setAdapter(newData);
                }
                break;
        }
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
    public void resetFragment(int pagerPosition) {
        switch (pagerPosition) {
            case ALL_APPS:
                AppListFragment appListFragment = (AppListFragment) mSectionsPagerAdapter.getFragment(ALL_APPS);
                if (appListFragment != null) {
                    appListFragment.setAdapter(apps);
                }
                break;
            case LOCKED_APPS:
                LockedAppsFragment lockedAppsFragment = (LockedAppsFragment) mSectionsPagerAdapter.getFragment(LOCKED_APPS);
                if (lockedAppsFragment != null) {
                    lockedAppsFragment.setAdapter(iAppListPresenter.lockedApps(apps));
                }
                break;
            case UNLOCKED_APPS:
                UnlockedAppsFragment unlockedAppsFragment = (UnlockedAppsFragment) mSectionsPagerAdapter.getFragment(UNLOCKED_APPS);
                if (unlockedAppsFragment != null) {
                    unlockedAppsFragment.setAdapter(iAppListPresenter.unlockedApps(apps));
                }
                break;
        }
    }

    @Override
    public void updateListWith(App app, boolean checked) {
        LockedAppsFragment lockedAppsFragment = (LockedAppsFragment) mSectionsPagerAdapter.getFragment(LOCKED_APPS);

        iAppListPresenter.updateAppWith(lockedAppsFragment.getApps(), app, checked);
    }

    @Override
    public void add(App app, Integer position) {
        LockedAppsFragment lockedAppsFragment = (LockedAppsFragment) mSectionsPagerAdapter.getFragment(LOCKED_APPS);
        lockedAppsFragment.add(app, position);
    }

    @Override
    public void remove(Integer position) {
        LockedAppsFragment lockedAppsFragment = (LockedAppsFragment) mSectionsPagerAdapter.getFragment(LOCKED_APPS);
        lockedAppsFragment.removeAppIn(position);
    }

    @OnClick(R.id.btnBack)
    public void onClick() {
        iAppListPresenter.onClick();
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment instantiateFragment(int position) {
            switch (position) {
                case ALL_APPS:
                    return AppListFragment.newInstance(apps);
                case LOCKED_APPS:
                    return LockedAppsFragment.newInstance(iAppListPresenter.lockedApps(apps));
                case UNLOCKED_APPS:
                    return UnlockedAppsFragment.newInstance(iAppListPresenter.unlockedApps(apps));
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case ALL_APPS:
                    return getString(R.string.applications);
                case LOCKED_APPS:
                    return getString(R.string.locked_apps);
                case UNLOCKED_APPS:
                    return getString(R.string.unlocked_apps);
            }
            return null;
        }
    }
}

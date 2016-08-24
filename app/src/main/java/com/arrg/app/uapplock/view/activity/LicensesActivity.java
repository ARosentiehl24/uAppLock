package com.arrg.app.uapplock.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.util.Util;

import net.yslibrary.licenseadapter.LicenseAdapter;
import net.yslibrary.licenseadapter.LicenseEntry;
import net.yslibrary.licenseadapter.Licenses;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LicensesActivity extends UAppLockActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Util.modifyToolbar(this, R.string.title_activity_licenses, true);

        List<LicenseEntry> licenses = new ArrayList<>();

        licenses.add(Licenses.noContent("Android SDK", "Google Inc.", "https://developer.android.com/sdk/terms.html"));
        //licenses.add(Licenses.fromGitHub("oktayayr/advancedtextview", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("afollestad/assent", Licenses.NAME_APACHE_V2, Licenses.FILE_MD));
        licenses.add(Licenses.fromGitHub("afollestad/material-dialogs", Licenses.NAME_MIT, Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("aritraroy/PinLockView", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("Commit451/InkPageIndicator", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("DmitryMalkovich/material-design-dimens", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("Dimezis/BlurView", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("2359media/EasyAndroidAnimations", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("bumptech/glide", Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("CymChad/BaseRecyclerViewAdapterHelper", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("Jaouan/Revealator", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("jkwiecien/EasyImage", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("Kennyc1012/BottomSheet", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("JakeWharton/butterknife", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("JakeWharton/timber", Licenses.NAME_APACHE_V2, Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("kyleduo/SwitchButton", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("mukeshsolanki/App-Runtime-Permissions-Android", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("nvanbenschoten/motion", Licenses.NAME_APACHE_V2, Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("sbrukhanda/fragmentviewpager", Licenses.NAME_APACHE_V2, Licenses.FILE_TXT));
        licenses.add(Licenses.fromGitHub("ShawnLin013/PreferencesManager", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("square/picasso", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("ArthurHub/Android-Image-Cropper", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("cketti/EmailIntentBuilder", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("hdodenhof/CircleImageView", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.noContent("AndroidGlobalUtils", "A7maDev", "http://www.gnu.org/licenses/gpl-3.0.en.html"));
        licenses.add(Licenses.fromGitHub("code-mc/material-icon-lib", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("yshrsmz/LicenseAdapter", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("fingerlinks/Navigator", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("chrisjenx/Calligraphy", Licenses.LICENSE_APACHE_V2));

        LicenseAdapter licenseAdapter = new LicenseAdapter(licenses);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(licenseAdapter);

        Licenses.load(licenses);
    }
}

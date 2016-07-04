package com.arrg.app.uapplock.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.IntroActivityView;
import com.arrg.app.uapplock.presenter.IIntroActivityPresenter;
import com.arrg.app.uapplock.view.adapter.SectionsPagerAdapter;
import com.norbsoft.typefacehelper.TypefaceHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IntroActivity extends AppCompatActivity implements IntroActivityView {

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private ArrayList<Fragment> fragments;
    private FingerprintManagerCompat fingerprintManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);

        IIntroActivityPresenter iIntroActivityPresenter = new IIntroActivityPresenter(this);
        iIntroActivityPresenter.onCreate();
    }

    @Override
    public void setupViews() {
        fragments = new ArrayList<>();
        fingerprintManagerCompat = FingerprintManagerCompat.from(this);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }

    @Override
    public void hideNext() {

    }

    @Override
    public void hidePrevious() {

    }

    @Override
    public void showNext() {

    }

    @Override
    public void showPrevious() {

    }

    @Override
    public void hideAux() {

    }

    @Override
    public void showAux() {

    }

    @Override
    public boolean isHardwareDetected() {
        return fingerprintManagerCompat.isHardwareDetected();
    }

    @Override
    public boolean hasEnrolledFingerprints() {
        return fingerprintManagerCompat.isHardwareDetected();
    }
}

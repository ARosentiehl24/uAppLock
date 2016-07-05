package com.arrg.app.uapplock.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

public class SectionsPagerAdapter extends SmartFragmentStatePagerAdapter {
    private ArrayList<Fragment> fragments;

    public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getActivity().getLocalClassName();
    }
}

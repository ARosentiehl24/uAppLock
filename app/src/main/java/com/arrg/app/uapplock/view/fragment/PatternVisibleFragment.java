package com.arrg.app.uapplock.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arrg.app.uapplock.R;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class PatternVisibleFragment extends PreferenceFragmentCompatDividers {

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pattern_settings);
        setDividerPreferences(DIVIDER_PREFERENCE_AFTER_LAST);
    }
}

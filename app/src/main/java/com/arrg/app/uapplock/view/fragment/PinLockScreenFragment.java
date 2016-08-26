package com.arrg.app.uapplock.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arrg.app.uapplock.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinLockScreenFragment extends Fragment {


    public PinLockScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pin_lock_screen, container, false);
    }

}

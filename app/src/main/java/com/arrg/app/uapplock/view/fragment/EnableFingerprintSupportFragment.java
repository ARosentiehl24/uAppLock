package com.arrg.app.uapplock.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.UAppLock;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnableFingerprintSupportFragment extends Fragment {

    @Bind(R.id.tvDescription)
    AppCompatTextView tvDescription;
    @Bind(R.id.btnResetPin)
    AppCompatButton btnResetPin;

    private Integer initialUnlockMethod;

    public EnableFingerprintSupportFragment() {
    }

    public static EnableFingerprintSupportFragment newInstance() {
        return new EnableFingerprintSupportFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enable_fingerprint_support, container, false);
        TypefaceHelper.typeface(view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialUnlockMethod = PreferencesManager.getInt(getString(R.string.unlock_method));

        boolean isFingerprintEnabled = PreferencesManager.getBoolean(getString(R.string.fingerprint_recognition_activated), false);

        updateViews(isFingerprintEnabled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnResetPin)
    public void onClick() {
        boolean isFingerprintEnabled = PreferencesManager.getBoolean(getString(R.string.fingerprint_recognition_activated));

        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(getActivity());

        if (!fingerprintManagerCompat.hasEnrolledFingerprints()) {
            Toast.makeText(getActivity(), R.string.add_fingerprint_message, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else {
            updateViews(!isFingerprintEnabled);
        }
    }

    public void updateViews(boolean isFingerprintEnabled) {
        PreferencesManager.putBoolean(getString(R.string.fingerprint_recognition_activated), isFingerprintEnabled);

        btnResetPin.setText(isFingerprintEnabled ? R.string.disable_fingerprint_support : R.string.enable_fingerprint_support);
        tvDescription.setVisibility(isFingerprintEnabled ? View.VISIBLE : View.INVISIBLE);

        if (isFingerprintEnabled) {
            PreferencesManager.putInt(getString(R.string.unlock_method), UAppLock.FINGERPRINT);
        } else {
            PreferencesManager.putInt(getString(R.string.unlock_method), initialUnlockMethod);
        }
    }
}

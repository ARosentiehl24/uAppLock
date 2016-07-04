package com.arrg.app.uapplock.view.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arrg.app.uapplock.R;
import com.norbsoft.typefacehelper.TypefaceHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arrg.app.uapplock.view.activity.IntroActivity.OVERLAY_PERMISSION_RC;
import static com.arrg.app.uapplock.view.activity.IntroActivity.USAGE_STATS_RC;
import static com.arrg.app.uapplock.view.activity.IntroActivity.WRITE_SETTINGS_RC;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestPermissionsFragment extends Fragment {

    @Bind(R.id.imageView)
    AppCompatImageView imageView;
    @Bind(R.id.header)
    AppCompatTextView header;
    @Bind(R.id.description)
    AppCompatTextView description;
    private int resIdHeader;
    private int resIdImage;
    private int resIdDescription;
    private int requestCode;

    public RequestPermissionsFragment() {

    }

    public static RequestPermissionsFragment newInstance(int resIdHeader, int resIdImage, int resIdDescription, int requestCode) {
        RequestPermissionsFragment requestPermissionsFragment = new RequestPermissionsFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("header", resIdHeader);
        bundle.putInt("image", resIdImage);
        bundle.putInt("description", resIdDescription);
        bundle.putInt("request_code", requestCode);

        requestPermissionsFragment.setArguments(bundle);

        return requestPermissionsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        this.resIdHeader = bundle.getInt("header");
        this.resIdImage = bundle.getInt("image");
        this.resIdDescription = bundle.getInt("description");
        this.requestCode = bundle.getInt("request_code");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_permissions, container, false);
        ButterKnife.bind(this, view);
        TypefaceHelper.typeface(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        header.setText(resIdHeader);
        imageView.setImageResource(resIdImage);
        description.setText(resIdDescription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnGrantPermission)
    public void onClick() {
        switch (requestCode) {
            case USAGE_STATS_RC:
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                break;
            case OVERLAY_PERMISSION_RC:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName())));
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                }
                break;
            case WRITE_SETTINGS_RC:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getActivity().getPackageName())));
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                }
                break;
        }
    }
}

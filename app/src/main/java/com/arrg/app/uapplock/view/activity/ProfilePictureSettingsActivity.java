package com.arrg.app.uapplock.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.MenuItem;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.ProfilePictureSettingsView;
import com.arrg.app.uapplock.presenter.IProfilePictureSettingsPresenter;
import com.arrg.app.uapplock.util.BlurEffectUtil;
import com.arrg.app.uapplock.util.Util;
import com.arrg.app.uapplock.util.kisstools.utils.SystemUtil;
import com.arrg.app.uapplock.util.kisstools.utils.ToastUtil;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.mukesh.permissions.AppPermissions;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eightbitlab.com.blurview.BlurView;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfilePictureSettingsActivity extends UAppLockActivity implements ProfilePictureSettingsView {

    @Bind(R.id.cropImageView)
    CropImageView cropImageView;
    @Bind(R.id.container)
    AppCompatImageView container;
    @Bind(R.id.btnChooseTakePicture)
    AppCompatImageButton btnChooseTakePicture;
    @Bind(R.id.blurView)
    BlurView blurView;

    public static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int STORAGE_PERMISSION_RC = 100;
    private AppPermissions appPermissions;
    private BottomSheet bottomSheet;
    private IProfilePictureSettingsPresenter iProfilePictureSettingsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_settings);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);
        Util.modifyToolbar(this, R.string.title_activity_profile_picture_settings, true);

        iProfilePictureSettingsPresenter = new IProfilePictureSettingsPresenter(this);
        iProfilePictureSettingsPresenter.onCreate();

        appPermissions = new AppPermissions(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemUtil.hideSystemUI(this);
    }

    @Override
    protected void onDestroy() {
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        iProfilePictureSettingsPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        iProfilePictureSettingsPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.btnChooseTakePicture)
    public void onClick() {
        if (appPermissions.hasPermission(STORAGE_PERMISSIONS)) {
            showBottomSheet();
        } else {
            showBottomSheet();

            appPermissions.requestPermission(STORAGE_PERMISSIONS, STORAGE_PERMISSION_RC);
        }
    }

    @Override
    public void showBottomSheet() {
        bottomSheet.show();
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        container.setImageBitmap(BlurEffectUtil.blur(getApplicationContext(), bitmap, 25.0f, 0.1f));
        cropImageView.setImageBitmap(bitmap);
    }

    @Override
    public void hideBottomSheet() {
        bottomSheet.dismiss();
    }

    @Override
    public void setupViews() {
        cropImageView.setMaxZoom(100);
        bottomSheet = new BottomSheet.Builder(this, R.style.BottomSheetStyle)
                .setSheet(R.menu.camera_gallery_bottom_sheet_picker)
                .setTitle(R.string.select_source)
                .grid()
                .setColumnCount(2)
                .setCancelable(false)
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet) {

                    }

                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
                        iProfilePictureSettingsPresenter.handleOnItemSelected(menuItem.getItemId());
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, int i) {

                    }
                })
                .create();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showError(String message) {
        ToastUtil.show(message);
    }
}

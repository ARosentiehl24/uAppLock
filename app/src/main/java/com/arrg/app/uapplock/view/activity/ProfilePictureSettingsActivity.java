package com.arrg.app.uapplock.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.MenuItem;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.util.BlurEffectUtil;
import com.arrg.app.uapplock.util.Util;
import com.arrg.app.uapplock.util.kisstools.utils.BitmapUtil;
import com.arrg.app.uapplock.util.kisstools.utils.ToastUtil;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.mukesh.permissions.AppPermissions;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfilePictureSettingsActivity extends UAppLockActivity {

    @Bind(R.id.cropImageView)
    CropImageView cropImageView;

    @Bind(R.id.container)
    AppCompatImageView container;

    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int STORAGE_PERMISSION_RC = 100;

    private AppPermissions appPermissions;
    private BottomSheet bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_settings);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);
        Util.modifyToolbar(this, R.string.title_activity_profile_picture_settings, true);

        appPermissions = new AppPermissions(this);
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
                        EasyImage.clearConfiguration(getApplicationContext());

                        int id = menuItem.getItemId();

                        switch (id) {
                            case R.id.action_camera:
                                EasyImage.openCamera(ProfilePictureSettingsActivity.this, 0);
                                break;
                            case R.id.action_gallery:
                                EasyImage.openGallery(ProfilePictureSettingsActivity.this, 0);
                                break;
                        }
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, int i) {

                    }
                })
                .create();
    }

    @Override
    protected void onDestroy() {
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {
                ToastUtil.show(e.getMessage());
            }

            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource, int i) {
                Bitmap bitmap = BitmapUtil.getImage(file.getAbsolutePath());

                cropImageView.setImageBitmap(bitmap);

                container.setImageBitmap(BlurEffectUtil.blur(getApplicationContext(), bitmap));
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource, int i) {
                if (imageSource == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ProfilePictureSettingsActivity.this);

                    if (photoFile != null) {
                        photoFile.delete();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_PERMISSION_RC:
                List<Integer> permissionResults = new ArrayList<>();

                for (int grantResult : grantResults) {
                    permissionResults.add(grantResult);
                }

                if (permissionResults.contains(PackageManager.PERMISSION_DENIED)) {
                    hideBottomSheet();
                }
                break;
        }
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

    public void showBottomSheet() {
        bottomSheet.show();
    }

    public void hideBottomSheet() {
        bottomSheet.dismiss();
    }
}

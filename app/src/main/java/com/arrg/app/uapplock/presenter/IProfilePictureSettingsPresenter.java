package com.arrg.app.uapplock.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.ProfilePictureSettingsPresenter;
import com.arrg.app.uapplock.interfaces.ProfilePictureSettingsView;
import com.arrg.app.uapplock.util.kisstools.utils.BitmapUtil;
import com.arrg.app.uapplock.view.activity.ProfilePictureSettingsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

public class IProfilePictureSettingsPresenter implements ProfilePictureSettingsPresenter {

    private ProfilePictureSettingsView profilePictureSettingsView;

    public IProfilePictureSettingsPresenter(ProfilePictureSettingsView profilePictureSettingsView) {
        this.profilePictureSettingsView = profilePictureSettingsView;
    }

    @Override
    public void onCreate() {
        profilePictureSettingsView.setupViews();
    }

    @Override
    public Activity getContext() {
        return profilePictureSettingsView.getActivity();
    }

    @Override
    public void handleOnItemSelected(int itemId) {
        EasyImage.clearConfiguration(getContext());

        switch (itemId) {
            case R.id.action_camera:
                EasyImage.openCamera(getContext(), 0);
                break;
            case R.id.action_gallery:
                EasyImage.openGallery(getContext(), 0);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        EasyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {
                profilePictureSettingsView.showError(e.getMessage());
            }

            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource, int i) {
                Bitmap bitmap = BitmapUtil.getImage(file.getAbsolutePath());

                profilePictureSettingsView.setBitmap(bitmap);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource, int i) {
                if (imageSource == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getContext());

                    if (photoFile != null) {
                        photoFile.delete();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ProfilePictureSettingsActivity.STORAGE_PERMISSION_RC:
                List<Integer> permissionResults = new ArrayList<>();

                for (int grantResult : grantResults) {
                    permissionResults.add(grantResult);
                }

                if (permissionResults.contains(PackageManager.PERMISSION_DENIED)) {
                    profilePictureSettingsView.hideBottomSheet();
                }
                break;
        }
    }
}

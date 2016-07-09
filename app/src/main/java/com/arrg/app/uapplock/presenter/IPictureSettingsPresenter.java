package com.arrg.app.uapplock.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.PictureSettingsPresenter;
import com.arrg.app.uapplock.interfaces.PictureSettingsView;
import com.arrg.app.uapplock.util.kisstools.utils.BitmapUtil;
import com.arrg.app.uapplock.util.kisstools.utils.FileUtil;
import com.arrg.app.uapplock.util.kisstools.utils.ResourceUtil;
import com.arrg.app.uapplock.view.activity.ProfilePictureSettingsActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

import static com.arrg.app.uapplock.view.activity.ProfilePictureSettingsActivity.STORAGE_PERMISSIONS;
import static com.arrg.app.uapplock.view.activity.ProfilePictureSettingsActivity.STORAGE_PERMISSION_RC;

public class IPictureSettingsPresenter implements PictureSettingsPresenter {

    private PictureSettingsView profilePictureSettingsView;

    public IPictureSettingsPresenter(PictureSettingsView profilePictureSettingsView) {
        this.profilePictureSettingsView = profilePictureSettingsView;
    }

    @Override
    public void onCreate() {
        profilePictureSettingsView.setupViews();
        profilePictureSettingsView.enableButtons(false);
    }

    @Override
    public Activity getContext() {
        return profilePictureSettingsView.getActivity();
    }

    @Override
    public void handleOnItemSelected(int itemId) {
        EasyImage.clearConfiguration(getContext());
        EasyImage.clearPublicTemp(getContext());

        EasyImage.configuration(getContext())
                .setImagesFolderName(ResourceUtil.getString(R.string.profile_picture_title))
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);

        switch (itemId) {
            case R.id.action_camera:
                EasyImage.openCamera(getContext(), 0);
                break;
            case R.id.action_document:
                EasyImage.openDocuments(getContext(), 0);
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
                profilePictureSettingsView.showMessage(e.getMessage(), false);
            }

            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource, int i) {
                profilePictureSettingsView.enableButtons(true);

                //profilePictureSettingsView.showMessage(file.getAbsolutePath(), true);

                Bitmap bitmap = BitmapUtil.getImage(file.getAbsolutePath());

                profilePictureSettingsView.makeCroppedImageViewVisible(false);
                profilePictureSettingsView.setBitmap(bitmap);

                FileUtil.delete(file);
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

    @Override
    public void onClick(int id) {
        switch (id) {
            case R.id.btnAdd:
                profilePictureSettingsView.showBottomSheet();

                if (!profilePictureSettingsView.hasPermissions(STORAGE_PERMISSIONS)) {
                    profilePictureSettingsView.requestMultiplePermissions(STORAGE_PERMISSIONS, STORAGE_PERMISSION_RC);
                }
                break;
            case R.id.btnRotate:
                profilePictureSettingsView.rotateCropImage(90);
                break;
            case R.id.btnCrop:
                profilePictureSettingsView.enableUndo(true);
                profilePictureSettingsView.makeCroppedImageViewVisible(true);
                break;
            case R.id.btnUndo:
                profilePictureSettingsView.enableUndo(false);
                profilePictureSettingsView.makeCroppedImageViewVisible(false);
                break;
            case R.id.btnDone:
                profilePictureSettingsView.saveProfilePicture();
                break;
        }
    }

    @Override
    public void saveProfilePicture(Bitmap croppedImage, String path) {
        try {
            if (BitmapUtil.saveImage(croppedImage, path)) {
                makeScan(path);

                profilePictureSettingsView.showMessage(ResourceUtil.getString(R.string.done), false);
            }
        } catch (IOException e) {
            profilePictureSettingsView.showMessage(e.getMessage(), true);
        }
    }

    private void makeScan(String path) {
        MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i(TAG(), "Finished scanning " + path);
            }
        });
    }

    public String TAG(){
        return this.getClass().getCanonicalName();
    }
}

package com.arrg.app.uapplock.interfaces;

import android.app.Activity;
import android.graphics.Bitmap;

public interface PictureSettingsView {

    void setupViews();

    Activity getActivity();

    void showMessage(String message, boolean showInLog);

    void showBottomSheet();

    void hideBottomSheet();

    void setBitmap(Bitmap bitmap);

    boolean hasPermission(String permission);

    boolean hasPermissions(String[] permissions);

    void requestOnePermission(String permission, int requestCode);

    void requestMultiplePermissions(String[] permissions, int requestCode);

    void rotateCropImage(int degrees);

    void enableButtons(boolean enable);

    void enableUndo(boolean enable);

    void makeCroppedImageViewVisible(boolean isVisible);

    void saveProfilePicture();
}

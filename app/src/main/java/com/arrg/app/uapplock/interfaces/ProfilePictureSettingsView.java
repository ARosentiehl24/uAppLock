package com.arrg.app.uapplock.interfaces;

import android.app.Activity;
import android.graphics.Bitmap;

public interface ProfilePictureSettingsView {

    void setupViews();

    Activity getActivity();

    void showError(String message);

    void showBottomSheet();

    void hideBottomSheet();

    void setBitmap(Bitmap bitmap);
}

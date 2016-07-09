package com.arrg.app.uapplock.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

public interface PictureSettingsPresenter {

    void onCreate();

    Activity getContext();

    void handleOnItemSelected(int itemId);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void onClick(int id);

    void saveProfilePicture(Bitmap croppedImage, String path);
}

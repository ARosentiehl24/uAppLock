package com.arrg.app.uapplock.interfaces;

import android.app.Activity;
import android.content.Intent;

public interface ProfilePictureSettingsPresenter {

    void onCreate();

    Activity getContext();

    void handleOnItemSelected(int itemId);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}

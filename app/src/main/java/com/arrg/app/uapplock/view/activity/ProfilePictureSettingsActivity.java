package com.arrg.app.uapplock.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.PictureSettingsView;
import com.arrg.app.uapplock.presenter.IPictureSettingsPresenter;
import com.arrg.app.uapplock.util.BlurEffectUtil;
import com.arrg.app.uapplock.util.Util;
import com.arrg.app.uapplock.util.kisstools.utils.BitmapUtil;
import com.arrg.app.uapplock.util.kisstools.utils.SystemUtil;
import com.arrg.app.uapplock.util.kisstools.utils.ToastUtil;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.mukesh.permissions.AppPermissions;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfilePictureSettingsActivity extends UAppLockActivity implements PictureSettingsView {

    @Bind(R.id.cropImageView)
    CropImageView cropImageView;
    @Bind(R.id.container)
    AppCompatImageView container;
    @Bind(R.id.blurView)
    BlurView blurView;
    @Bind(R.id.profilePicture)
    AppCompatImageView profilePicture;
    @Bind(R.id.btnAdd)
    AppCompatImageButton btnAdd;
    @Bind(R.id.btnRotate)
    AppCompatImageButton btnRotate;
    @Bind(R.id.btnDone)
    AppCompatImageButton btnDone;
    @Bind(R.id.btnUndo)
    AppCompatImageButton btnUndo;
    @Bind(R.id.btnCrop)
    AppCompatImageButton btnCrop;

    public static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final float SCALE = 0.0625f;
    public static final int STORAGE_PERMISSION_RC = 100;
    private int height;
    private int width;
    private AppPermissions appPermissions;
    private BottomSheet bottomSheet;
    private IPictureSettingsPresenter iProfilePictureSettingsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_settings);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);
        Util.modifyToolbar(this, R.string.title_activity_profile_picture_settings, true);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        final float radius = 25;
        final View decorView = getWindow().getDecorView();
        final View rootView = decorView.findViewById(android.R.id.content);
        final Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(this, true))
                .blurRadius(radius);

        appPermissions = new AppPermissions(this);

        iProfilePictureSettingsPresenter = new IPictureSettingsPresenter(this);
        iProfilePictureSettingsPresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemUtil.hideSystemUI(this);
    }

    @Override
    protected void onDestroy() {
        EasyImage.clearConfiguration(this);
        EasyImage.clearPublicTemp(this);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            SystemUtil.hideSystemUI(this);
        }
    }

    @Override
    public void showBottomSheet() {
        bottomSheet.show();
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        container.setImageBitmap(BlurEffectUtil.blur(getApplicationContext(), bitmap, 25.0f, SCALE));
        cropImageView.setImageBitmap(bitmap);
    }

    public void showCurrentProfilePicture(Bitmap bitmap) {
        container.setImageBitmap(BlurEffectUtil.blur(getApplicationContext(), bitmap, 25.0f, SCALE));

        profilePicture.setImageBitmap(bitmap);

        cropImageView.setVisibility(View.INVISIBLE);

        profilePicture.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean hasPermissions(String[] permissions) {
        return appPermissions.hasPermission(permissions);
    }

    @Override
    public void requestOnePermission(String permission, int requestCode) {
        appPermissions.requestPermission(permission, requestCode);
    }

    @Override
    public void requestMultiplePermissions(String[] permissions, int requestCode) {
        appPermissions.requestPermission(permissions, requestCode);
    }

    @Override
    public void rotateCropImage(int degrees) {
        cropImageView.rotateImage(degrees);
    }

    @Override
    public void enableButtons(boolean enable) {
        btnCrop.setEnabled(enable);
        btnDone.setEnabled(enable);
        btnRotate.setEnabled(enable);
        btnUndo.setEnabled(enable);
    }

    @Override
    public void enableUndo(boolean enable) {
        btnCrop.setEnabled(!enable);
        btnUndo.setEnabled(enable);
    }

    @Override
    public void makeCroppedImageViewVisible(boolean isVisible) {
        if (isVisible) {
            profilePicture.setImageBitmap(BitmapUtil.resizeImage(cropImageView.getCroppedImage(), width, height, true));
        } else {
            profilePicture.setImageBitmap(null);
        }

        cropImageView.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        profilePicture.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void saveProfilePicture() {
        String path = getFilesDir().getPath() + "/Pictures/Profile Picture/ProfilePicture.png";

        PreferencesManager.putString(getString(R.string.profile_picture), path);

        iProfilePictureSettingsPresenter.saveProfilePicture(cropImageView.getCroppedImage(), path);
    }

    @Override
    public void closeActivity() {
        onBackPressed();
    }

    @Override
    public boolean hasPermission(String permission) {
        return appPermissions.hasPermission(permission);
    }

    @Override
    public void hideBottomSheet() {
        bottomSheet.dismiss();
    }

    @Override
    public void setupViews() {
        String profilePicture = PreferencesManager.getString(getString(R.string.profile_picture));

        if (profilePicture.length() != 0) {
            Bitmap background = BitmapUtil.getImage(profilePicture);

            showCurrentProfilePicture(BitmapUtil.resizeImage(background, width, height, true));
        }

        cropImageView.setMaxZoom(100);

        bottomSheet = new BottomSheet.Builder(this, R.style.BottomSheetStyle)
                .setSheet(R.menu.bottom_sheet_picture_picker)
                .setTitle(R.string.select_source)
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
                        SystemUtil.hideSystemUI(getActivity());
                    }
                })
                .create();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showMessage(String message, boolean showInLog) {
        ToastUtil.show(message);

        if (showInLog) {
            Log.d(TAG(), message);
        }
    }

    @OnClick({R.id.btnAdd, R.id.btnRotate, R.id.btnCrop, R.id.btnUndo, R.id.btnDone})
    public void onClick(View view) {
        iProfilePictureSettingsPresenter.onClick(view.getId());
    }

    public String TAG() {
        return this.getClass().getCanonicalName();
    }
}

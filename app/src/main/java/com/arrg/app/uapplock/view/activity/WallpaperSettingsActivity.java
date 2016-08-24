package com.arrg.app.uapplock.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
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
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.fingerlinks.mobile.android.navigator.Navigator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WallpaperSettingsActivity extends UAppLockActivity implements ColorChooserDialog.ColorCallback, PictureSettingsView {

    @BindView(R.id.container)
    AppCompatImageView container;
    @BindView(R.id.cropImageView)
    CropImageView cropImageView;
    @BindView(R.id.wallpaperPicture)
    AppCompatImageView wallpaperPicture;
    @BindView(R.id.seekBar)
    AppCompatSeekBar seekBar;
    @BindView(R.id.btnAdd)
    AppCompatImageButton btnAdd;
    @BindView(R.id.btnBlur)
    AppCompatImageButton btnBlur;
    @BindView(R.id.btnRotate)
    AppCompatImageButton btnRotate;
    @BindView(R.id.btnCrop)
    AppCompatImageButton btnCrop;
    @BindView(R.id.btnUndo)
    AppCompatImageButton btnUndo;
    @BindView(R.id.btnDone)
    AppCompatImageButton btnDone;
    /*@BindView(R.id.btnClose)
    AppCompatImageButton btnClose;*/
    @BindView(R.id.blurView)
    BlurView blurView;
    @BindView(R.id.blurSeekBarView)
    BlurView blurSeekBarView;
    @BindView(R.id.buttonBarContainer)
    LinearLayout buttonBarContainer;

    public static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final float SCALE = 0.0625f;
    public static final int STORAGE_PERMISSION_RC = 100;
    private int height;
    private int width;
    private AppPermissions appPermissions;
    private Bitmap original;
    private Boolean isBlurEffectEnabled = false;
    private BottomSheet bottomSheet;
    private Integer selectedColor;
    private IPictureSettingsPresenter iPictureSettingsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_settings);
        ButterKnife.bind(this);
        Util.modifyToolbar(this, R.string.title_activity_wallpaper_settings, true);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        final float radius = 25.0f;
        final View decorView = getWindow().getDecorView();
        final View rootView = decorView.findViewById(android.R.id.content);
        final Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(this, true))
                .blurRadius(radius);

        blurSeekBarView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(this, true))
                .blurRadius(radius);

        appPermissions = new AppPermissions(this);

        iPictureSettingsPresenter = new IPictureSettingsPresenter(this);
        iPictureSettingsPresenter.onCreate();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
    public void onBackPressed() {
        if (isBlurEffectEnabled) {
            hideSeekBar();
        } else {
            super.onBackPressed();
        }
    }

    private void hideSeekBar() {
        isBlurEffectEnabled = false;

        blurView.setVisibility(View.VISIBLE);
        blurSeekBarView.setVisibility(View.INVISIBLE);
    }

    private void showSeekBar() {
        isBlurEffectEnabled = true;

        blurView.setVisibility(View.INVISIBLE);
        blurSeekBarView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        iPictureSettingsPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        iPictureSettingsPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            SystemUtil.hideSystemUI(this);
        }
    }

    @Override
    public void setupViews() {
        btnUndo.setEnabled(false);

        hideSeekBar();

        String wallpaper = PreferencesManager.getString(getString(R.string.wallpaper));

        if (wallpaper.length() != 0) {
            if (PreferencesManager.getBoolean(getString(R.string.color_as_background))) {
                int backgroundColor = Integer.parseInt(wallpaper);

                getWindow().getDecorView().setBackgroundColor(backgroundColor);
            } else {
                Bitmap background = BitmapUtil.getImage(wallpaper);

                showCurrentProfilePicture(BitmapUtil.resizeImage(background, width, height, true));
            }
        }

        cropImageView.setMaxZoom(100);

        bottomSheet = new BottomSheet.Builder(this, R.style.BottomSheetStyle)
                .setSheet(R.menu.bottom_sheet_background_picker)
                .grid()
                .setTitle(R.string.select_source)
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet) {

                    }

                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
                        iPictureSettingsPresenter.handleOnItemSelected(menuItem.getItemId());
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, int i) {
                        SystemUtil.hideSystemUI(getActivity());
                    }
                })
                .create();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i > 0 && i < 26) {
                    try {
                        cropImageView.setImageBitmap(BlurEffectUtil.blur(getActivity(), original, (float) i));
                    } catch (NullPointerException e) {
                        showMessage(e.getMessage(), true);

                        Navigator.with(getActivity()).utils().finishWithAnimation();
                    }
                } else {
                    cropImageView.setImageBitmap(original);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

    @Override
    public void showBottomSheet() {
        bottomSheet.show();
    }

    @Override
    public void hideBottomSheet() {
        bottomSheet.dismiss();
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        original = bitmap;

        container.setImageBitmap(BlurEffectUtil.blur(getApplicationContext(), bitmap, 25.0f, SCALE));
        cropImageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean hasPermission(String permission) {
        return appPermissions.hasPermission(permission);
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
        btnBlur.setEnabled(enable);
        btnCrop.setEnabled(enable);
        btnDone.setEnabled(enable);
        btnRotate.setEnabled(enable);
    }

    @Override
    public void enableUndo(boolean enable) {
        btnCrop.setEnabled(!enable);
        btnUndo.setEnabled(enable);
    }

    @Override
    public void makeCroppedImageViewVisible(boolean isVisible) {
        if (isVisible) {
            wallpaperPicture.setImageBitmap(BitmapUtil.resizeImage(cropImageView.getCroppedImage(), width, height, true));
        } else {
            wallpaperPicture.setImageBitmap(null);
        }

        cropImageView.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        wallpaperPicture.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void saveProfilePicture() {
        String path = getFilesDir().getPath() + "/Pictures/Wallpaper/Wallpaper.png";

        PreferencesManager.putBoolean(getString(R.string.color_as_background), false);

        PreferencesManager.putString(getString(R.string.wallpaper), path);

        iPictureSettingsPresenter.saveProfilePicture(cropImageView.getCroppedImage(), path);
    }

    @Override
    public void closeActivity() {
        onBackPressed();
    }

    @Override
    public boolean haveNavigationBar() {
        int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");

        return !(id > 0 && getResources().getBoolean(id));
    }

    @Override
    public void showColorDialog() {
        new ColorChooserDialog.Builder(WallpaperSettingsActivity.this, R.string.color)
                .doneButton(R.string.md_done_label)
                .cancelButton(R.string.md_cancel_label)
                .backButton(R.string.md_back_label)
                .show();
    }

    @OnClick({R.id.btnAdd, R.id.btnBlur, R.id.btnRotate, R.id.btnCrop, R.id.btnUndo, R.id.btnDone, R.id.btnCloseSeekBar/*, R.id.btnClose*/})
    public void onClick(View view) {
        iPictureSettingsPresenter.onClick(view.getId());

        switch (view.getId()) {
            case R.id.btnBlur:
                if (isBlurEffectEnabled) {
                    hideSeekBar();
                } else {
                    showSeekBar();
                }
                break;
            case R.id.btnCloseSeekBar:
                hideSeekBar();
                break;
        }
    }

    public void showCurrentProfilePicture(Bitmap bitmap) {
        container.setImageBitmap(BlurEffectUtil.blur(getApplicationContext(), bitmap, 25.0f, SCALE));

        wallpaperPicture.setImageBitmap(bitmap);

        cropImageView.setVisibility(View.INVISIBLE);

        wallpaperPicture.setVisibility(View.VISIBLE);
    }

    public String TAG() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        getWindow().getDecorView().setBackgroundColor(selectedColor);

        PreferencesManager.putBoolean(getString(R.string.color_as_background), true);

        PreferencesManager.putString(getString(R.string.wallpaper), String.valueOf(selectedColor));

        ToastUtil.show(getString(R.string.done));
    }
}

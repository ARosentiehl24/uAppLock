package com.arrg.app.uapplock.model.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.arrg.app.uapplock.UAppLock;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class App implements Serializable, Parcelable {

    private Boolean isChecked = false;
    private Drawable appIcon;
    private String appName;
    private String appPackage;

    public App() {

    }

    public App(Drawable appIcon, String appName, String appPackage) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.appPackage = appPackage;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isChecked);

        Bitmap bitmap = ((BitmapDrawable) appIcon).getBitmap();

        if (bitmap != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            if (success) {
                dest.writeInt(byteStream.toByteArray().length);
                dest.writeByteArray(byteStream.toByteArray());
            }
        }

        dest.writeString(this.appName);
        dest.writeString(this.appPackage);
    }

    protected App(Parcel in) {
        this.isChecked = (Boolean) in.readValue(Boolean.class.getClassLoader());

        byte[] bytes = new byte[in.readInt()];
        in.readByteArray(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        this.appIcon = new BitmapDrawable(UAppLock.uAppLock.getResources(), bitmap);
        this.appName = in.readString();
        this.appPackage = in.readString();
    }

    public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() {
        @Override
        public App createFromParcel(Parcel source) {
            return new App(source);
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };
}

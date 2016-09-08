package com.arrg.app.uapplock.util;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.arrg.app.uapplock.R;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class Util {

    public static void modifyToolbar(AppCompatActivity activity, Boolean displayHome) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {

            Drawable leftArrow;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(activity, R.color.colorAccent)));

                leftArrow = MaterialDrawableBuilder.with(activity)
                        .setIcon(MaterialDrawableBuilder.IconValue.CHEVRON_LEFT)
                        .setColor(ContextCompat.getColor(activity, R.color.clouds))
                        .setToActionbarSize()
                        .build();
            } else {
                leftArrow = MaterialDrawableBuilder.with(activity)
                        .setIcon(MaterialDrawableBuilder.IconValue.CHEVRON_LEFT)
                        .setColor(ContextCompat.getColor(activity, R.color.colorAccent))
                        .setToActionbarSize()
                        .build();
            }

            actionBar.setDisplayHomeAsUpEnabled(displayHome);

            if (displayHome) {
                actionBar.setHomeAsUpIndicator(leftArrow);
            }
        }
    }
}

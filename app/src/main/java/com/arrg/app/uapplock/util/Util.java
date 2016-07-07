package com.arrg.app.uapplock.util;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.arrg.app.uapplock.R;
import com.norbsoft.typefacehelper.ActionBarHelper;
import com.norbsoft.typefacehelper.TypefaceHelper;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class Util {

    public static void modifyToolbar(AppCompatActivity activity, Integer title, Boolean displayHome){
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            ActionBarHelper.setTitle(actionBar, TypefaceHelper.typeface(activity, title));

            Drawable leftArrow = MaterialDrawableBuilder.with(activity)
                    .setIcon(MaterialDrawableBuilder.IconValue.CHEVRON_LEFT)
                    .setColor(ContextCompat.getColor(activity, R.color.colorAccent))
                    .setToActionbarSize()
                    .build();

            actionBar.setDisplayHomeAsUpEnabled(displayHome);
            actionBar.setHomeAsUpIndicator(leftArrow);
        }
    }
}

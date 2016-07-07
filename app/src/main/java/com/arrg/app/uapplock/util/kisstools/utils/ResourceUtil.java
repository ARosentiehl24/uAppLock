/**
 *
 * Copyright (c) 2014 CoderKiss
 *
 * CoderKiss[AT]gmail.com
 *
 */

package com.arrg.app.uapplock.util.kisstools.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.arrg.app.uapplock.util.kisstools.KissTools;


public class ResourceUtil {

	public static String getString(int resId) {
		Context context = KissTools.getApplicationContext();
		if (context == null || resId <= 0) {
			return null;
		}
		try {
			return context.getString(resId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getString(int resId, Object... args) {
		Context context = KissTools.getApplicationContext();
		if (context == null || resId <= 0) {
			return null;
		}
		try {
			return context.getString(resId, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Drawable getDrawable(int resId) {
		Context context = KissTools.getApplicationContext();
		if (context == null || resId <= 0) {
			return null;
		}
		try {
			return context.getResources().getDrawable(resId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Integer getColor(int resId){
		Context context = KissTools.getApplicationContext();
		if (context == null || resId <= 0) {
			return null;
		}
		try {
			return ContextCompat.getColor(context, resId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

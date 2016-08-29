package com.arrg.app.uapplock.model.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.interfaces.LockScreenServiceView;
import com.arrg.app.uapplock.util.kisstools.utils.ResourceUtil;
import com.shawnlin.preferencesmanager.PreferencesManager;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Boolean isSwipeEnabled;
    private LockScreenServiceView lockScreenServiceView;

    public SwipeGestureDetector(LockScreenServiceView lockScreenServiceView) {
        this.isSwipeEnabled = PreferencesManager.getBoolean(ResourceUtil.getString(R.string.enable_swipe_on_lock_screen));
        this.lockScreenServiceView = lockScreenServiceView;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println(" in onFling() :: ");

        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
            return false;
        }

        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && isSwipeEnabled) {
            lockScreenServiceView.showNext();
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && isSwipeEnabled) {
            lockScreenServiceView.showPrevious();
        }

        return super.onFling(e1, e2, velocityX, velocityY);
    }
}

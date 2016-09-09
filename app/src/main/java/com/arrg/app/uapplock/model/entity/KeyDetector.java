package com.arrg.app.uapplock.model.entity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class KeyDetector {

    public interface OnKeyPressedListener {
        void onHomePressed();

        void onMenuPressed();
    }

    static String TAG;
    private Context context;
    private IntentFilter intentFilter;
    private KeyReceiver keyReceiver;
    private OnKeyPressedListener onKeyPressedListener;

    public KeyDetector(Context context) {
        TAG = getClass().getSimpleName();

        this.context = context;
        this.intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public void setOnKeyPressedListener(OnKeyPressedListener listener) {
        onKeyPressedListener = listener;
        keyReceiver = new KeyReceiver();
    }

    public void startWatch() {
        if (keyReceiver != null) {
            context.registerReceiver(keyReceiver, intentFilter);
        }
    }

    public void stopWatch() {
        if (keyReceiver != null) {
            context.unregisterReceiver(keyReceiver);
        }
    }

    class KeyReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.e(TAG, "Action: " + action);

            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                Log.e(TAG, "Reason: " + reason);

                if (reason != null) {
                    if (onKeyPressedListener != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            onKeyPressedListener.onHomePressed();
                        } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            onKeyPressedListener.onMenuPressed();
                        }
                    }
                }
            }
        }
    }
}

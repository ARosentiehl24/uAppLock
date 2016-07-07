package com.arrg.app.uapplock.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arrg.app.uapplock.presenter.IAppListFragmentPresenter;
import com.arrg.app.uapplock.util.kisstools.utils.LogUtil;

public class PackageReceiver extends BroadcastReceiver {

    private IAppListFragmentPresenter iAppListFragmentPresenter;

    public PackageReceiver(IAppListFragmentPresenter iAppListFragmentPresenter) {
        this.iAppListFragmentPresenter = iAppListFragmentPresenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String appPackage = intent.getData().getEncodedSchemeSpecificPart();

        switch (intent.getAction()) {
            case Intent.ACTION_PACKAGE_ADDED:
                iAppListFragmentPresenter.add(appPackage);

                LogUtil.e("PackageReceiver", "Add: " + appPackage);
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                iAppListFragmentPresenter.remove(appPackage);

                LogUtil.e("PackageReceiver", "Remove: " + appPackage);
                break;
        }
    }
}

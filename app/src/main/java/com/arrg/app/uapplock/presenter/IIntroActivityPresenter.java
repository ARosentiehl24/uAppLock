package com.arrg.app.uapplock.presenter;

import com.arrg.app.uapplock.interfaces.IntroActivityPresenter;
import com.arrg.app.uapplock.interfaces.IntroActivityView;

public class IIntroActivityPresenter implements IntroActivityPresenter {

    private IntroActivityView introActivityView;

    public IIntroActivityPresenter(IntroActivityView introActivityView) {
        this.introActivityView = introActivityView;
    }

    @Override
    public void onCreate() {
        introActivityView.setupViews();
    }
}

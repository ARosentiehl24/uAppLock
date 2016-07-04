package com.arrg.app.uapplock.presenter;

import com.arrg.app.uapplock.R;
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
        introActivityView.hidePrevious();
    }

    @Override
    public void onButtonBarClick(int id) {
        switch (id) {
            case R.id.btnPrevious:
                introActivityView.previous();
                break;
            case R.id.btnNext:
                introActivityView.next();
                break;
        }
    }

    @Override
    public void configureTheViewsAccordingPageSelected(int position, int totalPages) {
        if (position > 0) {
            introActivityView.showPrevious();
        } else {
            introActivityView.hidePrevious();
        }
    }
}

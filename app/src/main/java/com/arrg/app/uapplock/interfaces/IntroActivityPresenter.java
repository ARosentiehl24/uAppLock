package com.arrg.app.uapplock.interfaces;

public interface IntroActivityPresenter {

    void onCreate();
    void onButtonBarClick(int id);
    void configureTheViewsAccordingPageSelected(int position, int totalPages);
}

package com.arrg.app.uapplock.interfaces;

public interface IntroActivityView {

    void setupViews();
    void next();
    void previous();
    void hideNext();
    void hidePrevious();
    void showNext();
    void showPrevious();
    void hideAux();
    void showAux();
    boolean isHardwareDetected();
    boolean hasEnrolledFingerprints();
}

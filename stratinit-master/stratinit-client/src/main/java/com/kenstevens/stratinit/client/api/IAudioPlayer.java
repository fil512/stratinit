package com.kenstevens.stratinit.client.api;

public interface IAudioPlayer {
    void playHit();

    void playExplosion();

    void playIntro();

    void playFinishedLoading();

    void playEmpty();

    void playRedAlert();

    void playIDied();

    void playFanfare();

    void shutdown();
}

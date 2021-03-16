package com.kenstevens.stratinit.client.audio;

// FIXME rename and move to shell
public interface WavPlayer {
    void playHit();

    void playExplosion();

    // FIXME rename
    void playIntro();

    void playFinishedLoading();

    void playEmpty();

    void playRedAlert();

    void playIDied();

    void playFanfare();

    void shutdown();
}

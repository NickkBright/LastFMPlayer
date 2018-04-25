package com.nickkbright.lastfmplayer;

import android.app.Application;

import com.ag.lfm.Lfm;

public class LastFMPlayer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Lfm.initializeWithSecret(this);
    }
}

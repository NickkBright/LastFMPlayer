package com.nickkbright.lastfmplayer.api;

import com.ag.lfm.LfmParameters;
import com.ag.lfm.LfmRequest;
import com.ag.lfm.api.methods.ApiBase;

public class LfmApiArtist extends ApiBase {

    public LfmRequest getInfo(LfmParameters params) {
        return prepareRequest("getInfo", params, false);
    }


    public LfmRequest getTopAlbums(LfmParameters params) {
        return prepareRequest("getTopAlbums", params, false);
    }

    public LfmRequest getSimilar(LfmParameters params) {
        return prepareRequest("getSimilar", params, false);
    }


    @Override
    protected String getMethodsGroup() {
        return "artist";
    }
}

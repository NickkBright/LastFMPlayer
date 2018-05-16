package com.nickkbright.lastfmplayer.models;

public class RecentTrack {
    private String trackInfo;
    private String date;
    private String imageURL;

    public RecentTrack(String trackInfo, String date, String imageURL) {
        this.trackInfo = trackInfo;
        this.date = date;
        this.imageURL = imageURL;
    }


    public String getTrackInfo() {
        return trackInfo;
    }

    public String getDate() {
        return date;
    }

    public void setDate (String date) {
        this.date = date;
    }

    public void setTrackInfo(String trackInfo) {
        this.trackInfo = trackInfo;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}

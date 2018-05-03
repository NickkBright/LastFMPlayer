package com.nickkbright.lastfmplayer.models;

public class LfmTrack {

    private String trackName;
    private String duration;
    private String imageURL;

    public LfmTrack(String trackName, String duration, String imageURL) {
        this.trackName = trackName;
        this.duration = duration;
        this.imageURL = imageURL;
    }


    public String getTrack() {
        return trackName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setTrack(String trackName) {
        this.trackName = trackName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
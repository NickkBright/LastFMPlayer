package com.nickkbright.lastfmplayer.models;

public class LfmTrack {

    private String artist,track;
    private String imageURL;

    public LfmTrack() {
    }

    public LfmTrack(String artist, String track, String imageURL) {
        this.artist = artist;
        this.track = track;
        this.imageURL = imageURL;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
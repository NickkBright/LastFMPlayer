package com.nickkbright.lastfmplayer.models;

public class GridItem {
    private String name;
    private String artistName;
    private String playcount;
    private String imageURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getPlaycount() {
        return playcount;
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public GridItem(String name, String playcount, String imageURL, String artistName) {
        super();
        this.name = name;
        this.playcount = playcount;
        this.imageURL = imageURL;
        this.artistName = artistName;
    }

    public GridItem(String name, String playcount, String imageURL) {
        super();
        this.name = name;
        this.playcount = playcount;
        this.imageURL = imageURL;
    }
}

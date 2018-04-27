package com.nickkbright.lastfmplayer.models;

public class GridItem {
    private String name;
    private String imageURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public GridItem(String name, String imageURL) {
        super();
        this.name = name;
    }
}

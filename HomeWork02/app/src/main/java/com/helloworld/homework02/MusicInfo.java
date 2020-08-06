package com.helloworld.homework02;

import java.io.Serializable;

public class MusicInfo implements Serializable {
    String track_name, genre, album, artist, release_date, trackUrl;
    double track_price, collection_price;

    @Override
    public String toString() {
        return "MusicInfo{" +
                "track_name='" + track_name + '\'' +
                ", genre='" + genre + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", release_date='" + release_date + '\'' +
                ", track_price=" + track_price +
                ", collection_price=" + collection_price +
                '}';
    }
}

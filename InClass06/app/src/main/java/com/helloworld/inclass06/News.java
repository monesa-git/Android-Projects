package com.helloworld.inclass06;


public class News {

    String title;
    String publishedAt;
    String url;
    String description;

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

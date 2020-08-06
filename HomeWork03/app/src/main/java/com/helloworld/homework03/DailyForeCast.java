package com.helloworld.homework03;

public class DailyForeCast {

    String dayDetail, nightDetail, minTemp, maxTemp, mobileLink, date, detail;
    int index;
    String dayImage, nightImage;

    @Override
    public String toString() {
        return "DailyForeCast{" +
                "dayDetail='" + dayDetail + '\'' +
                ", nightDetail='" + nightDetail + '\'' +
                ", minTemp='" + minTemp + '\'' +
                ", maxTemp='" + maxTemp + '\'' +
                ", mobileLink='" + mobileLink + '\'' +
                ", date='" + date + '\'' +
                ", detail='" + detail + '\'' +
                ", index=" + index +
                ", dayImage='" + dayImage + '\'' +
                ", nightImage='" + nightImage + '\'' +
                '}';
    }
}

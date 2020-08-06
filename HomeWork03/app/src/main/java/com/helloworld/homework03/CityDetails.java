package com.helloworld.homework03;

public class CityDetails {
    String cityName;
    String countryName;
    String stateName;
    int cityKey;

    @Override
    public String toString() {
        return "CityDetails{" +
                "cityName='" + cityName + '\'' +
                ", countryName='" + countryName + '\'' +
                ", stateName='" + stateName + '\'' +
                ", key='" + cityKey + '\'' +
                '}';
    }
}



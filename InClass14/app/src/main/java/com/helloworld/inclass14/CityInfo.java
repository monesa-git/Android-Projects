package com.helloworld.inclass14;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class CityInfo implements Serializable {
    String name, stateName, placeId;
    String tripName;
    String latitude, longitude;
    ArrayList<PlacesDetails> placesDetailsArrayList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public ArrayList<PlacesDetails> getPlacesDetailsArrayList() {
        return placesDetailsArrayList;
    }

    public void setPlacesDetailsArrayList(ArrayList<PlacesDetails> placesDetailsArrayList) {
        this.placesDetailsArrayList = placesDetailsArrayList;
    }

    @Override
    public String toString() {
        return "CityInfo{" +
                "name='" + name + '\'' +
                ", stateName='" + stateName + '\'' +
                ", placeId='" + placeId + '\'' +
                ", tripName='" + tripName + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", placesDetailsArrayList=" + placesDetailsArrayList +
                '}';
    }
//
//    public HashMap<Integer, Object> toHashMap(){
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("integer",this.getName());
//        hashMap.put("stateName",this.getStateName());
//        hashMap.put("placeId",this.placeId());
//        return hashMap;
//    }
}

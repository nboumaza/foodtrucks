package com.nboumaza.foodies.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FoodTruck {

    private long locationid;
    private String applicant;
    private double latitude;
    private double longitude;
    private String fooditems;


    @JsonCreator
    public FoodTruck(
            @JsonProperty("locationid") long locationid,
            @JsonProperty("applicant") String applicant,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("fooditems") String fooditems) {

        this.locationid = locationid;
        this.applicant = applicant;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fooditems = fooditems;
    }

    public FoodTruck() {
    }

    public long getLocationid() {
        return locationid;
    }

    public void setLocationid(long locationid) {
        this.locationid = locationid;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFooditems() {
        return fooditems;
    }

    public void setFooditems(String fooditems) {
        this.fooditems = fooditems;
    }

    @Override
    public String toString() {
        return "FoodTruck{" +
                "locationid =" + locationid +
                ", applicant='" + applicant + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", fooditems='" + fooditems + '\'' +
                '}';
    }

}



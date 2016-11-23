package com.prestech.myfirebase;

/**
 * Created by asohm on 11/21/2016.
 */

public class Trip
{

    /*
    Trip's attributes
     */
    private String tripId;
    private String startTime;
    private String endTime;
    private String distance;
    private String totalTime;
    private String startOdometer;
    private String endOdometer;


    /***********************************************
     *Class GETTERS and SETTERS
     */
    public String getTripId()
    {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getEndOdometer() {
        return endOdometer;
    }

    public void setEndOdometer(String endOdometer) {
        this.endOdometer = endOdometer;
    }

    public String getStartOdometer() {
        return startOdometer;
    }

    public void setStartOdometer(String startOdometer) {
        this.startOdometer = startOdometer;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    /**********END of GEETERS AND SETTERS*******************     */
}

package TrackitDataObjects;

import java.util.HashMap;
import java.util.Map;

/**
 * The Trip Class is designed to hold all the information for a trip and be the object that
 * the MapActivity interface updates in order to keep track of a Driver initiated trip.
 * Additionally, it can be used to contain the data about a previously recorded trip.

 */

public class Trip
{

    /*
    Trip's attributes
     */
    private String startTime;
    private String endTime;
    private double distance;
    private String totalTime;


    /***********************************************
     *Class GETTERS and SETTERS
     */
    public Trip()
    {

    }

    public Trip(String startTime, double distance, String totalTime)
    {
        this.startTime = startTime;
        this.distance = 0;
        this.totalTime = totalTime;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    /**********END of GEETERS AND SETTERS*******************     */


}

package TrackitDataObjects;

/**
 * The Car Model is designed to contain all
 * the information for an individual car.
 */

public class Car {

    private String odometer;
    private String vinNumber;
    private String make;
    private String model;
    private String year;
    private String tag;



    /*
     no argument constructor initializes the
     default values for global variables
     */
    public  Car()
    {
        vinNumber = null;
        odometer = null;
    }

    /***************************************************************
    * CLASS GETTER AND SETTER METHODS
    */
    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getOdometer() {
        return odometer;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    /*******END OF GETTTERS AND SETTERS********/
}

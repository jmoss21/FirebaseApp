package TrackitDataObjects;

/**
 * Created by asohm on 11/21/2016.
 */
public class Driver
{
    private  String username, driverId, email;

    /******************************************************
      NO Argument constructor
     */
    Driver()
    {

    }//driver() Ends


    /*******************************************************
     Constructor with parameters
     */
    public Driver (String username, String email) {
        this.username = username;
        this.email = email;
    }//Driver(String, String) Ends


    /*******************************************************
     * Class Getter and Setter methods
     * @return
     */
    public String getDriverId() {
        return driverId;
    }

    public void setUserId(String driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    /************END OF GETTERS AND SETTERS***************/


}//Drivere Class Ends
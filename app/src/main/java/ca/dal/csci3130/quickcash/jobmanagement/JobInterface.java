package ca.dal.csci3130.quickcash.jobmanagement;

import com.google.android.gms.maps.model.LatLng;

public interface JobInterface {
    String getJobTitle();

    void setJobTitle(String jobTitle);

    String getJobType();

    void setJobType(String jobType);

    String getDescription();

    void setDescription(String description);

    // duration of job in weeks
    int getDuration();

    void setDuration(int duration);

    double getPayRate();

    void setPayRate(double payRate);

    String getJobID();

    void setJobID(String jobID);

    double getLatitude();

    void setLatitude(double latitude);

    double getLongitude();

    void setLongitude(double longitude);
}
